package co.nlighten.jsontransform.adapters;

import java.util.Map;
import java.util.Set;

public abstract class JsonObjectAdapter<JE, JA extends Iterable<JE>, JO extends JE> {
    public final Class<JO> type;
    protected JsonAdapter<JE, JA, JO> adapter;

    public JsonObjectAdapter(Class<JO> type, JsonAdapter<JE, JA, JO> adapter) {
        this.type = type;
        this.adapter = adapter;
    }

    public void setAdapter(JsonAdapter<JE, JA, JO> adapter) {
        this.adapter = adapter;
    }
    public abstract JO create();

    public abstract void add(JO object, String key, String value);
    public abstract void add(JO object, String key, Number value);
    public abstract void add(JO object, String key, Boolean value);
    public abstract void add(JO object, String key, Character value);
    public abstract void add(JO object, String key, JE value);
    public abstract void add(JO object, String key, JA value);
    public abstract JE remove(JO object, String key);

    public abstract boolean has(JO object, String key);
    public abstract JE get(JO object, String key);
    public abstract int size(JO object);
    public boolean isEmpty(JO object) {
        return size(object) == 0;
    }

    public abstract boolean is(Object value);

    public abstract JO convert(Object value);
    public abstract Set<Map.Entry<String, JE>> entrySet(JO object);
    public abstract Set<String> keySet(JO object);
}
