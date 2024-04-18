package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/*
 * For tests
 * @see TransformerFunctionUrlDecodeTest
 */
@Aliases("urldecode")
@Documentation("URL decodes as string")
@InputType(ArgType.String)
@ArgumentType(value = "charset", type = ArgType.Enum, position = 0, defaultEnum = "UTF-8",
              description = "Character set to use when decoding to string")
@OutputType(ArgType.String)
public class TransformerFunctionUrlDecode<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger log = LoggerFactory.getLogger(TransformerFunctionUrlDecode.class);

    public TransformerFunctionUrlDecode(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        try {
            var charset = context.getEnum("charset");
            return URLDecoder.decode(str, charset);
        }
        catch (UnsupportedEncodingException e) {
            log.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
