package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.HashMap;
import java.util.Map;

public class DebuggableTransformerFunctions extends TransformerFunctions{
    private final Map<String, TransformerDebugInfo> debugResults;

    public record TransformerDebugInfo(Object result) {}

    public DebuggableTransformerFunctions(JsonAdapter<?, ?, ?> adapter) {
        super(adapter);
        debugResults = new HashMap<>();
    }

    private TransformerFunctions.FunctionMatchResult<Object> auditAndReturn(String path, TransformerFunctions.FunctionMatchResult<Object> matchResult) {
        if (matchResult == null) {
            return null;
        }
        // if the function result is the transformer's output, don't audit it
        if ("$".equals(path)) return matchResult;

        if (matchResult.result() instanceof JsonElementStreamer streamer) {
            debugResults.put(matchResult.resultPath(), new TransformerDebugInfo(streamer.toJsonArray()));
            return matchResult;
        }
        debugResults.put(matchResult.resultPath(), new TransformerDebugInfo(matchResult.result()));
        return matchResult;
    }

    public TransformerFunctions.FunctionMatchResult<Object> matchObject(String path, Object definition, ParameterResolver resolver, JsonTransformerFunction transformer) {
        return auditAndReturn(path, super.matchObject(path, definition, resolver, transformer));
    }

    public TransformerFunctions.FunctionMatchResult<Object> matchInline(String path, String value, ParameterResolver resolver, JsonTransformerFunction transformer) {
        return auditAndReturn(path, super.matchInline(path, value, resolver, transformer));
    }

    public Map<String, TransformerDebugInfo> getDebugResults() {
        return debugResults;
    }
}
