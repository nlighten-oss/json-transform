package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.common.CompareBy;
import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.ArrayList;
import java.util.Comparator;

/*
 * For tests
 * @see TransformerFunctionSortTest
 */
@Aliases("sort")
@Documentation("Sorts elements of an array")
@InputType(ArgType.Array)
@ArgumentType(value = "by", type = ArgType.Transformer, position = 0, required = true,
              description = "A transformer to extract a property to sort by (using ##current to refer to the current item)")
@ArgumentType(value = "order", type = ArgType.Enum, position = 1, defaultEnum = "ASC",
              enumValues = { "ASC", "DESC" },
              description = "Direction of ordering (ascending / descending)")
@ArgumentType(value = "type", type = ArgType.Enum, position = 2, defaultEnum = "AUTO",
              enumValues = { "AUTO", "STRING", "NUMBER", "BOOLEAN" },
              description = "Type of values to expect when ordering the input array")
@ArgumentType(value = "then", type = ArgType.Array, position = 3, defaultIsNull = true,
              description = "An array of secondary sorting in case previous sorting returned equal, first being the root fields (Every item require the `by` field while other fields are optional)\n" +
                      "{ \"by\": ..., \"order\": ..., \"type\": ...} // same 3 fields as above (`by` is required)")
@OutputType(ArgType.Array)
@TypeIsPiped
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
                var size = ARRAY.size(thenArr);
                for (var i = 0; i < size; i++) {
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
