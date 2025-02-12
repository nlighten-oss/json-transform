package co.nlighten.jsontransform.adapters.pojo;

import com.jayway.jsonpath.*;

public class PojoNullDocumentContext implements DocumentContext {
    public static final PojoNullDocumentContext INSTANCE = new PojoNullDocumentContext();

    @Override
    public Configuration configuration() {
        return Configuration.defaultConfiguration();
    }

    @Override
    public <T> T json() {
        return (T)PojoNull.INSTANCE;
    }

    @Override
    public String jsonString() {
        return "null";
    }

    @Override
    public DocumentContext set(String s, Object o, Predicate... predicates) {
        return this;
    }

    @Override
    public DocumentContext set(JsonPath jsonPath, Object o) {
        return this;
    }

    @Override
    public DocumentContext map(String s, MapFunction mapFunction, Predicate... predicates) {
        return this;
    }

    @Override
    public DocumentContext map(JsonPath jsonPath, MapFunction mapFunction) {
        return this;
    }

    @Override
    public DocumentContext delete(String s, Predicate... predicates) {
        return this;
    }

    @Override
    public DocumentContext delete(JsonPath jsonPath) {
        return this;
    }

    @Override
    public DocumentContext add(String s, Object o, Predicate... predicates) {
        return this;
    }

    @Override
    public DocumentContext add(JsonPath jsonPath, Object o) {
        return this;
    }

    @Override
    public DocumentContext put(String s, String s1, Object o, Predicate... predicates) {
        return this;
    }

    @Override
    public DocumentContext put(JsonPath jsonPath, String s, Object o) {
        return this;
    }

    @Override
    public DocumentContext renameKey(String s, String s1, String s2, Predicate... predicates) {
        return this;
    }

    @Override
    public DocumentContext renameKey(JsonPath jsonPath, String s, String s1) {
        return this;
    }

    @Override
    public <T> T read(String s, Predicate... predicates) {
        return (T)PojoNull.INSTANCE;
    }

    @Override
    public <T> T read(String s, Class<T> aClass, Predicate... predicates) {
        return (T)PojoNull.INSTANCE;
    }

    @Override
    public <T> T read(JsonPath jsonPath) {
        return (T)PojoNull.INSTANCE;
    }

    @Override
    public <T> T read(JsonPath jsonPath, Class<T> aClass) {
        return (T)PojoNull.INSTANCE;
    }

    @Override
    public <T> T read(JsonPath jsonPath, TypeRef<T> typeRef) {
        return (T)PojoNull.INSTANCE;
    }

    @Override
    public <T> T read(String s, TypeRef<T> typeRef) {
        return (T)PojoNull.INSTANCE;
    }

    @Override
    public ReadContext limit(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReadContext withListeners(EvaluationListener... evaluationListeners) {
        throw new UnsupportedOperationException();
    }
}
