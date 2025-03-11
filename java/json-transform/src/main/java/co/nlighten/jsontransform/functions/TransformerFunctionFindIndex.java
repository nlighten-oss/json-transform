package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Iterator;
import java.util.Map;

public class TransformerFunctionFindIndex extends TransformerFunction {
    public TransformerFunctionFindIndex() {
        super(FunctionDescription.of(
            Map.of(
            "by", ArgumentType.of(ArgType.Transformer).position(0).defaultIsNull(true)
            )));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var hasBy = context.has("by");
        var by = context.getJsonElement("by", false);
        var adapter = context.getAdapter();
        var i = 0;
        boolean found = false;
        for (Iterator<?> iter = streamer.stream().iterator(); iter.hasNext();) {
            var item = iter.next();
            if (!hasBy) {
                if (adapter.isTruthy(item)) {
                    found = true;
                    break;
                }
            }
            var condition = context.transformItem(by, item, i++);
            if (adapter.isTruthy(condition)) {
                found = true;
                break;
            }
        }
        return found ? i - 1 : -1;
    }
}
