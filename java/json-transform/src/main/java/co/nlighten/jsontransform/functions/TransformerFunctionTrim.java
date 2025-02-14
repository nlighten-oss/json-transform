package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionTrim extends TransformerFunction {
    public TransformerFunctionTrim() {
        super(FunctionDescription.of(
                Map.of(
                        "type", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("BOTH")
                )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
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
