package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

/*
 * For tests
 * @see TransformerFunctionDigestTest
 */
@Aliases("digest")
@Documentation("Creates a message digest based on a supported algorithm")
@InputType(ArgType.String)
@ArgumentType(value = "algorithm", type = ArgType.Enum, position = 0, defaultEnum = "SHA-1",
              enumValues = {"SHA-1","SHA-256","SHA-384","SHA-512","MD5","JAVA"},
              description = "Hashing algorithm (as defined by Java Security Standard Algorithm Names)")
@ArgumentType(value = "format", type = ArgType.Enum, position = 1, defaultEnum = "BASE64",
              enumValues = {"BASE64","BASE64URL","HEX"},
              description = "Format of output (BASE64 = \"The Base64 Alphabet\" from RFC-2045, BAS64URL = \"URL and Filename safe Base64 Alphabet\" from RFC-4648, HEX = Hexadecimal string)")
@OutputType(value = {ArgType.Boolean, ArgType.Integer})
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
