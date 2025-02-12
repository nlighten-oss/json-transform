package co.nlighten.jsontransform.adapters;

import java.util.stream.Stream;

public abstract class JsonArrayAdapter<JE, JA extends Iterable<JE>, JO extends JE> {

    public abstract JA create();
    public abstract JA create(int capacity);

    public abstract void add(JA array, String value);
    public abstract void add(JA array, Number value);
    public abstract void add(JA array, Boolean value);
    public abstract void add(JA array, Character value);
    public abstract void add(JA array, JE value);
    public abstract void add(JA array, JA value);
    public abstract void set(JA array, int index, JE value);
    public abstract JE remove(JA array, int index);

    public abstract JE get(JA array, int index);

    public abstract int size(JA array);
    public boolean isEmpty(JA array) {
        return size(array) == 0;
    }
    public abstract boolean is(Object value);

    public abstract Stream<JE> stream(JA array, boolean parallel);

    public Stream<JE> stream(JA array) {
        return stream(array, false);
    }
}
