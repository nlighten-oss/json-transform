package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.annotations.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TransformerFunctionTests {

    private static class TestFunctionContext implements FunctionContext {
        private final List<String> array;
        private final Integer index;

        public TestFunctionContext(List<String> array, Integer index) {
            this.array = array;
            this.index = index;
        }

        public List<String> getArray() {
            return array;
        }

        public Integer getIndex() {
            return index;
        }
    }


    @Aliases("at")
    @Documentation("Retrieves an element from a specific position inside an input array")
    @InputType(value = ArgType.Array, description = "Array to fetch from")
    @ArgumentType(value = "index", type = ArgType.Integer, position = 0, required = true, defaultIsNull = true,
            description = "Index of element to fetch (negative values will be fetch from the end)")
    @OutputType(value = ArgType.Any, description = "Element at index, or null if undefined")
    private static class TransformerFunctionAt extends TransformerFunction<TestFunctionContext> {

        @Override
        public Object apply(TestFunctionContext context) {
            var array = context.getArray();
            var index = context.getIndex();
            return index == null ? null : index >= 0 ? array.get(index) : array.get(array.size() + index);
        }
    }

    @Test
    void sanity() {
        var atFunction = new TransformerFunctionAt();
        Assertions.assertEquals("B", atFunction.apply(new TestFunctionContext(List.of("A", "B"), 1)));
        Assertions.assertEquals("C", atFunction.apply(new TestFunctionContext(List.of("A", "B", "C", "D"), -2)));
    }
}
