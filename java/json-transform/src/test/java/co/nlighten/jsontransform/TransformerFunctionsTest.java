package co.nlighten.jsontransform;

import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.ArgumentType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionDescription;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionsTest extends BaseTest {
    @Test
    void testMultipleInlineFunctions() {
        var date = Instant.now();
        var now = date.toString();
        var text = "text";
        assertTransformation(now, "$$substring(0,10):$$date(iso):$", date.toString().substring(0,10));
        assertTransformation(text, "$$substring(1,3):$$wrap(>):$", "te");
        assertTransformation(text, "$$wrap(>):$$substring(1,3):$", ">ex");
        assertTransformation(text, "$$join(s):$$split(x):$", "test");
    }

    @Test
    void testMultipleObjectFunctions() {
        assertTransformation(null, fromJson("""
{
  "$$distinct": { "$$flat": [ "$$split:abcd", "$$split:def" ] }
}
"""), fromJson("""
[ "a", "b", "c", "d", "e", "f"]"""));
    }

    @Test
    void inlineInnerFailure() {
        // even though $$partition fails, $$string picks up the null and transforms it
        assertTransformation(fromJson("[1]"), "$$string(true):$$partition(0):$", "null");
    }

    @Test
    void testFunctionContextNesting() {
        assertTransformation(null, fromJson("""
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
"""), fromJson("""
[["A", "A"], ["B", "B"], ["FALSE", "C"]]"""));
    }

    @Test
    void testJsonElementStreamsImmediateEvaluation() {
        var callCount = new AtomicInteger();
        TransformerFunctions.registerFunctions(Map.entry("c", new TransformerFunction() {
            @Override
            public Object apply(FunctionContext context) {
                callCount.incrementAndGet();
                return context.get(null);
            }
        }));

        callCount.set(0);
        assertTransformation(null, fromJson("""
{
  "$$find": {
      "$$map": ["$$c:a", "$$c:b", "$$c:c", "$$c:d", "$$c:e"],
      "to": "$$upper:##current"
  },
  "by": "$$is(=,B):##current"
}
"""), "B");
        // assert that "c" was called twice (check A, check B, break)
        Assertions.assertEquals(2, callCount.get());

        callCount.set(0);
        assertTransformation(null, fromJson("""
{
  "$$is": "b",
  "in": ["$$c:a", "$$c:b", "$$c:c", "$$c:d"]
}
"""), true);
        // assert that "c" was called twice (check a, check b, break)
        Assertions.assertEquals(2, callCount.get());

        callCount.set(0);
        assertTransformation(false, fromJson("""
{
  "$$and": ["$$c:$", "$$c:a", "$$c:b"]
}
"""), false);
        // assert that "c" was called once (check false, break)
        Assertions.assertEquals(1, callCount.get());

        callCount.set(0);
        assertTransformation(false, fromJson("""
{
  "$$or": ["$$c:true", "$$c:$"]
}
"""), true);
        // assert that "c" was called once (check true, break)
        Assertions.assertEquals(1, callCount.get());

        callCount.set(0);
        assertTransformation(false, fromJson("""
{
  "$$first": {
    "$$map": [1, 2, 3],
    "to": "$$c:##current"
  }
}
"""), fromJson("1"));
        // assert that "c" was called once (mapped first, break)
        Assertions.assertEquals(1, callCount.get());

        callCount.set(0);
        assertTransformation(null, fromJson("""
{
  "$$at": ["$$c:a", "$$c:b", "$$c:c", "$$c:d"],
  "index": 2
}
"""), "c");
        // assert that "c" was called once (skipped 2, break)
        Assertions.assertEquals(1, callCount.get());


        callCount.set(0);
        assertTransformation(null, fromJson("""
{
  "$$slice": ["$$c:a", "$$c:b", "$$c:c", "$$c:d"],
  "begin": 1,
  "end": 3
}
"""), fromJson("[\"b\",\"c\"]"));
        // assert that "c" was called twice (skipped 1, eval 2, break)
        Assertions.assertEquals(2, callCount.get());
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
        public Object apply(FunctionContext context) {
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
            if (lst.size() == 1) {
                return adapter.isNull(lst.get(0)) ? "[NULL]" : lst.get(0);
            }
            return String.join(",", lst.stream().map(arg ->
                 "[" + (adapter.isNull(arg) ? "NULL" : adapter.getAsString(arg)) + "]"
            ).toList());
        }
    }

    @Test
    void inlineArgsParsingTest() {
        TransformerFunctions.registerFunctions(Map.entry("argstest", new TransformerFunctionArgsTest()));
        assertTransformation(null, "$$argstest(,):", "[],[]");

        assertTransformation(null, "$$argstest", "N/A");
        assertTransformation(null, "$$argstest:", "N/A");
        assertTransformation(null, "$$argstest()", "N/A");
        assertTransformation(null, "$$argstest():", "N/A");
        assertTransformation(null, "$$argstest(,):", "[],[]");
        assertTransformation(null, "$$argstest(#null):", "[NULL]");
        assertTransformation(null, "$$argstest(null,#null):", "[null],[NULL]");
        assertTransformation(null, "$$argstest(a):", "a");
        assertTransformation("A", "$$argstest($):", "A");
        assertTransformation("A", "$$argstest(\\$):", "$");
        assertTransformation("A", "$$argstest(\\$,\\$):", "[$],[$]");
        assertTransformation("A", "$$argstest('\\\\$'):", "$");
        assertTransformation("A", "$$argstest('\\\\$','\\\\$'):", "[$],[$]");
        assertTransformation("A", "$$argstest(\\\\$):", "\\\\$");
        assertTransformation(true, "$$argstest($):", true);
        assertTransformation(true, "$$argstest('$'):", true);
        assertTransformation(BigDecimal.valueOf(123.4), "$$argstest($):", BigDecimal.valueOf(123.4));
        assertTransformation(List.of(1, 2), "$$argstest($):", fromJson("[1,2]"));
        assertTransformation(null, "$$argstest( ):", " ");
        assertTransformation(null, "$$argstest(' '):", " ");
        assertTransformation(null, "$$argstest(  '  a' ):", "  a");
        assertTransformation(null, "$$argstest('a',b):", "[a],[b]");
        assertTransformation(null, "$$argstest('a','b'):", "[a],[b]");
        assertTransformation(null, "$$argstest('\\'','\\''):", "['],[']");
        assertTransformation(null, "$$argstest(  '\\'',  '\\''):", "['],[']");
        assertTransformation(null, "$$argstest(a, b):", "[a],[ b]");
        assertTransformation(null, "$$argstest( a):", " a");
        assertTransformation(null, "$$argstest( a ):", " a ");
        assertTransformation(null, "$$argstest( a, b):", "[ a],[ b]");
        assertTransformation(null, "$$argstest( a , b):", "[ a ],[ b]");
        assertTransformation(null, "$$argstest( a , b  , 'c  ' ):", "[ a ],[ b  ],[c  ]");
        assertTransformation(null, "$$argstest(a,' b'):", "[a],[ b]");
        assertTransformation(null, "$$argstest('\\n\\r\\t\\u0f0f'):", "\n\r\t\u0f0f");
        // not detected
        assertTransformation(null, "$$argstest(\n\r\t\u0f0f)", "$$argstest(\n\r\t\u0f0f)");
    }

    private static class TransformerFunctionValTest extends TransformerFunction {
        @Override
        public Object apply(FunctionContext context) {
            var value = context.getUnwrapped(null);
            if (value == null)
                return "NULL";
            return value;
        }
    }

    @Test
    void inlineValueParsingTest() {
        TransformerFunctions.registerFunctions(Map.entry("valtest", new TransformerFunctionValTest()));

        assertTransformation(null, "$$valtest", "NULL");
        assertTransformation(null, "$$valtest:", fromJson("\"\""));
        assertTransformation(null, "$$valtest:A", fromJson("\"A\""));
        assertTransformation("IN", "$$valtest:$", fromJson("\"IN\""));
        assertTransformation("IN", "$$valtest:\\$", "$");
        // regex replacements
        assertTransformation("IN", "$$valtest:$1", "$1");
    }

    @Test
    void internalMacrosTest() {
        var result = transform(null, fromJson("""
                ["#null"]"""), null);
        var listWithNull = new LinkedList<String>();
        listWithNull.add(null);
        Assertions.assertEquals(adapter.wrap(listWithNull), result);

        var result2 = transform(null, fromJson("""
                ["#now"]"""), null);
        Assertions.assertTrue(adapter.isJsonArray(result2));
        var arg2_0 = adapter.unwrap(adapter.get(result2, 0), false);
        Assertions.assertDoesNotThrow(() -> DateTimeFormatter.ISO_INSTANT.parse(arg2_0.toString()));

        var result3 = transform(null, fromJson("""
                ["#uuid"]"""), null);
        Assertions.assertTrue(adapter.isJsonArray(result3));
        var arg3_0 = adapter.unwrap(adapter.get(result3, 0), false);
        Assertions.assertDoesNotThrow(() -> UUID.fromString(arg3_0.toString()));
    }

    @Test
    void additionalContextTest() {
        assertTransformation(null, "$example", adapter.wrap(3), Map.of("$example", 3));
        assertTransformation(null, "$example[2]", adapter.wrap(4), Map.of("$example", List.of(0,2,4,6)));
        assertTransformation(null, "$example.key", adapter.wrap("KEY"), Map.of("$example", Map.of("key", "KEY")));
        assertTransformation(null, "$example", adapter.wrap("$example"));
        assertTransformation(null, "#call_id", adapter.wrap("CALL"), Map.of("#call_id", "CALL"));
        assertTransformation(null, "#ctx[2]", adapter.wrap(4), Map.of("#ctx", List.of(0,2,4,6)));
        assertTransformation(null, "#ctx.key", adapter.wrap("KEY"), Map.of("#ctx", Map.of("key", "KEY")));
    }
}
