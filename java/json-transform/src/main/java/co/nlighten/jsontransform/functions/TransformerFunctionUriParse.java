package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.formurlencoded.FormUrlEncodedFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class TransformerFunctionUriParse extends TransformerFunction {
    static final Logger log = LoggerFactory.getLogger(TransformerFunctionUriParse.class);

    public TransformerFunctionUriParse() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        try {
            var adapter = context.getAdapter();
            var result = adapter.createObject();
            URI uri = new URI(str);
            adapter.add(result, "scheme", uri.getScheme());
            var userInfo = uri.getUserInfo();
            if (userInfo != null) {
                adapter.add(result, "user_info", userInfo);
            }
            adapter.add(result, "authority", uri.getAuthority());
            var port = uri.getPort();
            adapter.add(result, "host", port > -1 ? uri.getHost() + ":" + port : uri.getHost());
            adapter.add(result, "hostname", uri.getHost());
            if (port > -1) {
                adapter.add(result, "port", port);
            }
            adapter.add(result, "path", uri.getPath());
            var queryString = uri.getQuery();
            if (queryString != null) {
                // TODO: how to create the format once?
                var formUrlFormat = new FormUrlEncodedFormat(adapter);
                adapter.add(result, "query", formUrlFormat.deserialize(queryString));
                adapter.add(result, "query_raw", queryString);
            }
            var fragment = uri.getFragment();
            if (fragment != null) {
                adapter.add(result, "fragment", fragment);
            }
            return result;
        } catch (URISyntaxException e) {
            log.warn("Failed parsing uri", e);
            return null;
        }
    }
}
