package co.nlighten.jsontransform;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

// @src https://dzone.com/articles/streaming-java-completablefutures-in-completion-or
public final class CompletionOrderSpliterator<T> implements Spliterator<T> {

    private final Map<Integer, CompletableFuture<Map.Entry<Integer, T>>> indexedFutures;

    private CompletionOrderSpliterator(Collection<CompletableFuture<T>> futures) {
        indexedFutures = toIndexedFutures(futures);
    }

    private static <T> Map<Integer, CompletableFuture<Map.Entry<Integer, T>>> toIndexedFutures(Collection<CompletableFuture<T>> futures) {
        // pre-sizing the HashMap since we know the capacity and expected collisions count (0)
        var map = new HashMap<Integer, CompletableFuture<Map.Entry<Integer, T>>>(futures.size(), 1);
        int seq = 0;
        for (CompletableFuture<T> future : futures) {
            int index = seq++;
            map.put(index, future.thenApply(value -> new AbstractMap.SimpleEntry<>(index, value)));
        }
        return map;
    }

    public static <T> CompletionOrderSpliterator<T> of(Collection<CompletableFuture<T>> futures) {
        return new CompletionOrderSpliterator<>(futures);
    }

    private T nextCompleted() {
        return CompletableFuture.anyOf(indexedFutures.values()
                .toArray(new CompletableFuture[0]))
                .thenApply(result -> ((Map.Entry<Integer, T>) result))
                .thenApply(result -> {
                    indexedFutures.remove(result.getKey());
                    return result.getValue();
                }).join();
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (!indexedFutures.isEmpty()) {
            action.accept(nextCompleted());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null; // because splitting is not allowed
    }

    @Override
    public long estimateSize() {
        return indexedFutures.size(); // because we know the size
    }

    @Override
    public int characteristics() {
        return
                SIZED       // because we know the size upfront
                        | IMMUTABLE // because the source can be safely modified
                        | NONNULL;  // because nulls in source are not accepted
    }
}
