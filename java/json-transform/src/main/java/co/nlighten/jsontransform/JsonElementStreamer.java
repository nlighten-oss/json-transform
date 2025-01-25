package co.nlighten.jsontransform;

import co.nlighten.jsontransform.functions.common.FunctionContext;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JsonElementStreamer<JE, JA extends Iterable<JE>, JO extends JE> {
    private final FunctionContext<JE, JA, JO> context;
    private final boolean transformed;
    private JA value;
    private final Stream<JE> stream;

    private JsonElementStreamer(FunctionContext<JE, JA, JO> context, JA arr, boolean transformed) {
        this.context = context;
        this.value = arr;
        this.transformed = transformed;
        this.stream = null;
    }

    private JsonElementStreamer(FunctionContext<JE, JA, JO> context, Stream<JE> stream, boolean transformed) {
        this.context = context;
        this.value = null;
        this.transformed = transformed;
        this.stream = stream;
    }

    public boolean knownAsEmpty() {
        return value != null && this.context.jArray.isEmpty(value);
    }

    public Stream<JE> stream() {
        return stream(null, null);
    }

    public Stream<JE> stream(Long skip, Long limit) {
        if (this.stream != null && this.value == null) {
            var skipped = skip != null ? this.stream.skip(skip) : this.stream;
            return limit != null ? skipped.limit(limit) : skipped;
        }
        if (value == null) {
            return Stream.empty();
        }
        var valueStream = StreamSupport.stream(value.spliterator(), false);
        if (skip != null) {
            valueStream = valueStream.skip(skip);
        }
        if (limit != null) {
            valueStream = valueStream.limit(limit);
        }
        if (!transformed) {
            valueStream = valueStream.map(context::transform);
        }
        return valueStream;
    }

    public static <JE, JA extends Iterable<JE>, JO extends JE> JsonElementStreamer<JE, JA, JO> fromJsonArray(
            FunctionContext<JE, JA, JO> context, JA array, boolean transformed) {
        return new JsonElementStreamer<>(context, array, transformed);
    }

    public static <JE, JA extends Iterable<JE>, JO extends JE> JsonElementStreamer<JE, JA, JO> fromTransformedStream(
            FunctionContext<JE, JA, JO> context, Stream<JE> stream) {
        return new JsonElementStreamer<>(context, stream, true);
    }

    public JA toJsonArray() {
        if (value != null) {
            return value;
        }
        var ja = context.jArray.create();
        if (stream != null) {
            stream.forEach(item -> context.jArray.add(ja, item));
        }
        value = ja;
        return ja;
    }
}
