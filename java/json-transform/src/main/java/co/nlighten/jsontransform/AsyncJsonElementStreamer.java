package co.nlighten.jsontransform;

import co.nlighten.jsontransform.functions.common.FunctionContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class AsyncJsonElementStreamer {
    private final FunctionContext context;
    private final boolean transformed;
    private Object value;
    private final Stream<?> stream;

    private AsyncJsonElementStreamer(FunctionContext context, Stream<?> stream, Object arr, boolean transformed) {
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

    public static AsyncJsonElementStreamer fromJsonArray(
            FunctionContext context, Object array, boolean transformed) {
        return new AsyncJsonElementStreamer(context, null, array, transformed);
    }

    public static AsyncJsonElementStreamer fromTransformedStream(
            FunctionContext context, Stream<CompletionStage<?>> stream) {
        return new AsyncJsonElementStreamer(context, stream, null, true);
    }

    public CompletionStage<Object> toJsonArray() {
        if (value != null) {
            return CompletableFuture.completedStage(value);
        }
        var adapter = context.getAdapter();

        var result = adapter.createArray();
        var index = new AtomicInteger(0);
        return CompletableFuture.allOf(
            stream.map(d -> {
                var i = index.getAndIncrement();
                return d.thenApply(item -> {
                    adapter.set(result, i, item);
                    return null;
                }).toCompletableFuture();
            }).toArray(CompletableFuture[]::new)
        ).thenApply(v -> {
            this.value = result;
            return result;
        });
    }
}
