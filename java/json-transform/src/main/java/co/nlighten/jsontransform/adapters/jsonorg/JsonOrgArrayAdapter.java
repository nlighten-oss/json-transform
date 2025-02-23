package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class JsonOrgArrayAdapter extends JsonArrayAdapter<Object, JSONArray, JSONObject> {
    @Override
    public JSONArray create() {
        return new JSONArray();
    }

    @Override
    public JSONArray create(int capacity) {
        return new JSONArray(capacity);
    }

    @Override
    public void add(JSONArray array, String value) {
        array.put(value);
    }

    @Override
    public void add(JSONArray array, Number value) {
        array.put(value);
    }

    @Override
    public void add(JSONArray array, Boolean value) {
        array.put(value);
    }

    @Override
    public void add(JSONArray array, Character value) {
        array.put(value);
    }

    @Override
    public void add(JSONArray array, Object value) {
        array.put(value);
    }

    @Override
    public void add(JSONArray array, JSONArray value) {
        array.put(value);
    }

    @Override
    public void addAll(JSONArray array, JSONArray other) {
        array.putAll(other);
    }

    @Override
    public void set(JSONArray array, int index, Object value) {
        array.put(index, value);
    }

    @Override
    public Object remove(JSONArray array, int index) {
        return array.remove(index);
    }

    @Override
    public Object get(JSONArray array, int index) {
        return array.get(index);
    }

    @Override
    public int size(JSONArray array) {
        return array.length();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JSONArray;
    }

    @Override
    public Stream<Object> stream(JSONArray array, boolean parallel) {
        return StreamSupport.stream(array.spliterator(), parallel);
    }
}
