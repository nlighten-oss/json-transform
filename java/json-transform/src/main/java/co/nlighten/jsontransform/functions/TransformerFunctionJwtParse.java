package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;

public class TransformerFunctionJwtParse extends TransformerFunction {

    private static final Logger logger = LoggerFactory.getLogger(TransformerFunctionJwtParse.class);

    public TransformerFunctionJwtParse() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
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
                return context.getAdapter().parse(claimsString);
            } else {
                return claimsString;
            }
        } catch (Exception e) {
            logger.warn("parseAnyJWT - Failed parsing JWT", e);
            return null;
        }
    }
}
