package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * For tests
 * @see TransformerFunctionSwitchTest
 */
@Aliases("switch")
@Documentation("Switch-case expression. Value is compared to each of the keys in cases and a matching **key** will result with its **value**, otherwise `default` value or `null` will be returned.")
@InputType(value = ArgType.Any, description = "Value to test")
@ArgumentType(value = "cases", type = ArgType.Object, position = 0, defaultIsNull = true, required = true,
              description = "A map of cases (string to value)")
@ArgumentType(value = "default", type = ArgType.Any, position = 1, defaultIsNull = true,
              description = "Fallback value in case no match to any key in cases")
@OutputType(value = ArgType.Any, description = "Same as `default` value or one of the `cases` values")
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
        if (!OBJECT.is(caseMapEl)) {
            logger.warn("{}.cases was not specified with an object as case map", alias);
            return null;
        }
        var caseMap = OBJECT.convert(caseMapEl);
        return OBJECT.has(caseMap, value)
               ? OBJECT.get(caseMap, value)
               : context.getJsonElement("default");
    }
}
