package co.nlighten.jsontransform.playground;

import co.nlighten.jsontransform.DebuggableTransformerFunctions;

import java.util.Map;

public class TransformTestResponse {
    Object result;
    Map<String, DebuggableTransformerFunctions.TransformerDebugInfo> debugInfo;
    public TransformTestResponse(Object result, Map<String, DebuggableTransformerFunctions.TransformerDebugInfo> debugInfo) {
        this.result = result;
        this.debugInfo = debugInfo;
    }
}
