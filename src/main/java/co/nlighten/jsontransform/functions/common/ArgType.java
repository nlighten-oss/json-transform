package co.nlighten.jsontransform.functions.common;

/**
 * The type of argument that a function can accept or return.
 */
public enum ArgType {
    Boolean,
    String,
    Enum,
    Integer,
    Long,
    BigDecimal,
    Array,
    Object,
    Transformer,
    ArrayOfString,
    ArrayOfBigDecimal,
    ArrayOfArray,
    Any;
}
