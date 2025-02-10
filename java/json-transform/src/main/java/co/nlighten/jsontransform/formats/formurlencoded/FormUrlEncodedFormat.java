package co.nlighten.jsontransform.formats.formurlencoded;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.formats.FormatDeserializer;
import co.nlighten.jsontransform.formats.FormatSerializer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class FormUrlEncodedFormat implements FormatSerializer, FormatDeserializer {

    private final JsonAdapter<?, ?, ?> adapter;

    public FormUrlEncodedFormat(JsonAdapter<?, ?, ?> adapter) {
        this.adapter = adapter;
    }

    private void appendEntry(StringBuilder sb, String origKey, Object jeValue) {
        var key = URLEncoder.encode(origKey, StandardCharsets.UTF_8);
        sb.append('&').append(key);
        var originalValue = adapter.getAsString(jeValue);
        if (originalValue != null) {
            var value = URLEncoder.encode(originalValue, StandardCharsets.UTF_8);
            sb.append("=").append(value);
        }
    }

    @Override
    public String serialize(Object payload) {
        try {
            if (payload == null) return null;

            var wrapped = adapter.wrap(payload);
            if (!adapter.isJsonObject(wrapped)) {
                return null;
            }

            var sb = new StringBuilder();
            for (var kv : adapter.entrySet(wrapped)) {
                var key = kv.getKey();
                var value = kv.getValue();
                if (adapter.isJsonArray(value)) {
                    for (var jeValue : adapter.asIterable(value)) {
                        appendEntry(sb, key, jeValue);
                    }
                } else {
                    appendEntry(sb, key, value);
                }
            }
            return !sb.isEmpty() ? sb.substring(1) : "";
        }
        catch (Throwable te) {
            throw new RuntimeException(te);
        }
    }

    private static String parseName(String s, StringBuilder sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i+1, i+3),
                                                          16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e) {
                        String rest  = s.substring(i);
                        sb.append(rest);
                        if (rest.length() == 2) {
                            i++;
                        }
                    }

                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    @Override
    public Object deserialize(String input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        var jo = adapter.createObject();
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(input, "&");
        while (st.hasMoreTokens()) {
            var key = st.nextToken();
            String val;
            var pos = key.indexOf('=');
            if (pos > -1) {
                val = parseName(key.substring(pos + 1), sb);
                key = parseName(key.substring(0, pos), sb);
            } else {
                val = "true";
            }
            if (!adapter.has(jo, key)) {
                adapter.add(jo, key, val);
                adapter.add(jo, key + "$$", adapter.createArray());
            }
            adapter.add(adapter.get(jo, key + "$$"), val);
        }
        return jo;
    }
}
