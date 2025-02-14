package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TransformerFunctionGroup extends TransformerFunction {

    public TransformerFunctionGroup() {
        super(FunctionDescription.of(
            Map.of(
            "by", ArgumentType.of(ArgType.Transformer).position(0),
            "order", ArgumentType.of(ArgType.Enum).position(1).defaultEnum("ASC"),
            "type", ArgumentType.of(ArgType.Enum).position(2).defaultEnum("AUTO"),
            "then", ArgumentType.of(ArgType.Array).position(3).defaultIsNull(true)
            )
        ));
    }

    private Object add(JsonAdapter<?, ?, ?> adapter, Object root, CompareBy by) {
        var elem = new AtomicReference<>(root);
        var byby = by.by;
        var bybySize = byby.size();
        for (var i = 0; i < bybySize - 1; i++) {
            var byKey = byby.get(i);
            // when adding a grouping key, fallback on empty string if null
            var key = adapter.isNull(byKey) ? "" : adapter.getAsString(byKey);
            var val = adapter.get(root, key);
            elem.set(Objects.requireNonNullElseGet(adapter.isJsonObject(val) ? val : null, () -> {
                var jo = adapter.createObject();
                adapter.add(elem.get(), key, jo);
                return jo;
            }));
        }
        var byKey = byby.get(bybySize - 1);
        // when adding a grouping key, fallback on empty string if null
        var key = adapter.isNull(byKey) ? "" : adapter.getAsString(byKey);
        var jArr = Objects.requireNonNullElseGet(
                adapter.get(elem.get(), key),
                adapter::createArray);
        adapter.add(jArr, by.value);
        adapter.add(elem.get(), key, jArr);
        return root;
    }

    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var adapter = context.getAdapter();
        var value = context.getJsonElementStreamer(null);
        if (value == null) {
            return adapter.createObject();
        }
        var type = context.getEnum("type");
        var order = context.getEnum("order");
        var by = context.getJsonElement("by", false);
        if (adapter.isNull(by)) {
            return adapter.createObject();
        }

        var chain = new ArrayList<>();

        var comparator = CompareBy.createByComparator(adapter, 0, type);
        if ("DESC".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        chain.add(by);

        var thenArr = context.has("then") ? context.getJsonArray("then", false) : null;
        if (thenArr != null) {
            var thenArrSize = adapter.size(thenArr);
            for (var i = 0; i < thenArrSize; i++) {
                var thenObj = adapter.get(thenArr, i);
                var thenType = adapter.has(thenObj, "type") ? context.getAsString(adapter.get(thenObj, "type")).trim() : null;
                var thenOrder = adapter.get(thenObj,"order");
                var thenComparator = CompareBy.createByComparator(adapter,i + 1, thenType);
                var thenDescending = !adapter.isNull(thenOrder) && context.getAsString(thenOrder).equalsIgnoreCase("DESC");
                if (thenDescending) {
                    thenComparator = thenComparator.reversed();
                }
                comparator = comparator.thenComparing(thenComparator);
                chain.add(adapter.get(thenObj,"by"));
            }
        }

        var result = adapter.createObject();
        value.stream()
                .map(item -> {
                    var cb = new CompareBy(item);
                    cb.by = new ArrayList<>();
                    for (var jsonElement : chain) {
                        var byKey = context.transformItem(jsonElement, item);
                        cb.by.add(byKey == null ? adapter.jsonNull() : byKey);
                    }
                    return cb;
                })
                .sorted(comparator)
                .forEachOrdered(itm -> add(adapter, result, itm));
        return result;
    }
}
