package co.nlighten.jsontransform.adapters.pojo;

import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

public class PojoArrayAdapter extends JsonArrayAdapter<Object, AbstractList<Object>, Map<String, Object>> {
    @Override
    public AbstractList<Object> create() {
        return new ArrayList<>();
    }

    @Override
    public AbstractList<Object> create(int capacity) {
        return new ArrayList<>(capacity);
    }

    @Override
    public void add(AbstractList<Object> array, String value) {
        array.add(value);
    }

    @Override
    public void add(AbstractList<Object> array, Number value) {
        array.add(value);
    }

    @Override
    public void add(AbstractList<Object> array, Boolean value) {
        array.add(value);
    }

    @Override
    public void add(AbstractList<Object> array, Character value) {
        array.add(value);
    }

    @Override
    public void add(AbstractList<Object> array, Object value) {
        array.add(value);
    }

    @Override
    public void add(AbstractList<Object> array, AbstractList<Object> value) {
        array.add(value);
    }

    @Override
    public void set(AbstractList<Object> array, int index, Object value) {
        if (array.size() > index || JsonAdapterHelpers.trySetArrayAtOOB(this, array, index, value, null)) {
            array.set(index, value);
        }
    }

    @Override
    public Object remove(AbstractList<Object> array, int index) {
        return array.remove(index);
    }

    @Override
    public Object get(AbstractList<Object> array, int index) {
        return array.get(index);
    }

    @Override
    public int size(AbstractList<Object> array) {
        return array.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof AbstractList<?>;
    }

    @Override
    public Stream<Object> stream(AbstractList<Object> array, boolean parallel) {
        return array.stream();
    }
}
