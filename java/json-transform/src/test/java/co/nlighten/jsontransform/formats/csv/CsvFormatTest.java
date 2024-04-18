package co.nlighten.jsontransform.formats.csv;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CsvFormatTest extends BaseTest {

    private void assertOutputFrom(String input, String output) {
        assertOutputFrom(input, output, null, null);
    }
    private void assertOutputFrom(String input, String output, List<String> names) {
        assertOutputFrom(input, output, names, null);
    }
    private void assertOutputFrom(String input, String output, List<String> names, Boolean forceQuote) {
        Assertions.assertEquals(output, new CsvFormat(adapter, names, null, forceQuote, null)
                .serialize(adapter.parse(input)));
    }

    @Test
    void testCsv() {
        assertOutputFrom("""
[{"a":"A","b":1},{"a":"C","b":2}]
""","""
a,b
A,1
C,2
""");
    }

    @Test
    void testCsv_withNames_reduced() {
        assertOutputFrom("""
[{"a":"A","b":1},{"a":"C","b":2}]
""","""
a
A
C
""", List.of("a"));
    }


    @Test
    void testCsv_arrays() {
        assertOutputFrom("""
[[1,2],[3,4]]
""","""
1,2
3,4
""");
    }

    @Test
    void testCsv_arrays_withNames() {
        assertOutputFrom("""
[[1,2],[3,4]]
""","""
a,b
1,2
3,4
""",List.of("a","b"));
    }

    @Test
    void testCsv_escape() {
        assertOutputFrom("""
[{"a":"Don't","b":1},{"a":"D\\"C\\"","b":"ok,"}]
""", """
a,b
Don't,1
"D""C""\","ok,"
""");

        assertOutputFrom("""
[{"a":"Don\\nt","b":1},{"a":"D\\"C\\"","b":"ok,"}]
""", """
a,b
"Don
t",1
"D""C""\","ok,"
""");
    }

    @Test
    void testCsv_forceQuote() {
        assertOutputFrom("""
[{"a":"A","b":1},{"a":"C","b":2}]
""","""
"a","b"
"A","1"
"C","2"
""", null, true);
        assertOutputFrom("""
[[1,2],[3,4]]
""","""
"a","b"
"1","2"
"3","4"
""",List.of("a","b"), true);
    }

    private void assertDeserializationOutputFrom(String input, String output) {
        assertDeserializationOutputFrom(input, output, null);
    }
    private void assertDeserializationOutputFrom(String input, String output, List<String> names) {
        Assertions.assertEquals(
                adapter.parse(output),
                new CsvFormat(adapter, names, null, null, null).deserialize(input)
                               );
    }
    private void assertDeserializationOutputFrom(String input, String output, boolean noHeaders, List<String> names) {
        Assertions.assertEquals(
                adapter.parse(output),
                new CsvFormat(adapter, names, noHeaders, null, null).deserialize(input)
                               );
    }

    @Test
    void testParseCSV_default_noData() {
        assertDeserializationOutputFrom("a,b", "[]");
    }

    @Test
    void testParseCSV_default_oneRow() {
        assertDeserializationOutputFrom("a,b\nA,B", """
[{ "a": "A", "b": "B" }]""");
    }

    @Test
    void testParseCSV_default_ignoreSpaces() {
        assertDeserializationOutputFrom("a,b\nA,   B", """
[{ "a": "A", "b": "B" }]""");
        assertDeserializationOutputFrom("a,b\nA,\t\tB", """
[{ "a": "A", "b": "B" }]""");
        assertDeserializationOutputFrom("a,b\nA,\"  B\"", """
[{ "a": "A", "b": "  B" }]""");
    }


    @Test
    void testParseCSV_default_escapingComma() {
        assertDeserializationOutputFrom("a\n\",\"","""
                                                             [{ "a": "," }]""");
    }
    @Test
    void testParseCSV_default_escapingNewline() {
        assertDeserializationOutputFrom("""
"aaa","b
bb","ccc"
zzz,yyy,xxx""", """
                [{"aaa":"zzz","b\\nbb":"yyy","ccc":"xxx"}]""");
    }
    @Test
    void testParseCSV_default_extraFields() {
        assertDeserializationOutputFrom("""
a,b
1,2,3""", """
                [{"a":"1","b":"2","$2":"3"}]""");
    }

    @Test
    void testParseCSV_default_escapingQuotes() {
        assertDeserializationOutputFrom("a\n\"a\"\"b\"","""
                                                             [{ "a": "a\\"b" }]""");
        assertDeserializationOutputFrom("a\n\"a\"\"\"","""
                                                             [{ "a": "a\\"" }]""");
        assertDeserializationOutputFrom("a\n\"\"\"b\"","""
                                                             [{ "a": "\\"b" }]""");
        assertDeserializationOutputFrom("a\n\"\"\"\"","""
                                                             [{ "a": "\\"" }]""");
    }

    @Test
    void testParseCSV_default_possibleIssuesAtEndOfRow() {
        assertDeserializationOutputFrom("a,b\nA,B\nC","""
                                                             [{ "a": "A", "b": "B" },{ "a": "C" }]""");
        assertDeserializationOutputFrom("a,b\nA,B\nC,","""
                                                             [{ "a": "A", "b": "B" },{ "a": "C", "b": "" }]""");
        assertDeserializationOutputFrom("a,b\nA,B\nC,\n","""
                                                             [{ "a": "A", "b": "B" },{ "a": "C", "b": "" }]""");
    }

    @Test
    void testParseCSV_noHeaders_arrays() {
        assertDeserializationOutputFrom("a,b\nA,B","""
                                                             [["a","b"],["A","B"]]""", true, null);
    }
    @Test
    void testParseCSV_noHeaders_escapingComma() {
        assertDeserializationOutputFrom("\",\",\",\"","""
                                                             [[",",","]]""", true, null);
    }

    @Test
    void testParseCSV_noHeaders_escapingQuotes() {
        assertDeserializationOutputFrom("\"'\",\"\"\"\"","""
                                                             [["'","\\""]]""", true, null);
    }

    @Test
    void testParseCSV_noHeaders__names() {
        assertDeserializationOutputFrom("""
1,2
2,4
3,6""", """
[{"number":"1","twice":"2"},{"number":"2","twice":"4"},{"number":"3","twice":"6"}]""", true, List.of("number","twice"));
    }

    @Test
    void testParseCSV_names() {
        assertDeserializationOutputFrom("""
a,b,c
1,2,3
4,5,6""", """
[{"a":"1","b":"2"},{"a":"4","b":"5"}]""", false, List.of("a","b"));
    }
}
