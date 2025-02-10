package co.nlighten.jsontransform.formats.xml;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonOrg {

    private final JsonAdapter<?, ?, ?> adapter;

    public JsonOrg(JsonAdapter<?, ?, ?> adapter) {
        this.adapter = adapter;
    }

    public Object toJsonElement(Object value) {
        Object jeValue = null;
        if (JSONObject.NULL.equals(value)) {
            jeValue = adapter.jsonNull();
        } else if (value instanceof JSONObject propertyValue) {
            jeValue = toJsonObject(propertyValue);
        } else if (value instanceof JSONArray propertyArrayValue) {
            jeValue = toJsonArray(propertyArrayValue);
        } else if (value instanceof Character c) {
            jeValue = adapter.wrap(c);
        } else if (value instanceof String s) {
            jeValue = adapter.wrap(s);
        } else if (value instanceof Boolean b) {
            jeValue = adapter.wrap(b);
        } else if (value instanceof Number n) {
            jeValue = adapter.wrap(n);
        }
        return jeValue;
    }

    public Object toJsonArray(JSONArray ja) {
        var result = adapter.createArray();
        ja.forEach(value -> adapter.add(result, toJsonElement(value)));
        return result;
    }

    public Object toJsonObject(JSONObject jo) {
        var results = adapter.createObject();
        for (String key : jo.keySet()) {
            adapter.add(results, key, toJsonElement(jo.get(key)));
        }
        return results;
    }

    public Object toJSONElement(Object value) {
        if (value instanceof JSONArray || value instanceof JSONObject || JSONObject.NULL.equals(value)) {
            return value;
        }
        if (adapter.is(value)) {
            if (adapter.isNull(value)) {
                return JSONObject.NULL;
            }
            if (adapter.isJsonArray(value)) {
                return toJSONArray(value);
            }
            if (adapter.isJsonObject(value)) {
                return toJSONObject(value);
            }
            return adapter.unwrap(value, true);
        }
        return JSONObject.wrap(value);
    }

    public JSONArray toJSONArray(Object ja) {
        var result = new JSONArray();
        for (var value: adapter.asIterable(ja)) {
            result.put(toJSONElement(value));
        }
        return result;
    }

    public JSONObject toJSONObject(Object jo) {
        var results = new JSONObject();
        for (String key : adapter.keySet(jo)) {
            results.put(key, toJSONElement(adapter.get(jo, key)));
        }
        return results;
    }
}
