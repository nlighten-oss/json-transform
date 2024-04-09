package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;
import co.nlighten.jsontransform.functions.common.CompareBy;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/*
 * For tests
 * @see TransformerFunctionGroupTest
 */
@Aliases("group")
@Documentation(value = "Groups an array of entries into a map of key/arr[] by a specified transformer (with optional nested grouping)",
               notes = "Sorts elements of an array")
@InputType(ArgType.Array)
@ArgumentType(value = "by", type = ArgType.Transformer, position = 0, required = true,
              description = "A transformer to extract a property to group by (using ##current to refer to the current item)")
@ArgumentType(value = "order", type = ArgType.Enum, position = 1, defaultEnum = "ASC",
              enumValues = { "ASC", "DESC" },
              description = "Direction of ordering (ascending / descending)")
@ArgumentType(value = "type", type = ArgType.Enum, position = 2, defaultEnum = "AUTO",
              enumValues = { "AUTO", "STRING", "NUMBER", "BOOLEAN" },
              description = "Type of values to expect when ordering the input array")
@ArgumentType(value = "then", type = ArgType.Array, position = 3, defaultIsNull = true,
              description = "An array of subsequent grouping. When previous sort had no difference (only when `by` specified)\n" +
                            "{ \"by\": ..., \"order\": ..., \"type\": ...} // same 3 fields as above (`by` is required)")
@OutputType(ArgType.Object)
public class TransformerFunctionGroup<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionGroup(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    private JO add(JsonAdapter<JE, JA, JO> adapter, JO root, CompareBy<JE> by) {
        var elem = new AtomicReference<>(root);
        var byby = by.by;
        var bybySize = byby.size();
        for (var i = 0; i < bybySize - 1; i++) {
            var byKey = byby.get(i);
            // when adding a grouping key, fallback on empty string if null
            var key = adapter.isNull(byKey) ? "" : adapter.getAsString(byKey);
            elem.set(Objects.requireNonNullElseGet(adapter.OBJECT.convert(adapter.OBJECT.get(root, key)), () -> {
                var jo = adapter.OBJECT.create();
                adapter.OBJECT.add(elem.get(), key, jo);
                return jo;
            }));
        }
        var byKey = byby.get(bybySize - 1);
        // when adding a grouping key, fallback on empty string if null
        var key = adapter.isNull(byKey) ? "" : adapter.getAsString(byKey);
        var jArr = Objects.requireNonNullElseGet(
                (JA)adapter.OBJECT.get(elem.get(), key),
                adapter.ARRAY::create);
        adapter.ARRAY.add(jArr, by.value);
        adapter.OBJECT.add(elem.get(), key, jArr);
        return root;
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonElementStreamer(null);
        if (value == null) {
            return OBJECT.create();
        }
        var type = context.getEnum("type");
        var order = context.getEnum("order");
        var by = context.getJsonElement("by", false);
        if (adapter.isNull(by)) {
            return OBJECT.create();
        }

        var chain = new ArrayList<JE>();

        var comparator = CompareBy.createByComparator(adapter, 0, type);
        if ("DESC".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        chain.add(by);

        var thenArr = context.has("then") ? context.getJsonArray("then", false) : null;
        if (thenArr != null) {
            var thenArrSize = ARRAY.size(thenArr);
            for (var i = 0; i < thenArrSize; i++) {
                var thenObj = OBJECT.convert(ARRAY.get(thenArr, i));
                var thenType = OBJECT.has(thenObj, "type") ? context.getAsString(OBJECT.get(thenObj, "type")).trim() : null;
                var thenOrder = OBJECT.get(thenObj,"order");
                var thenComparator = CompareBy.createByComparator(adapter,i + 1, thenType);
                var thenDescending = !adapter.isNull(thenOrder) && context.getAsString(thenOrder).equalsIgnoreCase("DESC");
                if (thenDescending) {
                    thenComparator = thenComparator.reversed();
                }
                comparator = comparator.thenComparing(thenComparator);
                chain.add(OBJECT.get(thenObj,"by"));
            }
        }

        var result = OBJECT.create();
        value.stream()
                .map(item -> {
                    var cb = new CompareBy<>(item);
                    cb.by = new ArrayList<>();
                    for (JE jsonElement : chain) {
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
