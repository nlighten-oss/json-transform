package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TransformerFunctionPartition extends TransformerFunction {
    public TransformerFunctionPartition() {
        super(FunctionDescription.of(
                Map.of(
                        "size", ArgumentType.of(ArgType.Integer).position(0).defaultInteger(100)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonArray(null);
        if (value == null) {
            return null;
        }
        var size = context.getInteger("size");

        var adapter = context.getAdapter();
        var list = adapter.createArray();
        IntStream.range(0, adapter.size(value))
                .boxed()
                .collect(Collectors.groupingBy(e -> e / size, Collectors.mapping(
                        i -> adapter.get(value, i),
                        Collectors.toList())))
                .values().forEach(x -> {
                    var subList = adapter.createArray();
                    x.forEach(item -> adapter.add(subList, item));
                    adapter.add(list, subList);
                });
        return list;
    }
}
