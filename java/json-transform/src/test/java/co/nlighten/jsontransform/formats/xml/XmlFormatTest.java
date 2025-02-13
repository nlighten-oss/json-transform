package co.nlighten.jsontransform.formats.xml;

import co.nlighten.jsontransform.MultiAdapterBaseTest;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class XmlFormatTest extends MultiAdapterBaseTest {

    public static class XMLTest {
        public String title = "Hello World";
        public int[] numbers = new int[] { 1, 2};
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJSON2XML(JsonAdapter<?,?,?> adapter) {
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

        assertEquals(adapter, expect, result);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJSON2XMLUglify(JsonAdapter<?,?,?> adapter) {
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

        assertEquals(adapter, expect, result);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testParseXML(JsonAdapter<?,?,?> adapter) {
        var result = new XmlFormat(adapter).deserialize("""
                                                   <root>
                                                   </root>""");
        assertEquals(adapter, adapter.parse("""
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
        assertEquals(adapter, adapter.parse("""
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
