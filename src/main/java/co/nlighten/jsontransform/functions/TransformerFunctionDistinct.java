package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.Objects;

/*
 * For tests
 * @see TransformerFunctionDistinctTest
 */
@Aliases("distinct")
@Documentation("Returns a distinct array (repeating elements removed, only primitive values are supported if no `by` was specified)")
@InputType(ArgType.Array)
@ArgumentType(value = "by", type = ArgType.Transformer, position = 0, defaultIsNull = true,
              description = "A mapping for each element to distinct by (instead of the whole element, using ##current to refer to the current item)")
@OutputType(ArgType.Array)
@TypeIsPiped
public class TransformerFunctionDistinct<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionDistinct(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) return null;
        var by = context.getJsonElement( "by", false);
        if (!adapter.isNull(by)) {
            return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
                        .map(item -> {
                            var toDistinctBy = context.transformItem(by, item);
                            return new EquatableTuple<>(item, toDistinctBy);
                        })
                        .distinct()
                        .map(EquatableTuple::unwrap)
            );
        } else {
            return JsonElementStreamer.fromTransformedStream(context, streamer.stream().distinct());
        }
    }

    private record EquatableTuple<T, R>(T value, R compareValue) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof EquatableTuple<?, ?> lct)) {
                return false;
            }
            return Objects.equals(lct.compareValue, this.compareValue);
        }

        @Override
        public int hashCode() {
            return this.compareValue.hashCode();
        }

        public T unwrap() {
            return value;
        }
    }
}
