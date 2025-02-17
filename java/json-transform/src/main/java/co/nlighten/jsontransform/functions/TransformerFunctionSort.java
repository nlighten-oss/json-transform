package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.JsonElementStreamer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class TransformerFunctionSort extends TransformerFunction {
    public TransformerFunctionSort() {
        super(FunctionDescription.of(
                Map.of(
                        "by", ArgumentType.of(ArgType.Transformer).position(0),
                        "order", ArgumentType.of(ArgType.Enum).position(1).defaultEnum("ASC"),
                        "type", ArgumentType.of(ArgType.Enum).position(2).defaultEnum("AUTO"),
                        "then", ArgumentType.of(ArgType.Array).position(3).defaultIsNull(true)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var arr = context.getJsonElementStreamer(null);
        if (arr == null) {
            return null;
        }
        var type = context.getEnum("type");
        var order = context.getEnum("order");
        var descending = "DESC".equals(order);

        var adapter = context.getAdapter();
        if (!context.has("by")) {
            // does not have sort "by" (can sort inside the stream)
            Comparator<Object> comparator = type == null || "AUTO".equals(type)
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
            var chain = new ArrayList<>();

            var comparator = CompareBy.createByComparator(adapter, 0, type);
            if (descending) comparator = comparator.reversed();
            chain.add(by);

            var thenArr = context.has("then") ? context.getJsonArray("then", false) : null;
            if (thenArr != null) {
                var size = adapter.size(thenArr);
                for (var i = 0; i < size; i++) {
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

            return JsonElementStreamer.fromTransformedStream(context, arr.stream()
                .map(item -> {
                    var cb = new CompareBy(item);
                    cb.by = new ArrayList<>();
                    for (var jsonElement : chain) {
                        cb.by.add(context.transformItem(jsonElement, item));
                    }
                    return cb;
                })
                .sorted(comparator)
                .map(itm -> itm.value));
        }
    }
}
