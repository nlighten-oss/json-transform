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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TransformerFunctions implements TransformerFunctionsAdapter {
    static final Logger log = LoggerFactory.getLogger(TransformerFunctions.class);

    private static final Pattern inlineFunctionRegex = Pattern.compile("^\\$\\$(\\w+)(\\((.*?)\\))?(:|$)");
    private static final Pattern inlineFunctionArgsRegex = Pattern.compile("('(\\\\'|[^'])*'|[^,]*)(?:,|$)");
    public static final String FUNCTION_KEY_PREFIX = "$$";
    public static final String QUOTE_APOS = "'";
    public static final String ESCAPE_DOLLAR = "\\$";
    public static final String ESCAPE_HASH = "\\#";

    private static Map<String, TransformerFunction> functions = Map.of();

    static {
        registerFunctions(
                Map.entry("and", new TransformerFunctionAnd()),
                Map.entry("at",new TransformerFunctionAt()),
                Map.entry("avg",new TransformerFunctionAvg()),
                Map.entry("base64", new TransformerFunctionBase64()),
                Map.entry("boolean", new TransformerFunctionBoolean()),
                Map.entry("coalesce", new TransformerFunctionCoalesce()),
                Map.entry("concat", new TransformerFunctionConcat()),
                Map.entry("contains", new TransformerFunctionContains()),
                Map.entry("csv", new TransformerFunctionCsv()),
                Map.entry("csvparse", new TransformerFunctionCsvParse()),
                Map.entry("date", new TransformerFunctionDate()),
                Map.entry("decimal", new TransformerFunctionDecimal()),
                Map.entry("digest", new TransformerFunctionDigest()),
                Map.entry("distinct", new TransformerFunctionDistinct()),
                Map.entry("entries", new TransformerFunctionEntries()),
                Map.entry("eval",new TransformerFunctionEval()),
                Map.entry("filter", new TransformerFunctionFilter()),
                Map.entry("find", new TransformerFunctionFind()),
                Map.entry("first", new TransformerFunctionCoalesce()), // * alias for coalesce
                Map.entry("flat", new TransformerFunctionFlat()),
                Map.entry("flatten",new TransformerFunctionFlatten()),
                Map.entry("form", new TransformerFunctionForm()),
                Map.entry("formparse", new TransformerFunctionFormParse()),
                Map.entry("group", new TransformerFunctionGroup()),
                Map.entry("if", new TransformerFunctionIf()),
                Map.entry("is", new TransformerFunctionIs()),
                Map.entry("isnull", new TransformerFunctionIsNull()),
                Map.entry("join", new TransformerFunctionJoin()),
                Map.entry("json", new TransformerFunctionJsonParse()),
                Map.entry("jsonparse", new TransformerFunctionJsonParse()),
                Map.entry("jsonpatch", new TransformerFunctionJsonPatch()),
                Map.entry("jsonpath", new TransformerFunctionJsonPath()),
                Map.entry("jsonpointer", new TransformerFunctionJsonPointer()),
                Map.entry("jwtparse", new TransformerFunctionJwtParse()),
                Map.entry("length", new TransformerFunctionLength()),
                Map.entry("long", new TransformerFunctionLong()),
                Map.entry("lookup", new TransformerFunctionLookup()),
                Map.entry("lower", new TransformerFunctionLower()),
                Map.entry("map", new TransformerFunctionMap()),
                Map.entry("match", new TransformerFunctionMatch()),
                Map.entry("matchall", new TransformerFunctionMatchAll()),
                Map.entry("math", new TransformerFunctionMath()),
                Map.entry("max",new TransformerFunctionMax()),
                Map.entry("min",new TransformerFunctionMin()),
                Map.entry("normalize", new TransformerFunctionNormalize()),
                Map.entry("not", new TransformerFunctionNot()),
                Map.entry("numberformat", new TransformerFunctionNumberFormat()),
                Map.entry("numberparse", new TransformerFunctionNumberParse()),
                Map.entry("object", new TransformerFunctionObject()),
                Map.entry("or", new TransformerFunctionOr()),
                Map.entry("pad", new TransformerFunctionPad()),
                Map.entry("partition", new TransformerFunctionPartition()),
                Map.entry("range", new TransformerFunctionRange()),
                Map.entry("raw", new TransformerFunctionRaw()),
                Map.entry("reduce", new TransformerFunctionReduce()),
                Map.entry("replace", new TransformerFunctionReplace()),
                Map.entry("reverse", new TransformerFunctionReverse()),
                Map.entry("slice", new TransformerFunctionSlice()),
                Map.entry("sort", new TransformerFunctionSort()),
                Map.entry("split", new TransformerFunctionSplit()),
                Map.entry("string", new TransformerFunctionString()),
                Map.entry("substring", new TransformerFunctionSubstring()),
                Map.entry("sum",new TransformerFunctionSum()),
                Map.entry("switch", new TransformerFunctionSwitch()),
                Map.entry("test", new TransformerFunctionTest()),
                Map.entry("transform", new TransformerFunctionTransform()),
                Map.entry("trim", new TransformerFunctionTrim()),
                Map.entry("unflatten",new TransformerFunctionUnflatten()),
                Map.entry("upper", new TransformerFunctionUpper()),
                Map.entry("uriparse", new TransformerFunctionUriParse()),
                Map.entry("urldecode", new TransformerFunctionUrlDecode()),
                Map.entry("urlencode", new TransformerFunctionUrlEncode()),
                Map.entry("uuid", new TransformerFunctionUuid()),
                Map.entry("value",new TransformerFunctionValue()),
                Map.entry("wrap", new TransformerFunctionWrap()),
                Map.entry("xml", new TransformerFunctionXml()),
                Map.entry("xmlparse", new TransformerFunctionXmlParse()),
                Map.entry("xor", new TransformerFunctionXor()),
                Map.entry("yaml", new TransformerFunctionYaml()),
                Map.entry("yamlparse", new TransformerFunctionYamlParse())
        );
    }

    @SafeVarargs
    public synchronized static void registerFunctions(Map.Entry<String, TransformerFunction>... moreFunctions) {
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
    public CompletionStage<FunctionMatchResult<Object>> matchObject(JsonAdapter<?,?,?> adapter, String path, Object definition, co.nlighten.jsontransform.ParameterResolver resolver, JsonTransformerFunction transformer) {
        if (definition == null) {
            return null;
        }
        // look for an object function
        // (precedence is all internal functions sorted alphabetically first, then client added ones second, by registration order)
        for (String key : functions.keySet()) {
            if (adapter.has(definition, FUNCTION_KEY_PREFIX + key)) {
                var func = functions.get(key);
                var context = new ObjectFunctionContext(
                        path,
                        definition,
                        adapter,
                        FUNCTION_KEY_PREFIX + key,
                        func, resolver, transformer);
                var resolvedPath = path + "." + FUNCTION_KEY_PREFIX + key;
                try {
                    return func.apply(context).thenApply(result -> new FunctionMatchResult<>(result, resolvedPath));
                } catch (Throwable ex) {
                    log.warn("Failed running object function (at {})", resolvedPath, ex);
                    return CompletableFuture.completedStage(new FunctionMatchResult<>(null, resolvedPath));
                }
            }
        }
        // didn't find an object function
        return null;
    }

    private InlineFunctionContext tryParseInlineFunction(JsonAdapter<?,?,?> adapter, String path, String value, co.nlighten.jsontransform.ParameterResolver resolver,
                                                                     JsonTransformerFunction transformer) {
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
                        if (trimmed.startsWith(QUOTE_APOS) && trimmed.endsWith(QUOTE_APOS) && trimmed.length() > 1) {
                            arg = adapter.getAsString(adapter.parse(trimmed));
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
                return new InlineFunctionContext(
                        path + "/" + FUNCTION_KEY_PREFIX + functionKey,
                        input, args,
                        adapter,
                        functionKey,
                        function,
                        resolver, transformer);
            }
        }
        return null;
    }

    public CompletionStage<FunctionMatchResult<Object>> matchInline(JsonAdapter<?,?,?> adapter, String path, String value, ParameterResolver resolver, JsonTransformerFunction transformer) {
        if (value == null) return null;
        var context = tryParseInlineFunction(adapter, path, value, resolver, transformer);
        if (context == null) {
            return null;
        }
        // at this point we detected an inline function, we must return a match result
        var resolvedPath = context.getPathFor(null);
        try {
            return functions.get(context.getAlias()).apply(context)
                    .thenApply(result -> new FunctionMatchResult<>(result, resolvedPath));
        } catch (Throwable ex) {
            log.warn("Failed running inline function (at {})", resolvedPath, ex);
        }
        return CompletableFuture.completedStage(new FunctionMatchResult<>(null, resolvedPath));
    }

    public Map<String, TransformerFunction> getFunctions() {
        return functions;
    }

    /**
     * The purpose of this class is to differentiate between null and null result
     *
     * @param <T>
     */
    public record FunctionMatchResult<T>(T result, String resultPath) {}
}
