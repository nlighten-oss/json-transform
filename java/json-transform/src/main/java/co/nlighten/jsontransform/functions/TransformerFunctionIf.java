package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionIfTest
 */
@Aliases("if")
@Documentation("Conditionally returns a value, if the evaluation of the condition argument is truthy (using the [Truthy logic]). A fallback value (if condition evaluates to false) is optional")
@InputType(value = {ArgType.Array, ArgType.Any}, description = "Either a value to evaluate for condition, or an Array of size 2 / 3 (with `condition`, `then` and optionally `else`)")
@ArgumentType(value = "then", type = ArgType.Any, position = 0, defaultIsNull = true,
              description = "Value to return if condition is true")
@ArgumentType(value = "else", type = ArgType.Any, position = 1, defaultIsNull = true,
              description = "Value to return if condition is false")
@OutputType(ArgType.Any)
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
