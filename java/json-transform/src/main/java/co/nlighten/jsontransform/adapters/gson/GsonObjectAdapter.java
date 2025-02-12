package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.adapters.JsonObjectAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public class GsonObjectAdapter extends JsonObjectAdapter<JsonElement, JsonArray, JsonObject> {
    @Override
    public JsonObject create() {
        return new JsonObject();
    }

    @Override
    public void add(JsonObject object, String key, String value) {
        object.addProperty(key, value);
    }

    @Override
    public void add(JsonObject object, String key, Number value) {
        object.addProperty(key, value);
    }

    @Override
    public void add(JsonObject object, String key, Boolean value) {
        object.addProperty(key, value);
    }

    @Override
    public void add(JsonObject object, String key, Character value) {
        object.addProperty(key, value);
    }

    @Override
    public void add(JsonObject object, String key, JsonElement value) {
        object.add(key, value);
    }

    @Override
    public void add(JsonObject object, String key, JsonArray value) {
        object.add(key, value);
    }

    @Override
    public JsonElement remove(JsonObject object, String key) {
        return object.remove(key);
    }

    @Override
    public boolean has(JsonObject object, String key) {
        return object.has(key);
    }

    @Override
    public JsonElement get(JsonObject object, String key) {
        return object.get(key);
    }

    @Override
    public int size(JsonObject object) {
        return object.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JsonObject;
    }

    @Override
    public Set<Map.Entry<String, JsonElement>> entrySet(JsonObject object) {
        return object.entrySet();
    }

    @Override
    public Set<String> keySet(JsonObject object) {
        return object.keySet();
    }

}