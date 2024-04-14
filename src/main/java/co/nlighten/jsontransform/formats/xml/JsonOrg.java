package co.nlighten.jsontransform.formats.xml;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonOrg<JE, JA extends Iterable<JE>, JO extends JE> {

    private final JsonAdapter<JE, JA, JO> adapter;

    public JsonOrg(JsonAdapter<JE, JA, JO> adapter) {
        this.adapter = adapter;
    }

    @SuppressWarnings("unchecked")
    public JE toJsonElement(Object value) {
        JE jeValue = null;
        if (JSONObject.NULL.equals(value)) {
            jeValue = adapter.jsonNull();
        } else if (value instanceof JSONObject propertyValue) {
            jeValue = toJsonObject(propertyValue);
        } else if (value instanceof JSONArray propertyArrayValue) {
            jeValue = (JE)toJsonArray(propertyArrayValue);
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

    public JA toJsonArray(JSONArray ja) {
        var result = adapter.jArray.create();
        ja.forEach(value -> adapter.jArray.add(result, toJsonElement(value)));
        return result;
    }

    public JO toJsonObject(JSONObject jo) {
        var results = adapter.jObject.create();
        for (String key : jo.keySet()) {
            adapter.jObject.add(results, key, toJsonElement(jo.get(key)));
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public Object toJSONElement(Object value) {
        if (value instanceof JSONArray || value instanceof JSONObject || JSONObject.NULL.equals(value)) {
            return value;
        }
        if (adapter.is(value)) {
            var je = (JE)value;
            if (adapter.isNull(je)) {
                return JSONObject.NULL;
            }
            if (adapter.jArray.is(je)) {
                return toJSONArray((JA)je);
            }
            if (adapter.jObject.is(je)) {
                return toJSONObject((JO)je);
            }
            return adapter.unwrap(je, true);
        }
        return JSONObject.wrap(value);
    }

    public JSONArray toJSONArray(JA ja) {
        var result = new JSONArray();
        ja.forEach(value -> result.put(toJSONElement(value)));
        return result;
    }

    public JSONObject toJSONObject(JO jo) {
        var results = new JSONObject();
        for (String key : adapter.jObject.keySet(jo)) {
            results.put(key, toJSONElement(adapter.jObject.get(jo, key)));
        }
        return results;
    }
}
