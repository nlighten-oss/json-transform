package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionXmlTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation(fromJson("""
              {
                "root": ""
              }"""), "$$xml:$", """
        <root/>""");

        assertTransformation(fromJson("""
              {
                "root": "",
                "root_b": ""
              }"""), "$$xml:$", """
        <root/><root_b/>""");


        assertTransformation(fromJson("""
              {
                "root": "",
                "root_b": ""
              }"""), "$$xml(container):$", """
        <container><root/><root_b/></container>""");

        // indent
        assertTransformation(fromJson("""
              {
                "root": "",
                "root_b": ""
              }"""), "$$xml(container,,true):$", """
<?xml version="1.0" encoding="UTF-8"?><container>
  <root/>
  <root_b/>
</container>
""");
    }

    @Test
    void object() {
        assertTransformation(fromJson("""
                 {
                   "root": ""
                 }"""), fromJson("""
{
  "$$xml": "$",
  "root": "container"
}"""), """
<container><root/></container>""");

        assertTransformation(null, fromJson("""
{
  "$$xml": {
                  "hello": {
                    "hi": ["", ""],
                    "to": "world"
                  }
                }
}"""), "<hello><hi/><hi/><to>world</to></hello>");

        assertTransformation("""
     <?xml version="1.0" encoding="UTF-8"?>
         <xsl:stylesheet version="1.0"
         xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
         
         <xsl:template match="/container">
         <my_record>
           <first><xsl:value-of select="a" /></first>
           <second><xsl:value-of select="b" /></second>
         </my_record>
         </xsl:template>
               
     </xsl:stylesheet>""", fromJson("""
                 {
                   "$$xml": {
                       "a": "AAA",
                       "b": "BBB"
                   },
                   "root": "container",
                   "xslt": "$"
                 }"""),
                             """
                             <?xml version="1.0" encoding="UTF-8"?><my_record><first>AAA</first><second>BBB</second></my_record>""");
    }
}
