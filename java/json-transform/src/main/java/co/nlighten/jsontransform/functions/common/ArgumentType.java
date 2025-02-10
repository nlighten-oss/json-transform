package co.nlighten.jsontransform.functions.common;

import java.math.BigDecimal;

public class ArgumentType {
    public ArgType type;
    public int position = -1;

    public boolean defaultIsNull = false;
    public boolean defaultBoolean = false;
    public String defaultString = "";
    public String defaultEnum = "";
    public int defaultInteger = -1;
    public long defaultLong = -1L;
    public BigDecimal defaultBigDecimal = BigDecimal.ONE.negate();

    public String[] aliases = {};

    public static ArgumentType of(ArgType type) {
        var arg = new ArgumentType();
        arg.type = type;
        return arg;
    }
    public ArgumentType position(int position) {
        this.position = position;
        return this;
    }
    public ArgumentType defaultIsNull(boolean defaultIsNull) {
        this.defaultIsNull = defaultIsNull;
        return this;
    }
    public ArgumentType defaultBoolean(boolean defaultBoolean) {
        this.defaultBoolean = defaultBoolean;
        return this;
    }
    public ArgumentType defaultString(String defaultString) {
        this.defaultString = defaultString;
        return this;
    }
    public ArgumentType defaultEnum(String defaultEnum) {
        this.defaultEnum = defaultEnum;
        return this;
    }
    public ArgumentType defaultInteger(int defaultInteger) {
        this.defaultInteger = defaultInteger;
        return this;
    }
    public ArgumentType defaultLong(long defaultLong) {
        this.defaultLong = defaultLong;
        return this;
    }
    public ArgumentType defaultBigDecimal(BigDecimal defaultBigDecimal) {
        this.defaultBigDecimal = defaultBigDecimal;
        return this;
    }
}
