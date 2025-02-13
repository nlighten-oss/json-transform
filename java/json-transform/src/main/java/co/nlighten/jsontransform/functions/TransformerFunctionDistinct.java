package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.JsonElementStreamer;

import java.util.Map;
import java.util.Objects;

/*
 * For tests
 * @see TransformerFunctionDistinctTest
 */
public class TransformerFunctionDistinct extends TransformerFunction {
    public TransformerFunctionDistinct() {
        super(FunctionDescription.of(
            Map.of(
            "by", ArgumentType.of(ArgType.Transformer).position(0).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) return null;
        var by = context.getJsonElement( "by", false);
        var adapter = context.getAdapter();
        if (!adapter.isNull(by)) {
            return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
                        .map(item -> {
                            var toDistinctBy = context.transformItem(by, item);
                            return new EquatableTuple<>(adapter, item, toDistinctBy);
                        })
                        .distinct()
                        .map(EquatableTuple::unwrap)
            );
        } else if (!adapter.nodesComparable()) {
            return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
                    .map(item -> new EquatableTuple<>(adapter, item, item))
                    .distinct()
                    .map(EquatableTuple::unwrap));
        } else {
            return JsonElementStreamer.fromTransformedStream(context, streamer.stream().distinct());
        }
    }

    private record EquatableTuple<T, R>(JsonAdapter<?,?,?> adapter, T value, R compareValue) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof EquatableTuple<?, ?> lct)) {
                return false;
            }
            return adapter.areEqual(lct.compareValue, this.compareValue);
        }

        @Override
        public int hashCode() {
            return adapter.hashCode(this.compareValue);
        }

        public T unwrap() {
            return value;
        }
    }
}
