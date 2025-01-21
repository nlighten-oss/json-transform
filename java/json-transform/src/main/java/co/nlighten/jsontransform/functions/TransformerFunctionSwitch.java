package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * For tests
 * @see TransformerFunctionSwitchTest
 */
@ArgumentType(value = "cases", type = ArgType.Object, position = 0, defaultIsNull = true)
@ArgumentType(value = "default", type = ArgType.Any, position = 1, defaultIsNull = true)
public class TransformerFunctionSwitch<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionSwitch.class);

    public TransformerFunctionSwitch(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var alias = context.getAlias();
        var value = context.getString(null);
        var caseMapEl = context.getJsonElement("cases");
        if (!jObject.is(caseMapEl)) {
            logger.warn("{}.cases was not specified with an object as case map", alias);
            return null;
        }
        var caseMap = jObject.convert(caseMapEl);
        return jObject.has(caseMap, value)
                ? jObject.get(caseMap, value)
                : context.getJsonElement("default");
    }
}
