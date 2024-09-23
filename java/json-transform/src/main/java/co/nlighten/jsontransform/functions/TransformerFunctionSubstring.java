package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

/*
 * For tests
 * @see TransformerFunctionSubstringTest
 */
@ArgumentType(value = "begin", type = ArgType.Integer, position = 0, defaultInteger = 0)
@ArgumentType(value = "end", type = ArgType.Integer, position = 1, defaultIsNull = true)
public class TransformerFunctionSubstring<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionSubstring(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var length = str.length();
        var beginIndex = context.getInteger("begin");
        if (beginIndex == null) {
            return str;
        }
        if (beginIndex < 0) {
            beginIndex = Math.max(0, length + beginIndex);
        }
        var endValue = context.getInteger("end");
        if (endValue == null) {
            return str.substring(beginIndex);
        }
        var endIndex = Math.min(endValue, length);
        if (endIndex < 0) {
            endIndex = Math.max(0, length + endIndex);
        }
        return str.substring(beginIndex, endIndex);
    }
}
