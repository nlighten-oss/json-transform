package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.common.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionLookup extends TransformerFunction {
    public TransformerFunctionLookup() {
        super(FunctionDescription.of(
            Map.of(
            "using", ArgumentType.of(ArgType.Array).position(0),
            "to", ArgumentType.of(ArgType.Transformer).position(1).defaultIsNull(true)
            )
        ));
    }

    private static class UsingEntry {
        public Object with;
        public String as;
        public Object on;
        public UsingEntry(Object with, String as, Object on) {
            this.with = with;
            this.as = as;
            this.on = on;
        }
    }

    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var usingArray = context.getJsonArray("using", false);
        if (usingArray == null)
            return null;

        // prepare matches map (this will be used in each iteration to create the merged item)
        var matches = new HashMap<String, Object>();
        var usingMap = new HashMap<Integer, UsingEntry>();
        var adapter = context.getAdapter();
        var usingArraySize = adapter.size(usingArray);
        for (var w = 0; w < usingArraySize; w++) {
            // prepare matches map
            var using = adapter.get(usingArray, w);
            var asDef = adapter.get(using, "as");
            if (adapter.isNull(asDef))
                continue; // as - null
            var as = context.getAsString(asDef);
            matches.put("##" + as, null);

            // collect using
            var withDef = adapter.get(using, "with");
            if (adapter.isNull(withDef))
                continue; // with - null
            var with = context.transform(context.getPathFor("with"), withDef);
            if (!adapter.isJsonArray(with))
                continue; // with - not array
            usingMap.put(w, new UsingEntry(with, as, adapter.get(using, "on")));
        }

        var to = context.has("to") ? context.getJsonElement("to", false) : null; // we don't transform definitions to prevent premature evaluation

        var index = new AtomicInteger(0);
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream().map(item1 -> {
            var i = index.getAndIncrement();
            for (var w = 0; w < usingArraySize; w++) {
                if (!usingMap.containsKey(w)) continue;
                var e = usingMap.get(w);

                Object match = null;
                var withSize = adapter.size(e.with);
                for (var j = 0; j < withSize; j++) {
                    var item2 = adapter.get(e.with, j);
                    var conditionResult = context.transformItem(e.on, item1, i, "##" + e.as, item2);
                    if (adapter.isTruthy(conditionResult)) {
                        match = item2;
                        break;
                    }
                }
                matches.put("##" + e.as, match);
            }

            if (to == null) {
                if (adapter.isJsonObject(item1)) {
                    var merged = item1;
                    for (var val : matches.values()) {
                        if (!adapter.isJsonObject(val))
                            continue; // edge case - tried to merge with an item which is not an object
                        merged = adapter.mergeInto(merged, val, null);
                    }
                    return merged;
                } else {
                    // edge case - tried to merge to an item which is not an object
                    return item1;
                }
            } else {
                return context.transformItem(to, item1, i, matches);
            }
        }));
    }
}
