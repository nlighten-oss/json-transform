package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class TransformerFunctionBase64 extends TransformerFunction {

    static final Logger log = LoggerFactory.getLogger(TransformerFunctionBase64.class);

    public TransformerFunctionBase64() {
        super(FunctionDescription.of(
            Map.of(
            "action", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("ENCODE"),
            "rfc", ArgumentType.of(ArgType.Enum).position(1).defaultEnum("BASIC"),
            "without_padding", ArgumentType.of(ArgType.Boolean).position(2).defaultBoolean(false),
            "charset", ArgumentType.of(ArgType.Enum).position(3).defaultEnum("UTF-8")
            )
        ));
    }

    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
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
