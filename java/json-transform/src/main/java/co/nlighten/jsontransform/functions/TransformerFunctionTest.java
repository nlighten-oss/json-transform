package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.util.regex.Pattern;

/*
 * For tests
 * @see TransformerFunctionTestTest
 */
@ArgumentType(value = "pattern", type = ArgType.String, position = 0)
public class TransformerFunctionTest<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionTest(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
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
