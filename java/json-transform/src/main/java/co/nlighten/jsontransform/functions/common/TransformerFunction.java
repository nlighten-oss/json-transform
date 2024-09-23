package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import co.nlighten.jsontransform.adapters.JsonObjectAdapter;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all transformer functions.
 * @param <JE> Json Element type
 * @param <JA> Json Array type
 * @param <JO> Json Object type
 */
public abstract class TransformerFunction<JE, JA extends Iterable<JE>, JO extends JE> {
    protected final Map<String, ArgumentType> arguments;
    protected final Map<String, Object> defaultValues;
    protected final JsonAdapter<JE, JA, JO> adapter;

    protected final JsonArrayAdapter<JE, JA, JO> jArray;
    protected final JsonObjectAdapter<JE, JA, JO> jObject;

    public TransformerFunction(JsonAdapter<JE, JA, JO> adapter) {
        this.adapter = adapter;
        this.jArray = adapter.jArray;
        this.jObject = adapter.jObject;

        var argArr = this.getClass().getAnnotationsByType(ArgumentType.class);
        if (argArr.length > 0) {
            var arguments = new HashMap<String, ArgumentType>();
            var defaultValues = new HashMap<String, Object>();
            Arrays.stream(argArr).forEachOrdered(arg -> {
               arguments.put(arg.value(), arg);
               if (arg.aliases().length > 0) {
                   Arrays.stream(arg.aliases()).forEach(alias -> {
                       arguments.put(alias, arg);
                   });
               }
               defaultValues.put(arg.value(), TransformerFunction.getDefaultValue(arg));
            });
            this.arguments = arguments;
            this.defaultValues = defaultValues;
        } else {
            this.arguments = Map.of();
            this.defaultValues = Map.of();
        }
    }

    private static Object getDefaultValue(ArgumentType a) {
        if (a == null || a.defaultIsNull()) return null;
        return switch (a.type()) {
            case Boolean -> a.defaultBoolean();
            case String -> a.defaultString();
            case Enum -> a.defaultEnum();
            case Integer -> a.defaultInteger();
            case Long -> a.defaultLong();
            case BigDecimal -> a.defaultBigDecimal();
            default -> null;
        };
    }

    /**
     * Apply the function to the given context.
     * @param context the context
     * @return the result of the function
     */
    public abstract Object apply(FunctionContext<JE, JA, JO> context);

    /**
     * Get the argument type for the given name.
     * @param name the name of the argument (null will return the primary argument)
     * @return the argument type or null if not found
     */
    public ArgumentType getArgument(String name) {
        if (name == null) return null;
        return arguments.get(name);
    }

    /**
     * Get the arguments for this function.
     * @return the function arguments
     */
    public Map<String, ArgumentType> getArguments() {
        return arguments;
    }

    /**
     * Get the default value for the given argument name.
     * @param name the argument name
     * @return the default value or null if not found
     */
    public Object getDefaultValue(String name) {
        if (name == null) return null;
        return defaultValues.get(name);
    }
}
