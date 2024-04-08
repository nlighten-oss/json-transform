package co.nlighten.jsontransform.functions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the alias(es) to the function
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Aliases {
    /**
     * Value[0] is function's main alias, all others are aliases to it
     */
    String[] value() default "";
}
