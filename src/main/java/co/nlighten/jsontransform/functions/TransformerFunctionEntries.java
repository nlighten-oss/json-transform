package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * For tests
 * @see TransformerFunctionEntriesTest
 */
@Aliases("entries")
@Documentation(value = "Gets the entries* of an object or an array",
               notes = "* Entry is in the form of [ key / index, value ]")
@InputType({ArgType.Object, ArgType.Array})
@OutputType(ArgType.Array)
public class TransformerFunctionEntries<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionEntries(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var input = context.getJsonElement(null);
        if (ARRAY.is(input)) {
            var ja = (JA) input;
            var i = new AtomicInteger(0);
            return JsonElementStreamer.fromTransformedStream(context, ARRAY.stream(ja)
                    .map(a -> {
                        var entry = ARRAY.create(2);
                        ARRAY.add(entry, i.getAndIncrement());
                        ARRAY.add(entry, a);
                        return (JE)entry;
                    }));
        }
        if (OBJECT.is(input)) {
            var jo = (JO) input;
            return JsonElementStreamer.fromTransformedStream(context, OBJECT.entrySet(jo)
                    .stream()
                    .map(e -> {
                        var entry = ARRAY.create(2);
                        ARRAY.add(entry, e.getKey());
                        ARRAY.add(entry, e.getValue());
                        return (JE)entry;
                    }));
        }
        return null;
    }
}
