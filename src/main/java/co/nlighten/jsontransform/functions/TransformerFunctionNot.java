package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionNotTest
 */
@Aliases("not")
@Documentation("Returns the opposite of the argument's boolean value (this returns the opposite of `$$boolean`)")
@InputType(ArgType.Object)
@ArgumentType(value = "style", type = ArgType.Enum, position = 0, defaultEnum = "JAVA",
              description = "Style of considering truthy values (JAVA/JS) (JS only relates to string handling; not objects and arrays)")
@OutputType(ArgType.Boolean)
public class TransformerFunctionNot<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionNot(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var jsStyle = "JS".equals(context.getEnum("style"));
        return !adapter.isTruthy(context.get(null), jsStyle);
    }
}
