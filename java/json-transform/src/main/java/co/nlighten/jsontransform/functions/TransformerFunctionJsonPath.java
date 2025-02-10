package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import java.util.Map;

/*
 * For tests
 * @see TransformerFunctionJsonPathTest
 */
public class TransformerFunctionJsonPath extends TransformerFunction {
    public TransformerFunctionJsonPath() {
        super(FunctionDescription.of(
                Map.of(
                        "path", ArgumentType.of(ArgType.String).position(0),
                        "options", ArgumentType.of(ArgType.ArrayOfString).position(1).defaultIsNull(true)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var source = context.getJsonElement(null);
        if (source == null) {
            return null;
        }
        var path = context.getString("path");
        if (path == null) {
            return null;
        }
        var adapter = context.getAdapter();
        adapter.setupJsonPath();
        var optionsArray = context.getJsonArray("options");
        if (optionsArray != null && !adapter.isEmpty(optionsArray)) {
            var conf = Configuration.defaultConfiguration();
            for (var option : adapter.asIterable(optionsArray)) {
                conf = conf.addOptions(Option.valueOf(adapter.getAsString(option)));
            }
            return JsonPath.using(conf).parse(source).read(path);
        }
        return JsonPath.read(source, path);
    }
}
