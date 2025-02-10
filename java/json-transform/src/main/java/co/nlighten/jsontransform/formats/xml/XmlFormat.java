package co.nlighten.jsontransform.formats.xml;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.formats.FormatDeserializer;
import co.nlighten.jsontransform.formats.FormatSerializer;
import org.json.XMLParserConfiguration;
import org.json.XMLXsiTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class XmlFormat implements FormatSerializer, FormatDeserializer {
    static final Logger log = LoggerFactory.getLogger(XmlFormat.class);

    static private final boolean DEFAULT_KEEP_STRINGS = false;

    /** rename "content" to "$content" (every non-tag values between any opening and closing tags) */
    static private final String DEFAULT_CDATA_TAG_NAME = "$content";
    static private final boolean DEFAULT_CONVERT_NIL_TO_NULL = false;
    static private final Map<String, XMLXsiTypeConverter<?>> DEFAULT_XSI_TYPE_MAP = Collections.emptyMap();
    static private final Set<String> DEFAULT_FORCE_LIST = Collections.emptySet();

    private final JsonOrg jsonOrg;
    private final javax.xml.transform.Transformer xslt; // function of 'xslt' field
    private final XMLParserConfiguration config;
    private final JsonAdapter<?, ?, ?> adapter;

    public XmlFormat(JsonAdapter<?, ?, ?> adapter,
                     String xslt,
                     final Boolean keepStrings,
                     final String cDataTagName,
                     final Boolean convertNilAttributeToNull,
                     final Map<String, XMLXsiTypeConverter<?>> xsiTypeMap,
                     final Set<String> forceList) {
        this.adapter = adapter;
        this.xslt = createXSLTTransformer(xslt);
        this.config = new XMLParserConfiguration()
                .withKeepStrings(keepStrings != null ? keepStrings : DEFAULT_KEEP_STRINGS)
                .withcDataTagName(cDataTagName != null ? cDataTagName : DEFAULT_CDATA_TAG_NAME)
                .withConvertNilAttributeToNull(convertNilAttributeToNull != null ? convertNilAttributeToNull : DEFAULT_CONVERT_NIL_TO_NULL)
                .withXsiTypeMap(xsiTypeMap != null ? xsiTypeMap : DEFAULT_XSI_TYPE_MAP)
                .withForceList(forceList != null ? forceList : DEFAULT_FORCE_LIST);
        this.jsonOrg = new JsonOrg(adapter);
    }
    public XmlFormat(JsonAdapter<?, ?, ?> adapter, String xslt) {
        this(adapter, xslt, null, null, null, null, null);
    }

    public XmlFormat(JsonAdapter<?, ?, ?> adapter) {
        this(adapter, null, null, null, null, null, null);
    }

    public static javax.xml.transform.Transformer createXSLTTransformer(String xslt) {
        if (xslt == null) return null;
        javax.xml.transform.Transformer transformer = null;
        try {
            // prepare xslt source
            var xsltReader = new StringReader(xslt);
            var xsltSource = new StreamSource(xsltReader);
            transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
        } catch (Throwable te) {
            log.error("Failed parsing XSLT", te);
        }
        return transformer;
    }

    public String obj2xml(Object input, String rootName) {
        var json = adapter.toString(input);
        // pass to org.json
        var jo = new org.json.JSONObject(json);
        var xml = org.json.XML.toString(jo);
        return rootName != null && !rootName.isBlank() ? "<" + rootName + ">" + xml + "</" + rootName + ">"
               : xml;
    }

    public static String indentXml(String input) throws TransformerException {
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        return xmlTransform(input, transformer);
    }

    public static String xmlTransform(String input, javax.xml.transform.Transformer transformer) {
        try {
            var xmlReader = new StringReader(input);
            var xmlSource = new StreamSource(xmlReader);

            // prepare output stream
            var outWriter = new StringWriter();
            var result = new StreamResult(outWriter);

            // transform
            transformer.transform(xmlSource, result);

            // return result
            return outWriter.getBuffer().toString();
        }
        catch (Throwable te) {
            log.warn("Failed formatting to XML using provided input and XSLT", te);
            throw new RuntimeException(te);
        }
    }

    @Override
    public String serialize(Object payload) {
        if (xslt == null) {
            log.warn("There is no (or invalid) XSLT for this formatter");
            return null;
        }
        var inputXml = obj2xml(payload, "root");
        return xmlTransform(inputXml, xslt);
    }

    @Override
    public Object deserialize(String input) {
        if (input == null) return null;
        var jsonOrgObject = org.json.XML.toJSONObject(input, config);
        return jsonOrg.toJsonObject(jsonOrgObject);
    }
}
