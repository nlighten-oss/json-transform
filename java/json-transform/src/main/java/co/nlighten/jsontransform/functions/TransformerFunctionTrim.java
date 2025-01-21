package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

/*
 * For tests
 * @see TransformerFunctionTrimTest
 */
@ArgumentType(value = "type", type = ArgType.Enum, position = 0, defaultEnum = "BOTH")
public class TransformerFunctionTrim<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionTrim(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        return switch (context.getEnum("type")) {
            case "START" -> str.stripLeading();
            case "END" -> str.stripTrailing();
            case "INDENT" -> str.stripIndent();
            case "JAVA" -> str.trim();
            default -> str.strip();
        };
    }
}
