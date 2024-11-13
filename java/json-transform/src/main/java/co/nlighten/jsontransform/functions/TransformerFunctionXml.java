package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.xml.XmlFormat;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * For tests
 * @see TransformerFunctionXmlTest
 */
@ArgumentType(value = "root", type = ArgType.String, position = 0, defaultIsNull = true)
@ArgumentType(value = "xslt", type = ArgType.String, position = 1, defaultIsNull = true)
@ArgumentType(value = "indent", type = ArgType.Boolean, position = 2, defaultBoolean = false)
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
