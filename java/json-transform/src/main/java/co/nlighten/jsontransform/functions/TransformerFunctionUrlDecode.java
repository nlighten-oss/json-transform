package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class TransformerFunctionUrlDecode extends TransformerFunction {
    static final Logger log = LoggerFactory.getLogger(TransformerFunctionUrlDecode.class);

    public TransformerFunctionUrlDecode() {
        super(FunctionDescription.of(
            Map.of(
            "charset", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("UTF-8")
            )
        ));
    }

    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
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
