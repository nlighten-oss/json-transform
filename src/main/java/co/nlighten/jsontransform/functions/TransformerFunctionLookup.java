package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * For tests
 * @see TransformerFunctionLookupTest
 */
@Aliases("lookup")
@Documentation(value = "Joins multiple arrays of objects by a dynamic condition (transformer) on each pair of matches",
               notes = "```\n" +
                       "{\n" +
                       "  \"$$lookup\": {items1},\n" +
                       "  \"using\": [\n" +
                       "    { \"with\": {items2}, \"as\": \"name\", \"on\": {callback transformer (##current, ##index, ##{as})} }\n" +
                       "    { \"with\": {items2}, \"as\": \"name\", \"on\": {callback transformer (##current, ##index, ##{as})} }\n" +
                       "    ...\n" +
                       "  ],\n" +
                       "  \"to\": {callback transformer} // optional - default is (item1 <- item2 <- item3 ...; appends and overwrites)\n" +
                       "}\n" +
                       "```")
@InputType(ArgType.Array)
@ArgumentType(value = "using", type = ArgType.Array, position = 0, required = true,
              description = "Array of definitions of how to match other arrays to the main one")
@ArgumentType(value = "to", type = ArgType.Transformer, position = 1, defaultIsNull = true,
              description = "Transformer to map each pair of elements to its value in the result array")
@OutputType(ArgType.Array)
public class TransformerFunctionLookup<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    private class UsingEntry {
        public JA with;
        public String as;
        public JE on;
        public UsingEntry(JA with, String as, JE on) {
            this.with = with;
            this.as = as;
            this.on = on;
        }
    }

    public TransformerFunctionLookup(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var usingArray = context.getJsonArray("using", false);
        if (usingArray == null)
            return null;

        // prepare matches map (this will be used in each iteration to create the merged item)
        var matches = new HashMap<String, JE>();
        var usingMap = new HashMap<Integer, UsingEntry>();
        var usingArraySize = ARRAY.size(usingArray);
        for (var w = 0; w < usingArraySize; w++) {
            // prepare matches map
            var using = OBJECT.convert(ARRAY.get(usingArray, w));
            var asDef = OBJECT.get(using, "as");
            if (adapter.isNull(asDef))
                continue; // as - null
            var as = context.getAsString(asDef);
            matches.put("##" + as, null);

            // collect using
            var withDef = OBJECT.get(using, "with");
            if (adapter.isNull(withDef))
                continue; // with - null
            var with = context.transform(withDef);
            if (!ARRAY.is(with))
                continue; // with - not array
            usingMap.put(w, new UsingEntry((JA)with, as, OBJECT.get(using, "on")));
        }

        var to = context.has("to") ? context.getJsonElement("to", false) : null; // we don't transform definitions to prevent premature evaluation

        var index = new AtomicInteger(0);
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream().map(item1 -> {
            var i = index.getAndIncrement();
            for (var w = 0; w < usingArraySize; w++) {
                if (!usingMap.containsKey(w)) continue;
                var e = usingMap.get(w);

                JE match = null;
                var withSize = ARRAY.size(e.with);
                for (var j = 0; j < withSize; j++) {
                    var item2 = ARRAY.get(e.with, j);
                    var conditionResult = context.transformItem(e.on, item1, i, "##" + e.as, item2);
                    if (adapter.isTruthy(conditionResult)) {
                        match = item2;
                        break;
                    }
                }
                matches.put("##" + e.as, match);
            }

            if (to == null) {
                if (OBJECT.is(item1)) {
                    var merged = (JO)item1;
                    for (var val : matches.values()) {
                        if (!OBJECT.is(val))
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
