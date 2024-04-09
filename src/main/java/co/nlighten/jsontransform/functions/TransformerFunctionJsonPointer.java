package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.manipulation.JsonPointer;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionJsonPointerTest
 */
@Aliases({"jsonpointer"})
@Documentation("Apply mutations on object paths using JSON Pointer (defined by RFC-6901)")
@InputType(ArgType.Any)
@ArgumentType(value = "op", type = ArgType.Enum, position = 0, defaultString = "GET",
              description = "Operation", enumValues = { "GET", "SET", "REMOVE" })
@ArgumentType(value = "pointer", type = ArgType.String, position = 1, defaultIsNull = true,
              description = "JSON Pointer to apply operation on")
@ArgumentType(value = "value", type = ArgType.Any, position = 2, defaultIsNull = true,
              description = "Value to use")
@OutputType(ArgType.Any)
public class TransformerFunctionJsonPointer<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    private final JsonPointer<JE, JA, JO> jsonPointer;

    public TransformerFunctionJsonPointer(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
        this.jsonPointer = new JsonPointer<>(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var source = context.getJsonElement(null);
        if (source == null) {
            return null;
        }
        var pointer = context.getString("pointer");
        if (pointer == null) {
            return null;
        }
        var op = context.getEnum("op");

        return switch (op) {
            case "GET" -> jsonPointer.get(source, pointer);
            case "SET" -> jsonPointer.set(source, pointer, context.getJsonElement("value"));
            case "REMOVE" -> jsonPointer.remove(source, pointer);
            default -> null;
        };
    }
}
