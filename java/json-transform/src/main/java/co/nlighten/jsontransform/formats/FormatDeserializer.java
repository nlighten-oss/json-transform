package co.nlighten.jsontransform.formats;

public interface FormatDeserializer<JE> {
    JE deserialize(String input);
}
