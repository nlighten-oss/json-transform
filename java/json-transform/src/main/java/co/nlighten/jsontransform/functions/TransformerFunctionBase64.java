package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/*
 * For tests
 * @see TransformerFunctionBase64Test
 */
@ArgumentType(value = "action", type = ArgType.Enum, position = 0, defaultEnum = "ENCODE")
@ArgumentType(value = "rfc", type = ArgType.Enum, position = 1, defaultEnum = "BASIC")
@ArgumentType(value = "without_padding", type = ArgType.Boolean, position = 2, defaultBoolean = false)
@ArgumentType(value = "charset", type = ArgType.Enum, position = 3, defaultEnum = "UTF-8")
public class TransformerFunctionBase64<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    static final Logger log = LoggerFactory.getLogger(TransformerFunctionBase64.class);

    public TransformerFunctionBase64(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        // parse arguments
        var encode = context.getEnum("action").charAt(0) == 'E'; // E / Enc / Encode (anything else like d / dec / decode is for decoding)
        var rfc = context.getEnum("rfc"); // B = basic / U = url / M = mime
        var withoutPadding = context.getBoolean("without_padding", false);
        var charset = context.getEnum("charset");

        String result;
        try {
            if (encode) {
                var encoder = rfc.charAt(0) == 'M' ? Base64.getMimeEncoder() :
                              rfc.charAt(0) == 'U' ? Base64.getUrlEncoder() : Base64.getEncoder();
                if (withoutPadding) {
                    encoder = encoder.withoutPadding();
                }
                result = new String(encoder.encode(str.getBytes(charset)), StandardCharsets.US_ASCII);

            } else {
                var decoder = rfc.charAt(0) == 'M' ? Base64.getMimeDecoder() :
                              rfc.charAt(0) == 'U' ? Base64.getUrlDecoder() : Base64.getDecoder();
                // padding isn't relevant in this implementation
                result = new String(decoder.decode(str), charset);
            }
        }
        catch (UnsupportedEncodingException e) {
            log.warn(context.getAlias() + " failed", e);
            return null;
        }
        return result;
    }
}
