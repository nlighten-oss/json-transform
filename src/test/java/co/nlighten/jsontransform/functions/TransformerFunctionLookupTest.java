package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionLookupTest extends BaseTest {

    @Test
    void mergeWithBy(){
        assertTransformation(fromJson("""
{
    "a": [
        { "id": 2, "a": 1 },
        { "id": 5, "a": 2 }
    ],
    "b":[
        { "id": 2, "a": "x" },
        { "id": 5, "e": true }
    ]
}"""),
                             fromJson("""
{
    "$$lookup": "$.a",
    "using": [
        {
            "with": "$.b",
            "as": "match",
            "on": {
                "$$is": "##current.id",
                "eq": "##match.id"
            }
        }
    ]
}
"""),
                             fromJson("""
[
    { "id": 2, "a": "x" },
    { "id": 5, "a": 2, "e":true }
]"""));

        assertTransformation(fromJson("""
{
    "a": [
        { "id": 2, "a": 1 },
        { "id": 5, "a": 2 }
    ],
    "b":[
        { "key": 2, "a": "x" },
        { "key": 5, "e": true }
    ]
}"""),
                             fromJson("""
{
    "$$lookup": "$.a",
    "using": [
        {
            "with": "$.b",
            "as": "match",
            "on": {
                "$$is": "##current.id",
                "eq": "##match.key"
            }
        }
    ]
}
"""),
                             fromJson("""
[
    { "id": 2, "a": "x", "key": 2 },
    { "id": 5, "a": 2, "e":true, "key": 5 }
]"""));

    }

    @Test
    public void mergeWithTo(){

        // don't override a, just copy e
        assertTransformation(fromJson("""
{
    "a": [
        { "id": 2, "a": 1 },
        { "id": 5, "a": 2 }
    ],
    "b":[
        { "id": 2, "a": "x" },
        { "id": 5, "e": true }
    ]
}"""),
                             fromJson("""
{
    "$$lookup": "$.a",
    "using": [
        {
            "with": "$.b",
            "as": "match",
            "on": {
                "$$is": "##current.id",
                "eq": "##match.id"
            }
        }
    ],
    "to": {
        "*":"##current",
        "e":"##match.e"
    }
}
"""),
                             fromJson("""
[
    { "id": 2, "a": 1 },
    { "id": 5, "a": 2, "e":true }
]"""));


        assertTransformation(fromJson("""
{
    "a1":[
        {"id":"aaa", "val": "a"},
        {"id":"bbb", "val": "b"}
    ],
    "a2": [
        {"name":"aaa", "val":"A"},
        {"name":"bbb", "val":"B" }
    ]
}
"""),fromJson("""
 {
    "$$lookup": "$.a1",
    "using": [
        {
            "with": "$.a2",
            "as": "a2",
            "on": {
                "$$is": "##current.id",
                "eq": "##a2.name"
            }
        }
    ],
    "to": [ "##current.val", "##a2.val"]
 }"""),fromJson("""
[
   ["a","A"],
   ["b","B"]
]"""));
    }
}
