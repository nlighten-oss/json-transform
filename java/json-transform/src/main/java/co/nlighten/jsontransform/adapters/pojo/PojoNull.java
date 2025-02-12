package co.nlighten.jsontransform.adapters.pojo;

public final class PojoNull {

    public static final PojoNull INSTANCE = new PojoNull();

    @Override
    public int hashCode() {
        return PojoNull.class.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PojoNull;
    }
}
