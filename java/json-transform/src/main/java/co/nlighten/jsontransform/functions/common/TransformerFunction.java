package co.nlighten.jsontransform.functions.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Base class for all transformer functions.
 */
public abstract class TransformerFunction {
    private final Map<String, Object> defaultValues;
    private final FunctionDescription description;

    public TransformerFunction(FunctionDescription description) {
        this.description = description;
        this.defaultValues = new HashMap<>();
        var args = description.getArguments();
        for (var arg : args.keySet()) {
            this.defaultValues.put(arg, TransformerFunction.getDefaultValue(args.get(arg)));
        }
    }

    public TransformerFunction() {
        this(FunctionDescription.of(Collections.emptyMap()));
    }


    private static Object getDefaultValue(ArgumentType a) {
        if (a == null || a.defaultIsNull) return null;
        return switch (a.type) {
            case Boolean -> a.defaultBoolean;
            case String -> a.defaultString;
            case Enum -> a.defaultEnum;
            case Integer -> a.defaultInteger;
            case Long -> a.defaultLong;
            case BigDecimal -> a.defaultBigDecimal;
            default -> null;
        };
    }

    /**
     * Apply the function to the given context.
     * @param context the context
     * @return the result of the function
     */
    public abstract CompletionStage<Object> apply(FunctionContext context);

    /**
     * Get the argument type for the given name.
     * @param name the name of the argument (null will return the primary argument)
     * @return the argument type or null if not found
     */
    public ArgumentType getArgument(String name) {
        if (name == null) return null;
        return description.getArguments().get(name);
    }

    /**
     * Get the arguments for this function.
     * @return the function arguments
     */
    public Map<String, ArgumentType> getArguments() {
        return description.getArguments();
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
