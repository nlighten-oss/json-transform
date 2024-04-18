package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;
import co.nlighten.jsontransform.JsonElementStreamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/*
 * For tests
 * @see TransformerFunctionMapTest
 */
@Aliases("map")
@Documentation("Returns a mapped array applying the transformer on each of the elements")
@InputType(ArgType.Array)
@ArgumentType(value = "to", type = ArgType.Transformer, position = 0, defaultIsNull = true,
              description = "Transformer to map each element to its value in the result array (inputs: ##current, ##index)")
@OutputType(ArgType.Array)
public class TransformerFunctionMap<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionMap.class);

    public TransformerFunctionMap(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        Stream<JE> inputStream;
        JE to;
        if (context.has("to")) {
            var streamer = context.getJsonElementStreamer(null);
            if (streamer == null)
                return null;
            inputStream = streamer.stream();
            to = context.getJsonElement("to", false); // we don't transform definitions to prevent premature evaluation
        } else {
            // [ input, to ]
            var arr = context.getJsonArray(null, false); // we don't transform definitions to prevent premature evaluation
            if (arr == null)
                return null;
            var inputEl = context.transform(jArray.get(arr, 0));
            if (!jArray.is(inputEl)) {
                logger.warn("{} was not specified with an array of items", context.getAlias());
                return null;
            }
            inputStream = jArray.stream((JA)inputEl);
            to = jArray.get(arr, 1);
        }
        var i = new AtomicInteger(0);
        return JsonElementStreamer.fromTransformedStream(context, inputStream
            .map(x -> context.transformItem(to, x, i.getAndIncrement()))
        );
    }
}
