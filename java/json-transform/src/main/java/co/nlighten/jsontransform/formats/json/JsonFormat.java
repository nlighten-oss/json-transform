package co.nlighten.jsontransform.formats.json;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.formats.FormatDeserializer;
import co.nlighten.jsontransform.formats.FormatSerializer;

public class JsonFormat<JE, JA extends Iterable<JE>, JO extends JE> implements FormatSerializer, FormatDeserializer<JE> {

    private final JsonAdapter<JE, JA, JO> adapter;

    public JsonFormat(JsonAdapter<JE, JA, JO> adapter) {
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
    public JE deserialize(String input) {
        return adapter.parse(input);
    }
}
