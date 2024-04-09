package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.formurlencoded.FormUrlEncodedFormat;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/*
 * For tests
 * @see TransformerFunctionUriParseTest
 */
@Aliases("uriparse")
@Documentation("Parses a URI to its components")
@InputType(ArgType.String)
@OutputType(ArgType.Object)
public class TransformerFunctionUriParse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger log = LoggerFactory.getLogger(TransformerFunctionUriParse.class);
    private final FormUrlEncodedFormat<JE, JA, JO> formUrlFormat;

    public TransformerFunctionUriParse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
        this.formUrlFormat = new FormUrlEncodedFormat<>(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        try {
            var result = OBJECT.create();
            URI uri = new URI(str);
            OBJECT.add(result, "scheme", uri.getScheme());
            var userInfo = uri.getUserInfo();
            if (userInfo != null) {
                OBJECT.add(result, "user_info", userInfo);
            }
            OBJECT.add(result, "authority", uri.getAuthority());
            var port = uri.getPort();
            OBJECT.add(result, "host", port > -1 ? uri.getHost() + ":" + port : uri.getHost());
            OBJECT.add(result, "hostname", uri.getHost());
            if (port > -1) {
                OBJECT.add(result, "port", port);
            }
            OBJECT.add(result, "path", uri.getPath());
            var queryString = uri.getQuery();
            if (queryString != null) {
                OBJECT.add(result, "query", formUrlFormat.deserialize(queryString));
                OBJECT.add(result, "query_raw", queryString);
            }
            var fragment = uri.getFragment();
            if (fragment != null) {
                OBJECT.add(result, "fragment", fragment);
            }
            return result;
        } catch (URISyntaxException e) {
            log.warn("Failed parsing uri", e);
            return null;
        }
    }
}
