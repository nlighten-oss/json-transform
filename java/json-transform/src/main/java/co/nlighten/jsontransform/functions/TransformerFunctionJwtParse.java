package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;

/*
 * For tests
 * @see TransformerFunctionJwtParseTest
 */
@Aliases("jwtparse")
@Documentation(value = "Parses JWT tokens and returns their payload",
               notes = "This function does not validate the token. Only returns its payload (claims)")
@InputType(ArgType.String)
@OutputType(ArgType.Any)
public class TransformerFunctionJwtParse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    private static final Logger logger = LoggerFactory.getLogger(TransformerFunctionJwtParse.class);

    public TransformerFunctionJwtParse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var jwt = context.getString(null);
        try {
            final int dot1 = jwt.indexOf(".");
            if (dot1 == -1) {
                throw new ParseException("Invalid serialized JWT object: Missing part delimiters", 0);
            }
            final int dot2 = jwt.indexOf(".", dot1 + 1);
            if (dot2 == -1) {
                throw new ParseException("Invalid serialized KWT object: Missing second delimiter", 0);
            }
            var encodedClaimsString = jwt.substring(dot1 + 1, dot2);
            var claimsString = new String(Base64.getUrlDecoder().decode(encodedClaimsString), StandardCharsets.UTF_8);
            if (claimsString.startsWith("{") && claimsString.endsWith("}")) {
                return context.parse(claimsString);
            } else {
                return claimsString;
            }
        } catch (Exception e) {
            logger.warn("parseAnyJWT - Failed parsing JWT", e);
            return null;
        }
    }
}
