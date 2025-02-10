package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.manipulation.JsonPointer;

import java.util.Map;

/*
 * For tests
 * @see TransformerFunctionJsonPointerTest
 */
public class TransformerFunctionJsonPointer extends TransformerFunction {
    public TransformerFunctionJsonPointer() {
        super(FunctionDescription.of(
                Map.of(
                        "op", ArgumentType.of(ArgType.Enum).position(0).defaultString("GET"),
                        "pointer", ArgumentType.of(ArgType.String).position(1).defaultIsNull(true),
                        "value", ArgumentType.of(ArgType.Any).position(2).defaultIsNull(true)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var source = context.getJsonElement(null);
        if (source == null) {
            return null;
        }
        var pointer = context.getString("pointer");
        if (pointer == null) {
            return null;
        }
        var op = context.getEnum("op");
        var jsonPointer = new JsonPointer(context.getAdapter());
        return switch (op) {
            case "GET" -> jsonPointer.get(source, pointer);
            case "SET" -> jsonPointer.set(source, pointer, context.getJsonElement("value"));
            case "REMOVE" -> jsonPointer.remove(source, pointer);
            default -> null;
        };
    }
}
