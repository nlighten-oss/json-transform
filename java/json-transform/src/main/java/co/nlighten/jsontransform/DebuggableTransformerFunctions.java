package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class DebuggableTransformerFunctions extends TransformerFunctions{
    private final Map<String, TransformerDebugInfo> debugResults;

    public record TransformerDebugInfo(Object result) {}

    public DebuggableTransformerFunctions() {
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

    public CompletionStage<TransformerFunctions.FunctionMatchResult<Object>> matchObject(JsonAdapter<?,?,?> adapter, String path, Object definition, ParameterResolver resolver, JsonTransformerFunction transformer) {
        return super.matchObject(adapter, path, definition, resolver, transformer)
            .thenApply(match -> {
                auditAndReturn(path, match);
                return match;
            });
    }

    public CompletionStage<TransformerFunctions.FunctionMatchResult<Object>> matchInline(JsonAdapter<?,?,?> adapter, String path, String value, ParameterResolver resolver, JsonTransformerFunction transformer) {
        return super.matchInline(adapter, path, value, resolver, transformer)
            .thenApply(match -> {
                auditAndReturn(path, match);
                return match;
            });
    }

    public Map<String, TransformerDebugInfo> getDebugResults() {
        return debugResults;
    }
}
