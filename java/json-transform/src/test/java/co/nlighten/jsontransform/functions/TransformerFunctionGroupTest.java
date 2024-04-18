package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionGroupTest extends BaseTest {

    @Test
    void inline() {
        assertTransformation(fromJson("""
              [
                { "o": 1, "p": 11, "w":"aaa"},
                { "o": 1, "p": 13, "w":"bbb"},
                { "o": 1, "p": 11, "w":"ccc"},
                { "o": 2, "p": 11, "w":"ddd"},
                { "o": 2, "p": 13, "w":"eee"},
                { "o": 3, "p": 12, "w":"fff"},
                { "o": 1, "p": 9, "w":"zzz"},
                { "no_o": false, "p": 9, "w":"zzz"}
              ]"""),
                             "$$group(##current.o):$", fromJson("""
             {
                "": [
                    { "no_o": false, "p": 9, "w":"zzz"}
                ],
                "1": [
                    {"o":1, "p":11, "w":"aaa"},
                    {"o":1, "p":13, "w":"bbb"},
                    {"o":1, "p":11, "w":"ccc"},
                    {"o":1, "p":9, "w":"zzz"}
                ],
                "2": [
                    {"o":2, "p":11, "w":"ddd"},
                    {"o":2, "p":13, "w":"eee"}
                ],
                "3": [
                    {"o":3, "p":12, "w":"fff"}
                ]
             }"""));
        assertTransformation(fromJson("""
             [["a",0, 1],["a",1, true],["a",2, "C"],["b",1, 6]]"""),
                             "$$group(##current[0]):$", fromJson("""
             {
                "a":[["a",0, 1],["a",1, true],["a",2, "C"]],
                "b":[["b",1, 6]]
             }"""));

        // invalid input will yield an empty object
        assertTransformation(null, "$$group", fromJson("{}"));
        assertTransformation(0.5, "$$group(##current[0]):$", fromJson("{}"));
        assertTransformation("test", "$$group(##current[0]):$", fromJson("{}"));
        assertTransformation(false, "$$group:$", fromJson("{}"));
    }

    @Test
    void object() {
        assertTransformation(fromJson("""
              [
                { "o": 1, "p": 11, "w":"aaa"},
                { "o": 1, "p": 13, "w":"bbb"},
                { "o": 1, "p": 11, "w":"ccc"},
                { "o": 2, "p": 11, "w":"ddd"},
                { "o": 2, "p": 13, "w":"eee"},
                { "o": 3, "p": 12, "w":"fff"},
                { "o": 1, "p": 9, "w":"zzz"},
                { "no_o": false, "p": 9, "w":"zzz"}
              ]"""),
            fromJson("""
              {
                 "$$group": "$",
                 "by": "##current.o",
                 "then": [
                   {
                     "by": {
                       "$$join": [
                         "p_",
                         "##current.p"
                       ]
                     },
                     "order": "DESC"
                   }
                 ]
               }"""),
             fromJson("""
                {
                    "": {
                      "p_9": [
                        { "no_o": false, "p": 9, "w":"zzz"}
                      ]
                    },
                    "1": {
                      "p_9": [
                        { "o": 1, "p": 9, "w": "zzz" }
                      ],
                      "p_13": [
                        { "o": 1, "p": 13, "w": "bbb" }
                      ],
                      "p_11": [
                        { "o": 1, "p": 11, "w": "aaa" },
                        { "o": 1, "p": 11, "w": "ccc" }
                      ]
                    },
                    "2": {
                      "p_13": [
                        { "o": 2, "p": 13, "w": "eee" }
                      ],
                      "p_11": [
                        { "o": 2, "p": 11, "w": "ddd" }
                      ]
                    },
                    "3": {
                      "p_12": [
                        { "o": 3, "p": 12, "w": "fff" }
                      ]
                    }
                  }"""));

        assertTransformation(null, fromJson("""
            { "$$group": "$"}"""), fromJson("{}"));
        assertTransformation(0.5, fromJson("""
            { "$$group": "$"}"""), fromJson("{}"));
        assertTransformation(false, fromJson("""
            { "$$group": "$"}"""), fromJson("{}"));
    }

    @Test
    void objectLazyBy() {
        assertTransformation(fromJson("""
              [
                { "o": 1, "p": 11, "w":"aaa"},
                { "o": 1, "p": 13, "w":"bbb"},
                { "o": 1, "p": 11, "w":"ccc"},
                { "o": 2, "p": 11, "w":"ddd"},
                { "o": 2, "p": 13, "w":"eee"},
                { "o": 3, "p": 12, "w":"fff"},
                { "o": 1, "p": 9, "w":"zzz"},
                { "no_o": false, "p": 9, "w":"zzz"}
              ]"""),
                             fromJson("""
              {
                 "$$group": "$",
                 "by": { "$$join": ["##current.o", "##current.p"] }
               }"""),
                             fromJson("""
                {
                    "111": [
                        { "o": 1, "p": 11, "w":"aaa"},
                        { "o": 1, "p": 11, "w":"ccc"}
                    ],
                    "113": [
                        { "o": 1, "p": 13, "w":"bbb"}
                    ],
                    "19": [
                        { "o": 1, "p": 9, "w":"zzz"}
                    ],
                    "211": [
                        { "o": 2, "p": 11, "w":"ddd"}
                    ],
                    "213": [
                        { "o": 2, "p": 13, "w":"eee"}
                    ],
                    "312": [
                        { "o": 3, "p": 12, "w":"fff"}
                    ],
                    "9": [
                        { "no_o": false, "p": 9, "w":"zzz"}
                    ]
                  }"""));
    }
}
