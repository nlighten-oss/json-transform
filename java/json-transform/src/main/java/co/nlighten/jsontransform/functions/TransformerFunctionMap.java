package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.JsonElementStreamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class TransformerFunctionMap extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionMap.class);

    public TransformerFunctionMap() {
        super(FunctionDescription.of(
            Map.of(
            "to", ArgumentType.of(ArgType.Transformer).position(0).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        Stream<?> inputStream;
        Object to;
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
            var adapter = context.getAdapter();
            var inputEl = context.transform(context.getPathFor(0), adapter.get(arr, 0));
            if (!adapter.isJsonArray(inputEl)) {
                logger.warn("{} was not specified with an array of items", context.getAlias());
                return null;
            }
            inputStream = adapter.stream(inputEl);
            to = adapter.get(arr, 1);
        }
        var i = new AtomicInteger(0);
        return JsonElementStreamer.fromTransformedStream(context, inputStream
            .map(x -> context.transformItem(to, x, i.getAndIncrement()))
        );
    }
}
