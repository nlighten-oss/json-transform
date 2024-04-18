package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionBooleanTest
 */
@Aliases("boolean")
@Documentation(value = "Evaluates input to boolean using the [Truthy logic]",
               notes = """
Strings evaluation depends on `style` argument:
- By default, value must be `"true"` for `true`.
- Unless `style` is set to `JS`, then any non-empty value is `true`. Arrays and objects of size 0 returns `false`.
""")
@InputType(ArgType.Any)
@ArgumentType(value = "style", type = ArgType.Enum, position = 0, defaultEnum = "JAVA",
              enumValues = { "JAVA", "JS" },
              description = "Style of considering truthy values (JS only relates to string handling; not objects and arrays)")
@OutputType(ArgType.Boolean)
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
