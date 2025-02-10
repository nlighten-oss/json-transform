package co.nlighten.jsontransform.formats.json;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.formats.FormatDeserializer;
import co.nlighten.jsontransform.formats.FormatSerializer;

public class JsonFormat implements FormatSerializer, FormatDeserializer {

    private final JsonAdapter<?, ?, ?> adapter;

    public JsonFormat(JsonAdapter<?, ?, ?> adapter) {
        this.adapter = adapter;
    }

    @Override
    public String serialize(Object payload) {
        if (payload == null) return adapter.jsonNull().toString();
        if (adapter.is(payload))
            return payload.toString();
        return adapter.toString(payload);
    }

    @Override
    public Object deserialize(String input) {
        return adapter.parse(input);
    }
}
