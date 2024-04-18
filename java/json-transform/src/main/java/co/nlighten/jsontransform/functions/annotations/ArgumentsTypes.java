package co.nlighten.jsontransform.functions.annotations;

import java.lang.annotation.*;

/**
 * Collection of ArgumentType annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ArgumentsTypes {
    ArgumentType[] value();
}
