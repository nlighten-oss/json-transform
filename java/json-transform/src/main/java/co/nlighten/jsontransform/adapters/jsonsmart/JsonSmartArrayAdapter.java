package co.nlighten.jsontransform.adapters.jsonsmart;

import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.stream.Stream;

public class JsonSmartArrayAdapter extends JsonArrayAdapter<Object, JSONArray, JSONObject> {
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
        array.add(value);
    }

    @Override
    public void add(JSONArray array, Number value) {
        array.add(value);
    }

    @Override
    public void add(JSONArray array, Boolean value) {
        array.add(value);
    }

    @Override
    public void add(JSONArray array, Character value) {
        array.add(value);
    }

    @Override
    public void add(JSONArray array, Object value) {
        array.add(value);
    }

    @Override
    public void add(JSONArray array, JSONArray value) {
        array.add(value);
    }

    @Override
    public void set(JSONArray array, int index, Object value) {
        if (array.size() > index || JsonAdapterHelpers.trySetArrayAtOOB(this, array, index, value, null)) {
            array.set(index, value);
        }
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
        return array.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JSONArray;
    }

    @Override
    public Stream<Object> stream(JSONArray array, boolean parallel) {
        return array.stream();
    }
}
