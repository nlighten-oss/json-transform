package co.nlighten.jsontransform.functions.common;

import java.util.HashMap;
import java.util.Map;

public class FunctionDescription {
    private final Map<String, ArgumentType> arguments;

    FunctionDescription(Map<String, ArgumentType> arguments) {
        this.arguments = arguments;
    }

    public static FunctionDescription of(Map<String, ArgumentType> arguments) {
        return new FunctionDescription(arguments);
    }

    public Map<String, ArgumentType> getArguments() {
        return arguments;
    }
}
