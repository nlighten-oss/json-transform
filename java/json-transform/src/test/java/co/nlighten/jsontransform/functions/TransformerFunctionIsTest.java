package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TransformerFunctionIsTest extends BaseTest {
    @Test
    void assertEq_NotEq() {
        assertTransformation("A", fromJson("""
                                                   { "$$is": "$", "eq": "A" }"""), true);
        assertTransformation("A", fromJson("""
                                                   { "$$is": "$", "eq": "B" }"""), false);
        assertTransformation(4, fromJson(""" 
                                                 { "$$is": "$", "eq": 4 }"""), true);
        assertTransformation(4.5, fromJson("""
                                                   { "$$is": "$", "eq": 4 }"""), false);
        assertTransformation(4.5, fromJson("""
                                                   { "$$is": "$", "neq": 4 }"""), true);
        assertTransformation(4.5, fromJson("""
                                                   { "$$is": "$", "eq": 4.5, "neq": 4 }"""), true);
    }

    @Test
    void assertGt_Gte_Lt_Lte() {
        assertTransformation("B", fromJson("""
                                                   { "$$is": "$", "gt": "A" }"""), true);
        assertTransformation("B", fromJson(""" 
                                                   { "$$is": "$", "gte": "B" }"""), true);
        assertTransformation(4, fromJson("""
                                                 { "$$is": "$", "gt": 3 }"""), true);
        assertTransformation(4, fromJson("""
                                                 { "$$is": "$", "gte": 4 }"""), true);
        assertTransformation(4, fromJson("""
                                                 { "$$is": "$", "lte": 4 }"""), true);
        assertTransformation(3, fromJson("""
                                                 { "$$is": "$", "lt": 4 }"""), true);
        assertTransformation(4, fromJson("""
                                                 { "$$is": "$", "lt": 4 }"""), false);
        assertTransformation(fromJson("[1,2,3]"), fromJson("""
                                                                   { "$$is": "$", "lt": [true,"a","b","c"] }"""), true);
        assertTransformation(fromJson("[1,2,3]"), fromJson("""
                                                                   { "$$is": "$", "gte": ["a","b","c"] }"""), true);
        assertTransformation(fromJson("""
                                              { "a": 1, "b": 2 }"""), fromJson("""
                                                                                                                    { "$$is": "$", "gte": { "key1": "a", "key2": "b" } }"""),
                             true);
    }

    @Test
    void assertIn_NotIn() {
        assertTransformation("A", fromJson("""
                                                   { "$$is": "$", "in": ["A", "B"] }"""), true);

        assertTransformation(fromJson("[\"A\",\"B\"]"), fromJson("""
                                                                         { "$$is": "A", "in": "$" }"""), true);
        assertTransformation(fromJson("[\"A\",\"B\"]"), fromJson(""" 
                                                                         { "$$is": "B", "in": ["$[0]","$[1]"] }"""),
                             true);
        assertTransformation(fromJson("[\"a\",\"B\"]"), fromJson("""
                                                                         { "$$is": "A", "in": "$" }"""), false);
        // other types
        assertTransformation(fromJson("[false, true]"), fromJson("""
                                                                         { "$$is": true, "in": "$" }"""), true);
        assertTransformation(null, fromJson("""
                                                    { "$$is": 30, "in": [10,20,30] }"""), true);
        assertTransformation(null, fromJson("""
                                                    { "$$is": 30, "nin": [10,20,30] }"""), false);
        assertTransformation(null, fromJson("""
                                                    { "$$is": 30, "in": "$" }"""), false);
        assertTransformation(null, fromJson("""
                                                    { "$$is": 30, "nin": "$" }"""), false);
        // even complex
        assertTransformation(null, fromJson("""
                                                    { "$$is": [{"a": 1}], "in": [[{"a": 4}], [{"a": 1}], [{"a": 3}]] }"""),
                             true);
        assertTransformation(null, fromJson("""
                                                    { "$$is": 30, "in": [10,20,30], "nin": [40,50,60] }"""), true);
        assertTransformation(null, fromJson("""
                                                    { "$$is": 30, "in": [40,50,60], "nin": [10,20,30] }"""), false);
    }

    @Test
    void andOrExample() {
        // check if number is between 1 < x < 3 or 4 <= x <= 6
        var between1And3 = """
                { "$$is": "$", "gt": 1, "lt": 3 }""";
        var between4And6 = """
                { "$$is": "$", "gte": 4, "lte": 6 }""";
        var definition = fromJson("""
                                          { "$$is": true, "in": [""" + between1And3 + "," + between4And6 + "] }");

        var goodValues = List.of(2, 4, 5, 6);
        var badValues = List.of(1, 3, 7);

        for (var value : goodValues) {
            assertTransformation(value, definition, true);
        }
        for (var value : badValues) {
            assertTransformation(value, definition, false);
        }
    }

    @Test
    void objectOpThat() {
        assertTransformation("A", fromJson("""
            { "$$is": "$", "op": "EQ", "that": "A" }"""), true);
        assertTransformation("A", fromJson("""
            { "$$is": "$", "op": "EQ", "that": "B" }"""), false);
        assertTransformation("A", fromJson("""
            { "$$is": "$", "op": "!=", "that": "B" }"""), true);
        assertTransformation(fromJson("5"), fromJson("""
            { "$$is": "$", "op": ">", "that": 2 }"""), true);
    }

    @Test
    void inlineOpThat() {
        assertTransformation("A", "$$is(EQ,A):$", true);
        assertTransformation("A", "$$is(=,B):$", false);
        assertTransformation("A", "$$is(!=,B):$", true);
        // string comparison vs number comparison
        assertTransformation("10", "$$is(>,2):$", false);
        assertTransformation(fromJson("10"), "$$is(>,2):$", true);
        // in / not in
        assertTransformation(fromJson("""
["a","b","A","B"]"""), "$$is(IN,$):A", true);
        assertTransformation(fromJson("""
["a","b","A","B"]"""), "$$is(IN,$):C", false);
        assertTransformation(fromJson("""
["a","b","A","B"]"""), "$$is(NIN,$):C", true);
        assertTransformation(null, "$$is(in,$):C", false);
        assertTransformation(null, "$$is(Nin,$):C", false);
    }


    @Test
    void inlineCompareToNull() {
        assertTransformation(null, "$$is(!=,#null):$", false);
        assertTransformation(null, "$$is(=,#null):$", true);
    }
}