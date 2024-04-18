package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonObjectAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class JsonOrgObjectAdapter extends JsonObjectAdapter<Object, JSONArray, JSONObject> {

    public JsonOrgObjectAdapter(JsonAdapter<Object, JSONArray, JSONObject> adapter) {
        super(JSONObject.class, adapter);
    }

    @Override
    public JSONObject create() {
        return new JSONObject();
    }

    @Override
    public void add(JSONObject object, String key, String value) {
        object.put(key, value);
    }

    @Override
    public void add(JSONObject object, String key, Number value) {
        object.put(key, value);
    }

    @Override
    public void add(JSONObject object, String key, Boolean value) {
        object.put(key, value);
    }

    @Override
    public void add(JSONObject object, String key, Character value) {
        object.put(key, value);
    }

    @Override
    public void add(JSONObject object, String key, Object value) {
        object.put(key, value);
    }

    @Override
    public void add(JSONObject object, String key, JSONArray value) {
        object.put(key, value);
    }

    @Override
    public Object remove(JSONObject object, String key) {
        return object.remove(key);
    }

    @Override
    public boolean has(JSONObject object, String key) {
        return object.has(key);
    }

    @Override
    public Object get(JSONObject object, String key) {
        return object.get(key);
    }

    @Override
    public int size(JSONObject object) {
        return object.length();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JSONObject;
    }
    
    @Override
    public JSONObject convert(Object value) {
        if (value instanceof JSONObject jo) return jo;
        var je = adapter.wrap(value);
        return je instanceof JSONObject jo ? jo : null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet(JSONObject object) {
        return object.keySet().stream()
                .map(key -> Map.entry(key, object.get(key)))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> keySet(JSONObject object) {
        return object.keySet();
    }

}