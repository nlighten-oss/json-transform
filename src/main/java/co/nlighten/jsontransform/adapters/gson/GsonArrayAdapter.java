package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GsonArrayAdapter extends JsonArrayAdapter<JsonElement, JsonArray, JsonObject> {

    public GsonArrayAdapter(JsonAdapter<JsonElement, JsonArray, JsonObject> adapter) {
        super(JsonArray.class, adapter);
    }

    @Override
    public JsonArray create() {
        return new JsonArray();
    }

    @Override
    public JsonArray create(int capacity) {
        return new JsonArray(capacity);
    }

    @Override
    public void add(JsonArray array, String value) {
        array.add(value);
    }

    @Override
    public void add(JsonArray array, Number value) {
        array.add(value);
    }

    @Override
    public void add(JsonArray array, Boolean value) {
        array.add(value);
    }

    @Override
    public void add(JsonArray array, Character value) {
        array.add(value);
    }

    @Override
    public void add(JsonArray array, JsonElement value) {
        array.add(value);
    }

    @Override
    public void add(JsonArray array, JsonArray value) {
        array.add(value);
    }

    @Override
    public void set(JsonArray array, int index, JsonElement value) {
        array.set(index, value);
    }

    @Override
    public JsonElement remove(JsonArray array, int index) {
        return array.remove(index);
    }

    @Override
    public JsonElement get(JsonArray array, int index) {
        return array.get(index);
    }

    @Override
    public int size(JsonArray array) {
        return array.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JsonArray;
    }

    @Override
    public Stream<JsonElement> stream(JsonArray array, boolean parallel) {
        return StreamSupport.stream(array.spliterator(), parallel);
    }
}
