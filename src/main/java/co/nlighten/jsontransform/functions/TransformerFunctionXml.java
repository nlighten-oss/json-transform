package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.xml.XmlFormat;
import co.nlighten.jsontransform.functions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * For tests
 * @see TransformerFunctionXmlTest
 */
@Aliases("xml")
@Documentation("Converts an object to XML string (a wrapper element can be added by specifying the field `root` with the element name)")
@InputType(ArgType.Object)
@ArgumentType(value = "root", type = ArgType.String, position = 0, defaultIsNull = true,
              description = "Name for a wrapper element (e.g. an array was passed and a container is needed)")
@ArgumentType(value = "xslt", type = ArgType.String, position = 1, defaultIsNull = true,
              description = "XSLT document to transform xml created from input")
@ArgumentType(value = "indent", type = ArgType.Boolean, position = 2,
              description = "Whether to output an indented xml")
@OutputType(ArgType.String)
public class TransformerFunctionXml<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionXml.class);
    private final XmlFormat<JE, JA, JO> xmlFormat;

    public TransformerFunctionXml(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
        this.xmlFormat = new XmlFormat<>(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var obj = context.getJsonElement(null);
        if (obj == null)
            return null;
        try {
            var rootName = context.getString("root");
            var xml = xmlFormat.obj2xml(obj, rootName);
            var xslt = context.getString("xslt");
            if (xslt != null && !xslt.isBlank()) {
                var transformer = XmlFormat.createXSLTTransformer(xslt);
                xml = XmlFormat.xmlTransform(xml, transformer);
            }
            if (context.getBoolean("indent")) {
                xml = XmlFormat.indentXml(xml);
            }
            return xml;
        } catch (Exception e) {
            logger.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
