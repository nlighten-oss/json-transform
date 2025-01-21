package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

/*
 * For tests
 * @see TransformerFunctionBooleanTest
 */
@ArgumentType(value = "style", type = ArgType.Enum, position = 0, defaultEnum = "JAVA")
public class TransformerFunctionBoolean<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionBoolean(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var jsStyle = "JS".equals(context.getEnum("style"));
        return adapter.isTruthy(context.getUnwrapped(null), jsStyle);
    }
}
