package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.regex.Pattern;

public class TransformerFunctionTest extends TransformerFunction {
    public TransformerFunctionTest() {
        super(FunctionDescription.of(
                Map.of(
                        "pattern", ArgumentType.of(ArgType.String).position(0)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return false;
        }
        var patternString = context.getString("pattern");
        if (patternString == null) {
            return false;
        }
        return Pattern.compile(patternString).matcher(str).find();
    }
}
