package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * For tests
 * @see TransformerFunctionPartitionTest
 */
@Aliases("partition")
@Documentation("Partition an array to multiple constant size arrays")
@InputType(ArgType.Array)
@ArgumentType(value = "size", type = ArgType.Integer, position = 0, defaultInteger = 100, required = true,
              description = "The size of each partition")
@OutputType(value = ArgType.ArrayOfArray, description = "of same items type as input")
public class TransformerFunctionPartition<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionPartition(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonArray(null);
        if (value == null) {
            return null;
        }
        var size = context.getInteger("size");

        var list = ARRAY.create();
        IntStream.range(0, ARRAY.size(value))
                .boxed()
                .collect(Collectors.groupingBy(e -> e / size, Collectors.mapping(
                        i -> ARRAY.get(value, i),
                        Collectors.toList())))
                .values().forEach(x -> {
                    var subList = ARRAY.create();
                    x.forEach(item -> ARRAY.add(subList, item));
                    ARRAY.add(list, subList);
                });
        return list;
    }
}
