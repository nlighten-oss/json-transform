package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.ArgumentType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionDescription;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionsTest extends MultiAdapterBaseTest {
    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMultipleInlineFunctions(JsonAdapter<?,?,?> adapter) {
        var date = Instant.now();
        var now = date.toString();
        var text = "text";
        assertTransformation(adapter, now, "$$substring(0,10):$$date(iso):$", date.toString().substring(0,10));
        assertTransformation(adapter, text, "$$substring(1,3):$$wrap(>):$", "te");
        assertTransformation(adapter, text, "$$wrap(>):$$substring(1,3):$", ">ex");
        assertTransformation(adapter, text, "$$join(s):$$split(x):$", "test");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMultipleObjectFunctions(JsonAdapter<?,?,?> adapter) {
        assertTransformation(adapter, null, adapter.parse("""
{
  "$$distinct": { "$$flat": [ "$$split:abcd", "$$split:def" ] }
}
"""), adapter.parse("""
[ "a", "b", "c", "d", "e", "f"]"""));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void inlineInnerFailure(JsonAdapter<?,?,?> adapter) {
        // even though $$partition fails, $$string picks up the null and transforms it
        assertTransformation(adapter, adapter.parse("[1]"), "$$string(true):$$partition(0):$", "null");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testFunctionContextNesting(JsonAdapter<?,?,?> adapter) {
        assertTransformation(adapter, null, adapter.parse("""
{
  "$$map": [ [true, "a"], [true, "b"], [false, "c"] ],
  "to": {
    "$$map": "##current",
    "context": {
        "##map":"##current"
    },
    "to": {
        "$$if": "##map[0]",
        "then": "$$upper:##map[1]",
        "else": "$$upper:##current"
    }
  }
}
"""), adapter.parse("""
[["A", "A"], ["B", "B"], ["FALSE", "C"]]"""));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJsonElementStreamsImmediateEvaluation(JsonAdapter<?,?,?> adapter) {
        var funcName = "foo" + Math.abs(adapter.toString().hashCode());
        var callCount = new AtomicInteger();
        TransformerFunctions.registerFunctions(Map.entry(funcName, new TransformerFunction() {
            @Override
            public CompletionStage<Object> apply(FunctionContext context) {
                callCount.incrementAndGet();
                return context.get(null);
            }
        }));

        callCount.set(0);
        assertTransformation(adapter, null, adapter.parse("""
{
  "$$find": {
      "$$map": ["$$foo:a", "$$foo:b", "$$foo:c", "$$foo:d", "$$foo:e"],
      "to": "$$upper:##current"
  },
  "by": "$$is(=,B):##current"
}
""".replace("foo", funcName)), "B");
        // assert that "c" was called twice (check A, check B, break)
        assertEquals(adapter, 2, callCount.get());

        callCount.set(0);
        assertTransformation(adapter, null, adapter.parse("""
{
  "$$is": "b",
  "in": ["$$foo:a", "$$foo:b", "$$foo:c", "$$foo:d"]
}
""".replace("foo", funcName)), true);
        // assert that "c" was called twice (check a, check b, break)
        assertEquals(adapter, 2, callCount.get());

        callCount.set(0);
        assertTransformation(adapter, false, adapter.parse("""
{
  "$$and": ["$$foo:$", "$$foo:a", "$$foo:b"]
}
""".replace("foo", funcName)), false);
        // assert that "c" was called once (check false, break)
        assertEquals(adapter, 1, callCount.get());

        callCount.set(0);
        assertTransformation(adapter, false, adapter.parse("""
{
  "$$or": ["$$foo:true", "$$foo:$"]
}
""".replace("foo", funcName)), true);
        // assert that "c" was called once (check true, break)
        assertEquals(adapter, 1, callCount.get());

        callCount.set(0);
        assertTransformation(adapter, false, adapter.parse("""
{
  "$$first": {
    "$$map": [1, 2, 3],
    "to": "$$foo:##current"
  }
}
""".replace("foo", funcName)), adapter.parse("1"));
        // assert that "c" was called once (mapped first, break)
        assertEquals(adapter, 1, callCount.get());

        callCount.set(0);
        assertTransformation(adapter, null, adapter.parse("""
{
  "$$at": ["$$foo:a", "$$foo:b", "$$foo:c", "$$foo:d"],
  "index": 2
}
""".replace("foo", funcName)), "c");
        // assert that "c" was called once (skipped 2, break)
        assertEquals(adapter, 1, callCount.get());


        callCount.set(0);
        assertTransformation(adapter, null, adapter.parse("""
{
  "$$slice": ["$$foo:a", "$$foo:b", "$$foo:c", "$$foo:d"],
  "begin": 1,
  "end": 3
}
""".replace("foo", funcName)), adapter.parse("[\"b\",\"c\"]"));
        // assert that "c" was called twice (skipped 1, eval 2, break)
        assertEquals(adapter, 2, callCount.get());
    }

    private static class TransformerFunctionArgsTest extends TransformerFunction {
        public TransformerFunctionArgsTest() {
            super(FunctionDescription.of(
                Map.of("a", ArgumentType.of(ArgType.Any).position(0).defaultIsNull(true),
                       "b", ArgumentType.of(ArgType.Any).position(1).defaultIsNull(true),
                       "c", ArgumentType.of(ArgType.Any).position(2).defaultIsNull(true)
                )
            ));
        }

        @Override
        public CompletionStage<Object> apply(FunctionContext context) {
            if (!context.has("a"))
                return "N/A";
            var lst = new ArrayList<>();
            lst.add(context.get("a"));
            if (context.has("b")) {
                lst.add(context.get("b"));
            }
            if (context.has("c")) {
                lst.add(context.get("c"));
            }
            var adapter = context.getAdapter();
            if (lst.size() == 1) {
                return adapter.isNull(lst.get(0)) ? "[NULL]" : lst.get(0);
            }
            return String.join(",", lst.stream().map(arg ->
                 "[" + (adapter.isNull(arg) ? "NULL" : adapter.getAsString(arg)) + "]"
            ).toList());
        }
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void inlineArgsParsingTest(JsonAdapter<?,?,?> adapter) {
        TransformerFunctions.registerFunctions(Map.entry("argstest", new TransformerFunctionArgsTest()));
        assertTransformation(adapter, null, "$$argstest(,):", "[],[]");

        assertTransformation(adapter, null, "$$argstest", "N/A");
        assertTransformation(adapter, null, "$$argstest:", "N/A");
        assertTransformation(adapter, null, "$$argstest()", "N/A");
        assertTransformation(adapter, null, "$$argstest():", "N/A");
        assertTransformation(adapter, null, "$$argstest(,):", "[],[]");
        assertTransformation(adapter, null, "$$argstest(#null):", "[NULL]");
        assertTransformation(adapter, null, "$$argstest(null,#null):", "[null],[NULL]");
        assertTransformation(adapter, null, "$$argstest(a):", "a");
        assertTransformation(adapter, "A", "$$argstest($):", "A");
        assertTransformation(adapter, "A", "$$argstest(\\$):", "$");
        assertTransformation(adapter, "A", "$$argstest(\\$,\\$):", "[$],[$]");
        assertTransformation(adapter, "A", "$$argstest('\\\\$'):", "$");
        assertTransformation(adapter, "A", "$$argstest('\\\\$','\\\\$'):", "[$],[$]");
        assertTransformation(adapter, "A", "$$argstest(\\\\$):", "\\\\$");
        assertTransformation(adapter, true, "$$argstest($):", true);
        assertTransformation(adapter, true, "$$argstest('$'):", true);
        assertTransformation(adapter, BigDecimal.valueOf(123.4), "$$argstest($):", BigDecimal.valueOf(123.4));
        assertTransformation(adapter, List.of(1, 2), "$$argstest($):", adapter.parse("[1,2]"));
        assertTransformation(adapter, null, "$$argstest( ):", " ");
        assertTransformation(adapter, null, "$$argstest(' '):", " ");
        assertTransformation(adapter, null, "$$argstest(  '  a' ):", "  a");
        assertTransformation(adapter, null, "$$argstest('a',b):", "[a],[b]");
        assertTransformation(adapter, null, "$$argstest('a','b'):", "[a],[b]");
        assertTransformation(adapter, null, "$$argstest('\\'','\\''):", "['],[']");
        assertTransformation(adapter, null, "$$argstest(  '\\'',  '\\''):", "['],[']");
        assertTransformation(adapter, null, "$$argstest(a, b):", "[a],[ b]");
        assertTransformation(adapter, null, "$$argstest( a):", " a");
        assertTransformation(adapter, null, "$$argstest( a ):", " a ");
        assertTransformation(adapter, null, "$$argstest( a, b):", "[ a],[ b]");
        assertTransformation(adapter, null, "$$argstest( a , b):", "[ a ],[ b]");
        assertTransformation(adapter, null, "$$argstest( a , b  , 'c  ' ):", "[ a ],[ b  ],[c  ]");
        assertTransformation(adapter, null, "$$argstest(a,' b'):", "[a],[ b]");
        assertTransformation(adapter, null, "$$argstest('\\n\\r\\t\\u0f0f'):", "\n\r\t\u0f0f");
        // not detected
        assertTransformation(adapter, null, "$$argstest(\n\r\t\u0f0f)", "$$argstest(\n\r\t\u0f0f)");
    }

    private static class TransformerFunctionValTest extends TransformerFunction {
        @Override
        public CompletionStage<Object> apply(FunctionContext context) {
            var value = context.getUnwrapped(null);
            if (value == null)
                return "NULL";
            return value;
        }
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void inlineValueParsingTest(JsonAdapter<?,?,?> adapter) {
        TransformerFunctions.registerFunctions(Map.entry("valtest", new TransformerFunctionValTest()));

        assertTransformation(adapter, null, "$$valtest", "NULL");
        assertTransformation(adapter, null, "$$valtest:", adapter.parse("\"\""));
        assertTransformation(adapter, null, "$$valtest:A", adapter.parse("\"A\""));
        assertTransformation(adapter, "IN", "$$valtest:$", adapter.parse("\"IN\""));
        assertTransformation(adapter, "IN", "$$valtest:\\$", "$");
        // regex replacements
        assertTransformation(adapter, "IN", "$$valtest:$1", "$1");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void internalMacrosTest(JsonAdapter<?,?,?> adapter) {
        var result = transform(adapter, null, adapter.parse("""
                ["#null"]"""), null);
        var listWithNull = new LinkedList<String>();
        listWithNull.add(null);
        assertEquals(adapter, adapter.wrap(listWithNull), result);

        var result2 = transform(adapter, null, adapter.parse("""
                ["#now"]"""), null);
        Assertions.assertTrue(adapter.isJsonArray(result2));
        var arg2_0 = adapter.unwrap(adapter.get(result2, 0), false);
        Assertions.assertDoesNotThrow(() -> DateTimeFormatter.ISO_INSTANT.parse(arg2_0.toString()));

        var result3 = transform(adapter, null, adapter.parse("""
                ["#uuid"]"""), null);
        Assertions.assertTrue(adapter.isJsonArray(result3));
        var arg3_0 = adapter.unwrap(adapter.get(result3, 0), false);
        Assertions.assertDoesNotThrow(() -> UUID.fromString(arg3_0.toString()));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void additionalContextTest(JsonAdapter<?,?,?> adapter) {
        assertTransformation(adapter, null, "$example", adapter.wrap(3), Map.of("$example", 3));
        assertTransformation(adapter, null, "$example[2]", adapter.wrap(4), Map.of("$example", List.of(0,2,4,6)));
        assertTransformation(adapter, null, "$example.key", adapter.wrap("KEY"), Map.of("$example", Map.of("key", "KEY")));
        assertTransformation(adapter, null, "$example", adapter.wrap("$example"));
        assertTransformation(adapter, null, "#call_id", adapter.wrap("CALL"), Map.of("#call_id", "CALL"));
        assertTransformation(adapter, null, "#ctx[2]", adapter.wrap(4), Map.of("#ctx", List.of(0,2,4,6)));
        assertTransformation(adapter, null, "#ctx.key", adapter.wrap("KEY"), Map.of("#ctx", Map.of("key", "KEY")));
    }
}
