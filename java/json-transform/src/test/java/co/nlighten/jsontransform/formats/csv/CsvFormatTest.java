package co.nlighten.jsontransform.formats.csv;

import co.nlighten.jsontransform.MultiAdapterBaseTest;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

public class CsvFormatTest extends MultiAdapterBaseTest {

    private void assertOutputFrom(JsonAdapter<?,?,?> adapter, String input, String output) {
        assertOutputFrom(adapter, input, output, null, null);
    }
    private void assertOutputFrom(JsonAdapter<?,?,?> adapter, String input, String output, List<String> names) {
        assertOutputFrom(adapter, input, output, names, null);
    }
    private void assertOutputFrom(JsonAdapter<?,?,?> adapter, String input, String output, List<String> names, Boolean forceQuote) {
        assertEquals(adapter, output, new CsvFormat(adapter, names, null, forceQuote, null)
                .serialize(adapter.parse(input)));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testCsv(JsonAdapter<?,?,?> adapter) {
        assertOutputFrom(adapter, """
[{"a":"A","b":1},{"a":"C","b":2}]
""","""
a,b
A,1
C,2
""");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testCsv_withNames_reduced(JsonAdapter<?,?,?> adapter) {
        assertOutputFrom(adapter, """
[{"a":"A","b":1},{"a":"C","b":2}]
""","""
a
A
C
""", List.of("a"));
    }


    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testCsv_arrays(JsonAdapter<?,?,?> adapter) {
        assertOutputFrom(adapter, """
[[1,2],[3,4]]
""","""
1,2
3,4
""");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testCsv_arrays_withNames(JsonAdapter<?,?,?> adapter) {
        assertOutputFrom(adapter, """
[[1,2],[3,4]]
""","""
a,b
1,2
3,4
""",List.of("a","b"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testCsv_escape(JsonAdapter<?,?,?> adapter) {
        assertOutputFrom(adapter, """
[{"a":"Don't","b":1},{"a":"D\\"C\\"","b":"ok,"}]
""", """
a,b
Don't,1
"D""C""\","ok,"
""");

        assertOutputFrom(adapter, """
[{"a":"Don\\nt","b":1},{"a":"D\\"C\\"","b":"ok,"}]
""", """
a,b
"Don
t",1
"D""C""\","ok,"
""");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testCsv_forceQuote(JsonAdapter<?,?,?> adapter) {
        assertOutputFrom(adapter, """
[{"a":"A","b":1},{"a":"C","b":2}]
""","""
"a","b"
"A","1"
"C","2"
""", null, true);
        assertOutputFrom(adapter, """
[[1,2],[3,4]]
""","""
"a","b"
"1","2"
"3","4"
""",List.of("a","b"), true);
    }

    private void assertDeserializationOutputFrom(JsonAdapter<?,?,?> adapter, String input, String output) {
        assertDeserializationOutputFrom(adapter, input, output, null);
    }
    private void assertDeserializationOutputFrom(JsonAdapter<?,?,?> adapter, String input, String output, List<String> names) {
        assertEquals(adapter,
                adapter.parse(output),
                new CsvFormat(adapter, names, null, null, null).deserialize(input)
                               );
    }
    private void assertDeserializationOutputFrom(JsonAdapter<?,?,?> adapter, String input, String output, boolean noHeaders, List<String> names) {
        assertEquals(adapter,
                adapter.parse(output),
                new CsvFormat(adapter, names, noHeaders, null, null).deserialize(input)
                               );
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_default_noData(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "a,b", "[]");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_default_oneRow(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "a,b\nA,B", """
[{ "a": "A", "b": "B" }]""");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_default_ignoreSpaces(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "a,b\nA,   B", """
[{ "a": "A", "b": "B" }]""");
        assertDeserializationOutputFrom(adapter, "a,b\nA,\t\tB", """
[{ "a": "A", "b": "B" }]""");
        assertDeserializationOutputFrom(adapter, "a,b\nA,\"  B\"", """
[{ "a": "A", "b": "  B" }]""");
    }


    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_default_escapingComma(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "a\n\",\"","""
                                                             [{ "a": "," }]""");
    }
    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_default_escapingNewline(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, """
"aaa","b
bb","ccc"
zzz,yyy,xxx""", """
                [{"aaa":"zzz","b\\nbb":"yyy","ccc":"xxx"}]""");
    }
    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_default_extraFields(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, """
a,b
1,2,3""", """
                [{"a":"1","b":"2","$2":"3"}]""");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_default_escapingQuotes(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "a\n\"a\"\"b\"","""
                                                             [{ "a": "a\\"b" }]""");
        assertDeserializationOutputFrom(adapter, "a\n\"a\"\"\"","""
                                                             [{ "a": "a\\"" }]""");
        assertDeserializationOutputFrom(adapter, "a\n\"\"\"b\"","""
                                                             [{ "a": "\\"b" }]""");
        assertDeserializationOutputFrom(adapter, "a\n\"\"\"\"","""
                                                             [{ "a": "\\"" }]""");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_default_possibleIssuesAtEndOfRow(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "a,b\nA,B\nC","""
                                                             [{ "a": "A", "b": "B" },{ "a": "C" }]""");
        assertDeserializationOutputFrom(adapter, "a,b\nA,B\nC,","""
                                                             [{ "a": "A", "b": "B" },{ "a": "C", "b": "" }]""");
        assertDeserializationOutputFrom(adapter, "a,b\nA,B\nC,\n","""
                                                             [{ "a": "A", "b": "B" },{ "a": "C", "b": "" }]""");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_noHeaders_arrays(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "a,b\nA,B","""
                                                             [["a","b"],["A","B"]]""", true, null);
    }
    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_noHeaders_escapingComma(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "\",\",\",\"","""
                                                             [[",",","]]""", true, null);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_noHeaders_escapingQuotes(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, "\"'\",\"\"\"\"","""
                                                             [["'","\\""]]""", true, null);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_noHeaders__names(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, """
1,2
2,4
3,6""", """
[{"number":"1","twice":"2"},{"number":"2","twice":"4"},{"number":"3","twice":"6"}]""", true, List.of("number","twice"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseCSV_names(JsonAdapter<?,?,?> adapter) {
        assertDeserializationOutputFrom(adapter, """
a,b,c
1,2,3
4,5,6""", """
[{"a":"1","b":"2"},{"a":"4","b":"5"}]""", false, List.of("a","b"));
    }
}
