package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.ParameterResolver;
import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.template.ParameterDefaultResolveOptions;
import co.nlighten.jsontransform.template.TextTemplate;

import java.util.Map;

import static co.nlighten.jsontransform.functions.common.FunctionContext.pathOfVar;

public class TransformerFunctionTemplate extends TransformerFunction {
    private static final Object DOLLAR = "$";

    public TransformerFunctionTemplate() {
        super(FunctionDescription.of(
                Map.of(
                    "payload", ArgumentType.of(ArgType.Any).position(0).defaultIsNull(true),
                    "default_resolve", ArgumentType.of(ArgType.Enum).position(1).defaultEnum(ParameterDefaultResolveOptions.UNIQUE.name())
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var input = context.getString(null);
        if (input == null) {
            return null;
        }
        var adapter = context.getAdapter();

        var defaultResolveValue = context.getEnum("default_resolve");
        var defaultResolver = ParameterDefaultResolveOptions.valueOf(defaultResolveValue);

        var currentResolver = context.getResolver();
        ParameterResolver resolver = currentResolver;
        var payload = context.getJsonElement("payload");
        if (!adapter.isNull(payload)) {
            var dc = adapter.getDocumentContext(payload);
            resolver = name -> {
                if (pathOfVar("##current", name)) {
                    return dc.read(DOLLAR + name.substring(9));
                }
                return currentResolver.get(name);
            };
        }

        var tt = TextTemplate.get(input, defaultResolver);
        return tt.render(resolver, adapter);
    }
}
