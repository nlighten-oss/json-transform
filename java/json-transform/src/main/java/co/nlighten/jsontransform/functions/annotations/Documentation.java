package co.nlighten.jsontransform.functions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documentation for a function. Value is the short description, and description is the long one.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Documentation {
    /**
     * Short description of the function.
     */
    String value() default "";

    /**
     * Long description of the function.
     */
    String notes() default "";
}
