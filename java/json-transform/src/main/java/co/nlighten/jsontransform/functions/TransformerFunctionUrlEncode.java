package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
 * For tests
 * @see TransformerFunctionUrlEncodeTest
 */
@ArgumentType(value = "charset", type = ArgType.Enum, position = 0, defaultEnum = "UTF-8")
public class TransformerFunctionUrlEncode<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger log = LoggerFactory.getLogger(TransformerFunctionUrlEncode.class);

    public TransformerFunctionUrlEncode(JsonAdapter<JE, JA, JO> adapter) {
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
            return URLEncoder.encode(str, charset);
        } catch (UnsupportedEncodingException e) {
            log.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
