package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/*
 * For tests
 * @see TransformerFunctionUrlEncodeTest
 */
public class TransformerFunctionUrlEncode extends TransformerFunction {
    static final Logger log = LoggerFactory.getLogger(TransformerFunctionUrlEncode.class);

    public TransformerFunctionUrlEncode() {
        super(FunctionDescription.of(
                Map.of(
                        "charset", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("UTF-8")
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
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
