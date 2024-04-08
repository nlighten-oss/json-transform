package co.nlighten.jsontransform.functions.annotations;

import co.nlighten.jsontransform.functions.ArgType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the output type of a function.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OutputType {
    ArgType[] value() default ArgType.Any;
    String description() default "";
}