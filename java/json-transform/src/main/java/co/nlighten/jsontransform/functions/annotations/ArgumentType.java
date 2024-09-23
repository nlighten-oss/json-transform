package co.nlighten.jsontransform.functions.annotations;


import co.nlighten.jsontransform.functions.common.ArgType;

import java.lang.annotation.*;

/**
 * Describes a function argument.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ArgumentsTypes.class)
public @interface ArgumentType {
    String value();
    ArgType type();
    int position() default -1;

    boolean defaultIsNull() default false;
    boolean defaultBoolean() default false;
    String defaultString() default "";
    String defaultEnum() default "";
    int defaultInteger() default -1;
    long defaultLong() default -1;
    double defaultBigDecimal() default -1;

    String[] aliases() default {};
}
