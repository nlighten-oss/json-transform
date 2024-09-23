package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

/*
 * For tests
 * @see TransformerFunctionIfTest
 */
@ArgumentType(value = "then", type = ArgType.Any, position = 0, defaultIsNull = true)
@ArgumentType(value = "else", type = ArgType.Any, position = 1, defaultIsNull = true)
public class TransformerFunctionIf<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionIf(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        boolean condition;
        if (context.has("then")) {
            var value = context.getJsonElement(null);
            if (adapter.isTruthy(value)) {
                return context.getJsonElement("then", true);
            } else if (context.has("else")) {
                return context.getJsonElement("else", true);
            }
        } else {
            var arr = context.getJsonArray(null);
            if (arr == null || jArray.size(arr) < 2)
                return null;
            var cje = jArray.get(arr, 0);
            if (adapter.isNull(cje)) {
                condition = false;
            } else if (adapter.isJsonBoolean(cje)) {
                condition = adapter.getBoolean(cje);
            } else {
                condition = adapter.isTruthy(context.transform(cje));
            }

            if (condition) {
                return context.transform(jArray.get(arr, 1));
            } else if (jArray.size(arr) > 2) {
                return context.transform(jArray.get(arr, 2));
            }
        }
        return null; // default falsy value
    }
}
