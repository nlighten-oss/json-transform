package co.nlighten.jsontransform.template;

import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.ParameterResolver;
import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Suriel
 * A string template engine
 */
public class TextTemplate {
    private final static Map<String, TextTemplate> cached = new HashMap<>();
    private final static int STATE_TEXT = 0;
    private final static int STATE_PARAM_NAME = 1;
    private final static int STATE_PARAM_DEFAULT = 2;
    private final static Pattern UNESCAPED_OPEN_CURLY_BRACKET = Pattern.compile("(?s)(?<!\\\\)\\{");
    private final LinkedList<TemplateParameter> templateParameters = new LinkedList<>();

    private final ParameterDefaultResolveOptions defaultResolver;
    /**
     * internal list of values
     */
    private final LinkedList<Object> values = new LinkedList<>();

    /**
     * Creates a new memory template for a Text
     *
     * @param template The template text
     */
    public TextTemplate(String template) {
        this(template, ParameterDefaultResolveOptions.UNIQUE);
    }

    /**
     * Creates a new memory template for a string
     *
     * @param template         The template text
     * @param defaultResolver defines how the template should resolve parameter default values
     */
    public TextTemplate(String template, ParameterDefaultResolveOptions defaultResolver) {
        this.defaultResolver = defaultResolver;
        parse(template, defaultResolver);
    }

    /**
     * Exposes a read only list to inspect the list of parameters
     *
     * @return a list of parameters in the template
     */
    public List<TemplateParameter> getParameters() {
        return Collections.unmodifiableList(templateParameters);
    }

    /**
     * gets or creates a template from the cache
     *
     * @param template         the command to parse
     * @param defaultResolver defines how the template should resolve parameter default values
     * @return a new text template
     */
    public static TextTemplate get(String template, ParameterDefaultResolveOptions defaultResolver) {
        TextTemplate tpl;
        var key = ParameterDefaultResolveOptions.UNIQUE.equals(defaultResolver)
                ? template // the common case
                : defaultResolver.toString() + "{" + template ;
        synchronized (cached) {
            tpl = cached.get(key);
        }
        if (tpl == null) {
            tpl = new TextTemplate(template, defaultResolver);
            synchronized (cached) {
                cached.put(key, tpl);
            }
        }
        return tpl;
    }

    public static TextTemplate get(String template) {
        return get(template, ParameterDefaultResolveOptions.UNIQUE);
    }

    private void parse(String template, ParameterDefaultResolveOptions defaultResolver) {
        int curleyOpen = 0;
        var buffer = new StringBuilder(template.length());
        var state = 0;
        //internal storage for found params
        var templateParams = new HashMap<String, TemplateParameter>();
        String pName = null, pValue = null;

        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);
            switch (c) {
                case '\\':
                    if (i + 1 < template.length()) {
                        var nextChar = template.charAt(i + 1);
                        if (nextChar == '{') {
                            i++; // skip backslash
                            buffer.append("\\{");
                            continue;
                        }
                    }
                    break;
                case '{':
                    curleyOpen++;
                    if (state == STATE_TEXT) {
                        //start of parameter flush old
                        if (!buffer.isEmpty()) {
                            values.addLast(buffer.toString());
                            buffer.delete(0, buffer.length());
                        }
                        state = STATE_PARAM_NAME;
                        //we don't need to add to buffer
                        continue;
                    }
                    break;
                case '}':
                    // if there is no open bracket, then treat it as a regular character
                    if (curleyOpen > 0) {
                        curleyOpen--;

                        if (curleyOpen == 0) {
                            // close off
                            switch (state) {
                                case STATE_PARAM_NAME:
                                    pName = buffer.toString().replace("\\{", "{");
                                    break;
                                case STATE_PARAM_DEFAULT:
                                    pValue = buffer.toString();
                                    break;
                            }
                            buffer.delete(0, buffer.length());

                            state = STATE_TEXT;
                            if (!pName.isEmpty()) {
                                TemplateParameter tParam;
                                if (defaultResolver != ParameterDefaultResolveOptions.UNIQUE && templateParams.containsKey(pName)) {
                                    tParam = templateParams.get(pName);
                                    if (pValue != null) {
                                        if (tParam.getDefault() == null || defaultResolver == ParameterDefaultResolveOptions.LAST_VALUE) {
                                            tParam.setDefault(pValue);
                                        }
                                    }
                                } else {
                                    tParam = new TemplateParameter(pName, pValue);
                                    templateParams.put(pName, tParam);
                                }
                                templateParameters.add(tParam);
                                values.addLast(tParam);
                            }
                            pName = pValue = null;
                            continue;
                        }
                    }
                    break;
                case ',':
                    if (state == STATE_PARAM_NAME) {
                        state = STATE_PARAM_DEFAULT;
                        pName = buffer.toString();
                        buffer.delete(0, buffer.length());
                        continue;
                    }
            }
            // must be a valid char
            buffer.append(c);
        }
        if (!buffer.isEmpty())
            values.add(buffer.toString());
    }

    String internalRender(ParameterResolver resolver, JsonAdapter<?,?,?> adapter) {
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            if (value instanceof String s) {
                sb.append(s);
            } else if (value instanceof TemplateParameter param) {
                // escape param value
                sb.append(param.getStringValue(resolver, adapter));
            }
        }
        return sb.toString();
    }

    /**
     * Renders the template after inserting the parameters
     *
     * @param resolver A resolver to extract parameter values
     * @return a string with its parameters replaced
     */
    public String render(ParameterResolver resolver, JsonAdapter<?,?,?> adapter) {
        var res = internalRender(resolver, adapter);
        while (UNESCAPED_OPEN_CURLY_BRACKET.matcher(res).find()) {
            res = get(res, defaultResolver).internalRender(resolver, adapter);
        }
        // unescape
        return res.replace("\\{", "{").replace("\\}", "}");
    }

    public String render(ParameterResolver resolver) {
        return render(resolver, JsonTransformerConfiguration.get().getAdapter());
    }

    public static Map<String, String> mapOf(String... parameters){
        if (parameters != null && (parameters.length % 2) != 0) {
            throw new VerifyError("Parameters must by an even array built as key value pairs");
        }
        Map<String, String> paramsMap = new HashMap<>();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i += 2) {
                paramsMap.put(parameters[i], parameters[i + 1]);
            }
        }
        return Collections.unmodifiableMap(paramsMap);
    }

    /**
     * Renders the template after inserting the parameters
     *
     * @param parameters An even array of parameters built as name value pairs
     * @return a string with its parameters replaced
     */
    public String render(String... parameters) {
        return render(mapOf(parameters));
    }

    /**
     * Renders the template after inserting the parameters
     *
     * @param resolver A map with the parameter names as keys
     * @return a string with its parameters replaced
     */
    public String render(Map<String, ?> resolver) {
        return render(ParameterResolver.fromMap(resolver));
    }

    public static String render(String template, ParameterResolver resolver, ParameterDefaultResolveOptions defaultResolver) {
        return get(template, defaultResolver).render(resolver);
    }

    public static String render(String template, Map<String, ?> resolver, ParameterDefaultResolveOptions defaultResolver) {
        return render(template, ParameterResolver.fromMap(resolver), defaultResolver);
    }

    public static String render(String template, ParameterResolver resolver) {
        return render(template, resolver, ParameterDefaultResolveOptions.UNIQUE);
    }

    public static String render(String template, Map<String, ?> resolver) {
        return render(template, ParameterResolver.fromMap(resolver), ParameterDefaultResolveOptions.UNIQUE);
    }

}
