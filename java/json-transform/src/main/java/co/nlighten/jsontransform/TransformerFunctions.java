package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.*;
import co.nlighten.jsontransform.functions.common.InlineFunctionContext;
import co.nlighten.jsontransform.functions.common.ObjectFunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TransformerFunctions<JE, JA extends Iterable<JE>, JO extends JE> implements TransformerFunctionsAdapter<JE, JA, JO>{
    static final Logger log = LoggerFactory.getLogger(TransformerFunctions.class);

    private static final Pattern inlineFunctionRegex = Pattern.compile("^\\$\\$(\\w+)(\\((.*?)\\))?(:|$)");
    private static final Pattern inlineFunctionArgsRegex = Pattern.compile("('(\\\\'|[^'])*'|[^,]*)(?:,|$)");
    public static final String FUNCTION_KEY_PREFIX = "$$";
    public static final String QUOTE_APOS = "'";
    public static final String ESCAPE_DOLLAR = "\\$";
    public static final String ESCAPE_HASH = "\\#";


    private Map<String, TransformerFunction<JE, JA, JO>> functions = Map.of();
    private final JsonAdapter<JE, JA, JO> jsonAdapter;

    public TransformerFunctions(JsonAdapter<JE, JA, JO> adapter) {
        this.jsonAdapter = adapter;
        registerFunctions(
                Map.entry("and", new TransformerFunctionAnd<>(adapter)),
                Map.entry("at",new TransformerFunctionAt<>(adapter)),
                Map.entry("avg",new TransformerFunctionAvg<>(adapter)),
                Map.entry("base64", new TransformerFunctionBase64<>(adapter)),
                Map.entry("boolean", new TransformerFunctionBoolean<>(adapter)),
                Map.entry("coalesce", new TransformerFunctionCoalesce<>(adapter)),
                Map.entry("concat", new TransformerFunctionConcat<>(adapter)),
                Map.entry("contains", new TransformerFunctionContains<>(adapter)),
                Map.entry("csv", new TransformerFunctionCsv<>(adapter)),
                Map.entry("csvparse", new TransformerFunctionCsvParse<>(adapter)),
                Map.entry("date", new TransformerFunctionDate<>(adapter)),
                Map.entry("decimal", new TransformerFunctionDecimal<>(adapter)),
                Map.entry("digest", new TransformerFunctionDigest<>(adapter)),
                Map.entry("distinct", new TransformerFunctionDistinct<>(adapter)),
                Map.entry("entries", new TransformerFunctionEntries<>(adapter)),
                Map.entry("eval",new TransformerFunctionEval<>(adapter)),
                Map.entry("filter", new TransformerFunctionFilter<>(adapter)),
                Map.entry("find", new TransformerFunctionFind<>(adapter)),
                Map.entry("first", new TransformerFunctionCoalesce<>(adapter)), // * alias for coalesce
                Map.entry("flat", new TransformerFunctionFlat<>(adapter)),
                Map.entry("flatten",new TransformerFunctionFlatten<>(adapter)),
                Map.entry("form", new TransformerFunctionForm<>(adapter)),
                Map.entry("formparse", new TransformerFunctionFormParse<>(adapter)),
                Map.entry("group", new TransformerFunctionGroup<>(adapter)),
                Map.entry("if", new TransformerFunctionIf<>(adapter)),
                Map.entry("is", new TransformerFunctionIs<>(adapter)),
                Map.entry("isnull", new TransformerFunctionIsNull<>(adapter)),
                Map.entry("join", new TransformerFunctionJoin<>(adapter)),
                Map.entry("json", new TransformerFunctionJsonParse<>(adapter)),
                Map.entry("jsonparse", new TransformerFunctionJsonParse<>(adapter)),
                Map.entry("jsonpatch", new TransformerFunctionJsonPatch<>(adapter)),
                Map.entry("jsonpath", new TransformerFunctionJsonPath<>(adapter)),
                Map.entry("jsonpointer", new TransformerFunctionJsonPointer<>(adapter)),
                Map.entry("jwtparse", new TransformerFunctionJwtParse<>(adapter)),
                Map.entry("length", new TransformerFunctionLength<>(adapter)),
                Map.entry("long", new TransformerFunctionLong<>(adapter)),
                Map.entry("lookup", new TransformerFunctionLookup<>(adapter)),
                Map.entry("lower", new TransformerFunctionLower<>(adapter)),
                Map.entry("map", new TransformerFunctionMap<>(adapter)),
                Map.entry("match", new TransformerFunctionMatch<>(adapter)),
                Map.entry("matchall", new TransformerFunctionMatchAll<>(adapter)),
                Map.entry("math", new TransformerFunctionMath<>(adapter)),
                Map.entry("max",new TransformerFunctionMax<>(adapter)),
                Map.entry("min",new TransformerFunctionMin<>(adapter)),
                Map.entry("normalize", new TransformerFunctionNormalize<>(adapter)),
                Map.entry("not", new TransformerFunctionNot<>(adapter)),
                Map.entry("numberformat", new TransformerFunctionNumberFormat<>(adapter)),
                Map.entry("numberparse", new TransformerFunctionNumberParse<>(adapter)),
                Map.entry("object", new TransformerFunctionObject<>(adapter)),
                Map.entry("or", new TransformerFunctionOr<>(adapter)),
                Map.entry("pad", new TransformerFunctionPad<>(adapter)),
                Map.entry("partition", new TransformerFunctionPartition<>(adapter)),
                Map.entry("range", new TransformerFunctionRange<>(adapter)),
                Map.entry("raw", new TransformerFunctionRaw<>(adapter)),
                Map.entry("reduce", new TransformerFunctionReduce<>(adapter)),
                Map.entry("replace", new TransformerFunctionReplace<>(adapter)),
                Map.entry("reverse", new TransformerFunctionReverse<>(adapter)),
                Map.entry("slice", new TransformerFunctionSlice<>(adapter)),
                Map.entry("sort", new TransformerFunctionSort<>(adapter)),
                Map.entry("split", new TransformerFunctionSplit<>(adapter)),
                Map.entry("string", new TransformerFunctionString<>(adapter)),
                Map.entry("substring", new TransformerFunctionSubstring<>(adapter)),
                Map.entry("sum",new TransformerFunctionSum<>(adapter)),
                Map.entry("switch", new TransformerFunctionSwitch<>(adapter)),
                Map.entry("test", new TransformerFunctionTest<>(adapter)),
                Map.entry("transform", new TransformerFunctionTransform<>(adapter)),
                Map.entry("trim", new TransformerFunctionTrim<>(adapter)),
                Map.entry("unflatten",new TransformerFunctionUnflatten<>(adapter)),
                Map.entry("upper", new TransformerFunctionUpper<>(adapter)),
                Map.entry("uriparse", new TransformerFunctionUriParse<>(adapter)),
                Map.entry("urldecode", new TransformerFunctionUrlDecode<>(adapter)),
                Map.entry("urlencode", new TransformerFunctionUrlEncode<>(adapter)),
                Map.entry("uuid", new TransformerFunctionUuid<>(adapter)),
                Map.entry("value",new TransformerFunctionValue<>(adapter)),
                Map.entry("wrap", new TransformerFunctionWrap<>(adapter)),
                Map.entry("xml", new TransformerFunctionXml<>(adapter)),
                Map.entry("xmlparse", new TransformerFunctionXmlParse<>(adapter)),
                Map.entry("xor", new TransformerFunctionXor<>(adapter)),
                Map.entry("yaml", new TransformerFunctionYaml<>(adapter)),
                Map.entry("yamlparse", new TransformerFunctionYamlParse<>(adapter))
        );
    }

    @SuppressWarnings("unchecked")
    public void registerFunctions(Map.Entry<String, TransformerFunction<JE, JA, JO>>... moreFunctions) {
        var additions = Arrays.stream(moreFunctions).filter(x -> {
            if (functions.containsKey(x.getKey())) {
                log.debug("Skipping registering function $${} (already exists)", x.getKey());
                return false;
            }
            return true;
        }).toList();
        if (additions.isEmpty()) {
            return;
        }
        log.info("Registering functions: $${}", String.join(", $$", additions.stream().map(
                Map.Entry::getKey).toList()));
        // create a new immutable map of both existing entries and new ones
        functions = Map.ofEntries(Stream.concat(functions.entrySet().stream(), additions.stream()).toArray(Map.Entry[]::new));
    }

    /**
     * Checks the context for a registered object function and returns the result if matched
     */
    public FunctionMatchResult<Object> matchObject(String path, JO definition, co.nlighten.jsontransform.ParameterResolver resolver, JsonTransformerFunction<JE> transformer) {
        if (definition == null) {
            return null;
        }
        // look for an object function
        // (precedence is all internal functions sorted alphabetically first, then client added ones second, by registration order)
        for (String key : functions.keySet()) {
            if (jsonAdapter.jObject.has(definition, FUNCTION_KEY_PREFIX + key)) {
                var func = functions.get(key);
                var context = new ObjectFunctionContext<>(
                        path,
                        definition,
                        jsonAdapter,
                        FUNCTION_KEY_PREFIX + key,
                        func, resolver, transformer);
                var resolvedPath = path + "." + FUNCTION_KEY_PREFIX + key;
                try {
                    return new FunctionMatchResult<>(func.apply(context), resolvedPath);
                } catch (Throwable ex) {
                    log.warn("Failed running object function (at {})", resolvedPath, ex);
                    return new FunctionMatchResult<>(null, resolvedPath);
                }
            }
        }
        // didn't find an object function
        return null;
    }

    private InlineFunctionContext<JE, JA, JO> tryParseInlineFunction(String path, String value, co.nlighten.jsontransform.ParameterResolver resolver,
                                                                     JsonTransformerFunction<JE> transformer) {
        var matcher = inlineFunctionRegex.matcher(value);
        if (matcher.find()) {
            var functionKey = matcher.group(1);
            if (functions.containsKey(functionKey)) {
                var function = functions.get(functionKey);
                var argsString = matcher.group(3);
                var args = new ArrayList<>();
                if (argsString != null && !argsString.isEmpty()) {
                    var argMatcher = inlineFunctionArgsRegex.matcher(argsString);
                    while (argMatcher.find() &&
                            (argMatcher.start() != argsString.length() || argsString.endsWith(","))) {
                        var arg = argMatcher.group(1);
                        var trimmed = argMatcher.group(1).trim();
                        // if after removing all the surrounding spaces we are left with quoted text, then unquote it
                        if (trimmed.startsWith(QUOTE_APOS) && trimmed.endsWith(QUOTE_APOS) && trimmed.length() > 1) {
                            if (trimmed.startsWith(ESCAPE_DOLLAR) || trimmed.startsWith(ESCAPE_HASH)) {
                                trimmed = trimmed.substring(1); // escape
                            }
                            arg = jsonAdapter.getAsString(jsonAdapter.parse(trimmed));
                            //otherwise, take the whole argument as-is
                        }
                        args.add(arg);
                    }
                }
                var matchEndIndex = matcher.end();
                String input;
                if (value.charAt(matchEndIndex - 1) != ':') { // if not ends with ':' then no input value specified
                    input = null;
                } else {
                    input = value.substring(matchEndIndex);
                }
                return new InlineFunctionContext<>(
                        path + "/" + FUNCTION_KEY_PREFIX + functionKey,
                        input, args,
                        jsonAdapter,
                        functionKey,
                        function,
                        resolver, transformer);
            }
        }
        return null;
    }

    public FunctionMatchResult<Object> matchInline(String path, String value, ParameterResolver resolver, JsonTransformerFunction<JE> transformer) {
        if (value == null) return null;
        var context = tryParseInlineFunction(path, value, resolver, transformer);
        if (context == null) {
            return null;
        }
        // at this point we detected an inline function, we must return a match result
        var resolvedPath = context.getPathFor(null);
        try {
            var result = functions.get(context.getAlias()).apply(context);
            return new FunctionMatchResult<>(result, resolvedPath);
        } catch (Throwable ex) {
            log.warn("Failed running inline function (at {})", resolvedPath, ex);
        }
        return new FunctionMatchResult<>(null, resolvedPath);
    }

    public Map<String, TransformerFunction<JE, JA, JO>> getFunctions() {
        return functions;
    }

    /**
     * The purpose of this class is to differentiate between null and null result
     *
     * @param <T>
     */
    public record FunctionMatchResult<T>(T result, String resultPath) {}
}
