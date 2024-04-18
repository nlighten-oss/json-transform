package co.nlighten.jsontransform.functions.annotations;

import co.nlighten.jsontransform.functions.common.ArgType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the input type of a function.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InputType {
    ArgType[] value() default ArgType.Any;
    String description() default "";
}
