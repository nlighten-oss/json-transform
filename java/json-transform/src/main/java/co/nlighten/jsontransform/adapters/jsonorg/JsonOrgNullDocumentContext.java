package co.nlighten.jsontransform.adapters.jsonorg;

import com.jayway.jsonpath.*;
import org.json.JSONObject;

public class JsonOrgNullDocumentContext implements DocumentContext {
    public static final JsonOrgNullDocumentContext INSTANCE = new JsonOrgNullDocumentContext();

    @Override
    public Configuration configuration() {
        return Configuration.defaultConfiguration();
    }

    @Override
    public <T> T json() {
        return (T) JSONObject.NULL;
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
        return (T)JSONObject.NULL;
    }

    @Override
    public <T> T read(String s, Class<T> aClass, Predicate... predicates) {
        return (T)JSONObject.NULL;
    }

    @Override
    public <T> T read(JsonPath jsonPath) {
        return (T)JSONObject.NULL;
    }

    @Override
    public <T> T read(JsonPath jsonPath, Class<T> aClass) {
        return (T)JSONObject.NULL;
    }

    @Override
    public <T> T read(JsonPath jsonPath, TypeRef<T> typeRef) {
        return (T)JSONObject.NULL;
    }

    @Override
    public <T> T read(String s, TypeRef<T> typeRef) {
        return (T)JSONObject.NULL;
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
