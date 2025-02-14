package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;

public class TransformerFunctionDigest extends TransformerFunction {
    public TransformerFunctionDigest() {
        super(FunctionDescription.of(
            Map.of(
            "algorithm", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("SHA-1"),
            "format", ArgumentType.of(ArgType.Enum).position(1).defaultEnum("BASE64")
            )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
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
