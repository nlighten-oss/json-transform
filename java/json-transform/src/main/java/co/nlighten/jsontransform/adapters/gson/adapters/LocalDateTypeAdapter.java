package co.nlighten.jsontransform.adapters.gson.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return switch (in.peek()) {
            case STRING -> LocalDate.parse(in.nextString());
            case NUMBER -> LocalDate.ofEpochDay(in.nextLong());
            default -> null;
        };
    }
}
