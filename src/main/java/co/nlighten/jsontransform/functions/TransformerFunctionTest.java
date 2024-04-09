package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.regex.Pattern;

/*
 * For tests
 * @see TransformerFunctionTestTest
 */
@Aliases("test")
@Documentation("Checks if a string matches a certain pattern")
@InputType(ArgType.String)
@ArgumentType(value = "pattern", type = ArgType.String, position = 0, required = true,
              description = "Regular expression to match against input string")
@OutputType(ArgType.Boolean)
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
