package co.nlighten.jsontransform.adapters.pojo;

import co.nlighten.jsontransform.adapters.JsonObjectAdapter;

import java.util.*;

public class PojoObjectAdapter extends JsonObjectAdapter<Object, AbstractList<Object>, Map<String, Object>> {
    
    @Override
    public Map<String, Object> create() {
        return new HashMap<>();
    }

    @Override
    public void add(Map<String, Object> object, String key, String value) {
        object.put(key, value);
    }

    @Override
    public void add(Map<String, Object> object, String key, Number value) {
        object.put(key, value);
    }

    @Override
    public void add(Map<String, Object> object, String key, Boolean value) {
        object.put(key, value);
    }

    @Override
    public void add(Map<String, Object> object, String key, Character value) {
        object.put(key, value);
    }

    @Override
    public void add(Map<String, Object> object, String key, Object value) {
        object.put(key, value);
    }

    @Override
    public void add(Map<String, Object> object, String key, AbstractList<Object> value) {
        object.put(key, value);
    }

    @Override
    public Object remove(Map<String, Object> object, String key) {
        return object.remove(key);
    }

    @Override
    public boolean has(Map<String, Object> object, String key) {
        return object.containsKey(key);
    }

    @Override
    public Object get(Map<String, Object> object, String key) {
        return object.get(key);
    }

    @Override
    public int size(Map<String, Object> object) {
        return object.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof Map<?, ?>;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet(Map<String, Object> object) {
        return object.entrySet();
    }

    @Override
    public Set<String> keySet(Map<String, Object> object) {
        return object.keySet();
    }
}
