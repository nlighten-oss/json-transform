package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.common.CompareBy;
import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.util.ArrayList;
import java.util.Comparator;

/*
 * For tests
 * @see TransformerFunctionSortTest
 */
@ArgumentType(value = "by", type = ArgType.Transformer, position = 0)
@ArgumentType(value = "order", type = ArgType.Enum, position = 1, defaultEnum = "ASC")
@ArgumentType(value = "type", type = ArgType.Enum, position = 2, defaultEnum = "AUTO")
@ArgumentType(value = "then", type = ArgType.Array, position = 3, defaultIsNull = true)
public class TransformerFunctionSort<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionSort(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var arr = context.getJsonElementStreamer(null);
        if (arr == null) {
            return null;
        }
        var type = context.getEnum("type");
        var order = context.getEnum("order");
        var descending = "DESC".equals(order);

        if (!context.has("by")) {
            // does not have sort "by" (can sort inside the stream)
            Comparator<JE> comparator = type == null || "AUTO".equals(type)
                             ? adapter.comparator()
                             : switch (type) {
                                  case "NUMBER" -> Comparator.comparing(adapter::getNumberAsBigDecimal);
                                  case "BOOLEAN" -> Comparator.comparing(adapter::getBoolean);
                                  //case "string"
                                  default -> Comparator.comparing(adapter::getAsString);
                               };
            return JsonElementStreamer.fromTransformedStream(context, arr.stream()
                        .sorted(descending ? comparator.reversed() : comparator)
                                                            );
        } else {
            var by = context.getJsonElement( "by", false);
            var chain = new ArrayList<JE>();

            var comparator = CompareBy.createByComparator(adapter, 0, type);
            if (descending) comparator = comparator.reversed();
            chain.add(by);

            var thenArr = context.has("then") ? context.getJsonArray("then", false) : null;
            if (thenArr != null) {
                var size = jArray.size(thenArr);
                for (var i = 0; i < size; i++) {
                    var thenObj = jObject.convert(jArray.get(thenArr, i));
                    var thenType = jObject.has(thenObj, "type") ? context.getAsString(jObject.get(thenObj, "type")).trim() : null;
                    var thenOrder = jObject.get(thenObj,"order");
                    var thenComparator = CompareBy.createByComparator(adapter,i + 1, thenType);
                    var thenDescending = !adapter.isNull(thenOrder) && context.getAsString(thenOrder).equalsIgnoreCase("DESC");
                    if (thenDescending) {
                        thenComparator = thenComparator.reversed();
                    }
                    comparator = comparator.thenComparing(thenComparator);
                    chain.add(jObject.get(thenObj,"by"));
                }
            }

            return JsonElementStreamer.fromTransformedStream(context, arr.stream()
                .map(item -> {
                    var cb = new CompareBy<>(item);
                    cb.by = new ArrayList<>();
                    for (JE jsonElement : chain) {
                        cb.by.add(context.transformItem(jsonElement, item));
                    }
                    return cb;
                })
                .sorted(comparator)
                .map(itm -> itm.value));
        }
    }
}
