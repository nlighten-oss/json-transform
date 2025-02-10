package co.nlighten.jsontransform;

import co.nlighten.jsontransform.functions.common.FunctionContext;

import java.util.stream.Stream;

public class JsonElementStreamer {
    private final FunctionContext context;
    private final boolean transformed;
    private Object value;
    private final Stream<?> stream;

    private JsonElementStreamer(FunctionContext context, Stream<?> stream, Object arr, boolean transformed) {
        this.context = context;
        this.value = arr;
        this.transformed = transformed;
        this.stream = stream;
    }

    public boolean knownAsEmpty() {
        return value != null && this.context.getAdapter().isEmpty(value);
    }

    public Stream<?> stream() {
        return stream(null, null);
    }

    public Stream<?> stream(Long skip, Long limit) {
        if (this.stream != null && this.value == null) {
            var skipped = skip != null ? this.stream.skip(skip) : this.stream;
            return limit != null ? skipped.limit(limit) : skipped;
        }
        if (value == null) {
            return Stream.empty();
        }
        var valueStream = context.getAdapter().stream(value, false);
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

    public static JsonElementStreamer fromJsonArray(
            FunctionContext context, Object array, boolean transformed) {
        return new JsonElementStreamer(context, null, array, transformed);
    }

    public static JsonElementStreamer fromTransformedStream(
            FunctionContext context, Stream<?> stream) {
        return new JsonElementStreamer(context, stream, null, true);
    }

    public Object toJsonArray() {
        if (value != null) {
            return value;
        }
        var adapter = context.getAdapter();
        var ja = adapter.createArray();
        if (stream != null) {
            stream.forEach(item -> adapter.add(ja, item));
        }
        value = ja;
        return ja;
    }
}
