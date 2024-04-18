package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionXmlParseTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation("""
               <root>
               </root>""", "$$xmlparse:$", fromJson("""
                 {
                   "root": ""
                 }"""));

        assertTransformation("""
               <root>
                  <hello to="world">
                    <hi />
                    <hi />
                  </hello>
                </root>""", "$$xmlparse:$", fromJson("""
                 {
                   "root": {
                     "hello": {
                       "hi": ["", ""],
                       "to": "world"
                     }
                   }
                 }"""));
    }

    @Test
    void object() {
        assertTransformation("""
               <root>
               </root>""", fromJson("""
{
  "$$xmlparse": "$"
}"""), fromJson("""
                 {
                   "root": ""
                 }"""));

        assertTransformation("""
               <root>
                  <hello to="world">
                    <hi />
                    <hi />
                  </hello>
                </root>""", fromJson("""
{
  "$$xmlparse": "$"
}"""), fromJson("""
                 {
                   "root": {
                     "hello": {
                       "hi": ["", ""],
                       "to": "world"
                     }
                   }
                 }"""));
    }

    @Test
    void testObject_forceList() {
        assertTransformation("""
               <root>
                  <hello to="world">
                    <hi><test>X</test></hi>
                  </hello>
                </root>""", fromJson("""
{
  "$$xmlparse": "$",
  "force_list": ["hi"]
}"""), fromJson("""
                 {
                   "root": {
                     "hello": {
                       "hi": [{ "test": "X" }],
                       "to": "world"
                     }
                   }
                 }"""));
        // without force_list
        assertTransformation("""
               <root>
                  <hello to="world">
                    <hi><test>X</test></hi>
                  </hello>
                </root>""", fromJson("""
{
  "$$xmlparse": "$"
}"""), fromJson("""
                 {
                   "root": {
                     "hello": {
                       "hi": { "test": "X" },
                       "to": "world"
                     }
                   }
                 }"""));


        assertTransformation("""
               <root>
                  <hello to="world">
                    <hi />
                    <hi />
                  </hello>
                </root>""", fromJson("""
{
  "$$xmlparse": "$",
  "force_list": ["hi"]
}"""), fromJson("""
                 {
                   "root": {
                     "hello": {
                       "hi": [],
                       "to": "world"
                     }
                   }
                 }"""));
    }

    @Test
    void testObject_keepStrings() {
        // without
        assertTransformation("""
               <root>
                  <hello to="2">
                    <hi>true</hi>
                  </hello>
                </root>""", fromJson("""
{
  "$$xmlparse": "$"
}"""), fromJson("""
                 {
                   "root": {
                     "hello": {
                       "hi": true,
                       "to": 2
                     }
                   }
                 }"""));
        // with
        assertTransformation("""
               <root>
                  <hello to="2">
                    <hi>true</hi>
                  </hello>
                </root>""", fromJson("""
{
  "$$xmlparse": "$",
  "keep_strings": true
}"""), fromJson("""
                 {
                   "root": {
                     "hello": {
                       "hi": "true",
                       "to": "2"
                     }
                   }
                 }"""));
    }
}
