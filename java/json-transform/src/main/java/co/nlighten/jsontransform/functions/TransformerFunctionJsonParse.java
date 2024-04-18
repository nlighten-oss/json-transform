package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionJsonParseTest
 */
@Aliases({"jsonparse", "json"})
@DeprecatedAlias("json")
@Documentation("Parses input as JSON string")
@InputType(ArgType.String)
@OutputType(ArgType.Any)
public class TransformerFunctionJsonParse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionJsonParse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        return context.parse(str);
    }
}
