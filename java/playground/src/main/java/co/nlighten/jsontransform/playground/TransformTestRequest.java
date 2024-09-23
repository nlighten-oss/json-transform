package co.nlighten.jsontransform.playground;

import com.google.gson.JsonElement;

import java.util.Map;

public class TransformTestRequest {
    public Object input;
    public JsonElement definition;
    public Map<String, Object> additionalContext;
}
