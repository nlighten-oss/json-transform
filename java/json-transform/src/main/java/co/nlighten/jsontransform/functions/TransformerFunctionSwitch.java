package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransformerFunctionSwitch extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionSwitch.class);

    public TransformerFunctionSwitch() {
        super(FunctionDescription.of(
            Map.of(
            "cases", ArgumentType.of(ArgType.Object).position(0).defaultIsNull(true),
            "default", ArgumentType.of(ArgType.Any).position(1).defaultIsNull(true)
            )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var alias = context.getAlias();
        var value = context.getString(null);
        var adapter = context.getAdapter();
        var caseMap = context.getJsonElement("cases");
        if (!adapter.isJsonObject(caseMap)) {
            logger.warn("{}.cases was not specified with an object as case map", alias);
            return null;
        }
        return adapter.has(caseMap, value)
                ? adapter.get(caseMap, value)
                : context.getJsonElement("default");
    }
}
