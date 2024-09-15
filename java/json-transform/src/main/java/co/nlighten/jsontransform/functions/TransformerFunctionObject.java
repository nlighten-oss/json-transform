package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

/*
 * For tests
 * @see TransformerFunctionObjectTest
 */
@Aliases("object")
@Documentation(value = "Reduces an array of entries into an object",
               notes = "- Entry is in the form of [ key, value ]")
@InputType(ArgType.ArrayOfArray)
@OutputType(ArgType.Object)
public class TransformerFunctionObject<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionObject(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        var result = jObject.create();
        if (streamer != null) {
            streamer.stream().forEach(entry -> {
                if (jArray.is(entry)) {
                    var ja = (JA)entry;
                    var size = jArray.size(ja);
                    if (size > 1) {
                        var key = jArray.get(ja, 0);
                        if (!adapter.isNull(key)) {
                            jObject.add(result, context.getAsString(key), jArray.get(ja, 1));
                        }
                    }
                }
            });
        }
        return result;
    }
}
