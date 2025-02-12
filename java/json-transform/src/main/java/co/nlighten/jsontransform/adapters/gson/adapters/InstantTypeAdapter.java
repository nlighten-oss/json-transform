package co.nlighten.jsontransform.adapters.gson.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class InstantTypeAdapter extends TypeAdapter<Instant> {

    @Override
    public void write(JsonWriter out, Instant value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        return switch (in.peek()) {
            case STRING -> Instant.parse(in.nextString());
            case NUMBER -> Instant.ofEpochSecond(in.nextLong());
            default -> null;
        };
    }
}
