package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

/*
 * For tests
 * @see TransformerFunctionDigestTest
 */
@ArgumentType(value = "algorithm", type = ArgType.Enum, position = 0, defaultEnum = "SHA-1")
@ArgumentType(value = "format", type = ArgType.Enum, position = 1, defaultEnum = "BASE64")
public class TransformerFunctionDigest<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionDigest(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var algorithm = context.getEnum("algorithm");
        if (algorithm.equals("JAVA")) {
            return str.hashCode();
        }
        try {
            var digest = MessageDigest.getInstance(algorithm).digest(str.getBytes());
            return switch (context.getEnum("format")) {
                case "BASE64" -> Base64.getEncoder().encodeToString(digest);
                case "BASE64URL" -> Base64.getUrlEncoder().encodeToString(digest);
                default -> HexFormat.of().formatHex(digest);
            };
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
