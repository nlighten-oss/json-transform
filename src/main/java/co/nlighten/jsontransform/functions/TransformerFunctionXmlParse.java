package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.xml.XmlFormat;
import co.nlighten.jsontransform.functions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/*
 * For tests
 * @see TransformerFunctionXmlParseTest
 */
@Aliases("xmlparse")
@Documentation("Parses XML String to Object (powered by `org.json.XML`)")
@InputType(ArgType.String)
@ArgumentType(value = "keep_strings", type = ArgType.Boolean, position = 0, defaultBoolean = false,
              description = "Do not try to detect primitive types (e.g. numbers, boolean, etc)")
@ArgumentType(value = "cdata_tag_name", type = ArgType.String, position = 1, defaultString = "$content",
              description = "A key for the CDATA section")
@ArgumentType(value = "convert_nil_to_null", type = ArgType.Boolean, position = 2, defaultBoolean = false,
              description = "If values with attribute `xsi:nil=\"true\"` will be converted to `null`")
@ArgumentType(value = "force_list", type = ArgType.ArrayOfString, position = 3, defaultIsNull = true,
              description = "Tag names that will always be parsed as arrays")
@OutputType(ArgType.Object)
public class TransformerFunctionXmlParse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionXmlParse.class);
    public TransformerFunctionXmlParse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var xml = context.getString(null);
        if (xml == null)
            return null;
        try {
            var keepStrings = context.getBoolean("keep_strings");
            var cDataTagName = context.getString("cdata_tag_name");
            var convertNilAttributeToNull = context.getBoolean("convert_nil_to_null");
            var forceList = context.getJsonArray("force_list");
            return new XmlFormat<>(adapter,
                                 null,
                                 keepStrings,
                                 cDataTagName,
                                 convertNilAttributeToNull,
                                 null,
                                 forceList == null
                                 ? null
                                 : ARRAY.stream(forceList)
                                         .map(context::getAsString)
                                         .collect(Collectors.toSet())
                                                       ).deserialize(xml);
        } catch (Exception e) {
            logger.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
