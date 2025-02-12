package co.nlighten.jsontransform.formats.xml;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XmlFormatTest extends BaseTest {

    public static class XMLTest {
        public String title = "Hello World";
        public int[] numbers = new int[] { 1, 2};
    }

    @Test
    void testJSON2XML() {
        var xbt = new XmlFormat(adapter, """
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/root">
<html>
  <body>
    <h2><xsl:value-of select="title" /></h2>
    <table>
      <tr>
        <th>Number</th>
      </tr>
      <xsl:for-each select="numbers">
      <tr>
        <td><xsl:value-of select="." /></td>
      </tr>
      </xsl:for-each>
    </table>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>""");
        var result = xbt.serialize(new XMLTest());

        var expect = """
<html>
    <body>
        <h2>Hello World</h2>
        <table>
            <tr>
                <th>Number</th>
            </tr>
            <tr>
                <td>1</td>
            </tr>
            <tr>
                <td>2</td>
            </tr>
        </table>
    </body>
</html>
""";

        assertEquals(expect, result);
    }

    @Test
    void testJSON2XMLUglify() {
        var xbt = new XmlFormat(adapter, """
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="utf-8" indent="no" omit-xml-declaration="yes" />
<xsl:template match="/root">
<html>
  <body>
    <h2><xsl:value-of select="title" /></h2>
    <table>
      <tr>
        <th>Number</th>
      </tr>
      <xsl:for-each select="numbers">
      <tr>
        <td><xsl:value-of select="." /></td>
      </tr>
      </xsl:for-each>
    </table>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>""");
        var result = xbt.serialize(new XMLTest());

        var expect = """
<html><body><h2>Hello World</h2><table><tr><th>Number</th></tr><tr><td>1</td></tr><tr><td>2</td></tr></table></body></html>""";

        assertEquals(expect, result);
    }

    @Test
    void testParseXML() {
        var result = new XmlFormat(adapter).deserialize("""
                                                   <root>
                                                   </root>""");
        assertEquals(adapter.parse("""
                                                             {
                                                               "root": ""
                                                             }"""), result);

        var result2 = new XmlFormat(adapter).deserialize("""
                                                    <root>
                                                      <hello to="world">
                                                        <hi />
                                                        <hi />
                                                      </hello>
                                                    </root>""");
        assertEquals(adapter.parse("""
                                                             {
                                                               "root": {
                                                                 "hello": {
                                                                   "hi": ["", ""],
                                                                   "to": "world"
                                                                 }
                                                               }
                                                             }"""), result2);
    }
}
