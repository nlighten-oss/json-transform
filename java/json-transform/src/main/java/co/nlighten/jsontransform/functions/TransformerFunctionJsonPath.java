package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.adapters.gson.GsonJsonPathConfigurator;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

/*
 * For tests
 * @see TransformerFunctionJsonPathTest
 */
@ArgumentType(value = "path", type = ArgType.String, position = 0)
@ArgumentType(value = "options", type = ArgType.ArrayOfString, position = 1, defaultIsNull = true)
public class TransformerFunctionJsonPath<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionJsonPath(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var source = context.getJsonElement(null);
        if (source == null) {
            return null;
        }
        var path = context.getString("path");
        if (path == null) {
            return null;
        }
        GsonJsonPathConfigurator.setup();
        var optionsArray = context.getJsonArray("options");
        if (optionsArray != null && !jArray.isEmpty(optionsArray)) {
            var conf = Configuration.defaultConfiguration();
            for (var option : optionsArray) {
                conf = conf.addOptions(Option.valueOf(adapter.getAsString(option)));
            }
            return JsonPath.using(conf).parse(source).read(path);
        }
        return JsonPath.read(source, path);
    }
}
