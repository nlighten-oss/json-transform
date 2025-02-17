package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.formats.xml.XmlFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class TransformerFunctionXmlParse extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionXmlParse.class);
    public TransformerFunctionXmlParse() {
        super(FunctionDescription.of(
            Map.of(
            "keep_strings", ArgumentType.of(ArgType.Boolean).position(0).defaultBoolean(false),
            "cdata_tag_name", ArgumentType.of(ArgType.String).position(1).defaultString("$content"),
            "convert_nil_to_null", ArgumentType.of(ArgType.Boolean).position(2).defaultBoolean(false),
            "force_list", ArgumentType.of(ArgType.ArrayOfString).position(3).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var xml = context.getString(null);
        if (xml == null)
            return null;
        try {
            var keepStrings = context.getBoolean("keep_strings");
            var cDataTagName = context.getString("cdata_tag_name");
            var convertNilAttributeToNull = context.getBoolean("convert_nil_to_null");
            var forceList = context.getJsonArray("force_list");
            var adapter = context.getAdapter();
            return new XmlFormat(adapter,
                                 null,
                                 keepStrings,
                                 cDataTagName,
                                 convertNilAttributeToNull,
                                 null,
                                 forceList == null
                                 ? null
                                 : adapter.stream(forceList)
                                         .map(context::getAsString)
                                         .collect(Collectors.toSet())
                                                       ).deserialize(xml);
        } catch (Exception e) {
            logger.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
