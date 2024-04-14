package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import co.nlighten.jsontransform.adapters.JsonObjectAdapter;
import co.nlighten.jsontransform.functions.TransformerFunctionRaw;
import co.nlighten.jsontransform.functions.annotations.*;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class FunctionsDescriber<JE, JA extends Iterable<JE>, JO extends JE> {

    private final String internalPackageName;
    private final JsonObjectAdapter<JE,JA,JO> jObject;
    private final JsonArrayAdapter<JE, JA, JO> jArray;
    private final JsonAdapter<JE, JA, JO> adapter;

    public FunctionsDescriber(JsonAdapter<JE, JA, JO> jsonAdapter) {
        this.adapter = jsonAdapter;
        this.jArray = jsonAdapter.jArray;
        this.jObject = jsonAdapter.jObject;
        this.internalPackageName = TransformerFunctionRaw.class.getPackageName();
    }

    private JO convertToSchema(ArgType argType) {
        if (argType == ArgType.Any || argType == ArgType.Transformer) return null;
        var res = jObject.create();
        String type;
        switch (argType) {
            case Enum -> {
                jObject.add(res, "enum", jArray.create());
                type = "string";
            }
            case BigDecimal -> {
                jObject.add(res, "$comment", argType.name());
                type = "number";
            }
            case Long -> {
                jObject.add(res, "$comment", argType.name());
                type = "integer";
            }
            case ArrayOfArray -> {
                jObject.add(res, "items", convertToSchema(ArgType.Array));
                type = "array";
            }
            case ArrayOfBigDecimal -> {
                jObject.add(res, "items", convertToSchema(ArgType.BigDecimal));
                type = "array";
            }
            case ArrayOfString -> {
                jObject.add(res, "items", convertToSchema(ArgType.String));
                type = "array";
            }
            default -> {
                type = argType.name().toLowerCase();
            }
        }
        jObject.add(res, "type", type);
        return res;
    }

    private JO oneOf(ArgType[] types) {
        var res = jObject.create();
        var of = jArray.create();
        Arrays.stream(types).forEach(argType -> jArray.add(of, convertToSchema(argType)));
        jObject.add(res, "oneOf", of);
        return res;
    }

    private JO describe(TransformerFunction<JE, JA, JO> function) {
        var jo = jObject.create();
        // description
        var desc = function.getClass().getAnnotationsByType(Documentation.class);
        if (desc.length > 0) {
            var ann = Arrays.stream(desc).findFirst().get();
            jObject.add(jo, "description", ann.value());
            if (ann.notes() != null && !ann.notes().isEmpty()) {
                jObject.add(jo, "notes", ann.notes());
            }
        }
        // inputSchema
        var inputType = function.getInputType();
        if (inputType != null) {
            var inputTypeArr = inputType.value();
            var inputSchema = inputTypeArr.length == 1 ? convertToSchema(inputTypeArr[0]) : oneOf(inputTypeArr);
            if (inputType.description() != null && !inputType.description().isEmpty()) {
                if (inputSchema == null) { // probably "Any"
                    inputSchema = jObject.create();
                }
                jObject.add(inputSchema, "description", inputType.description());
            }
            jObject.add(jo, "inputSchema", inputSchema);
        }
        // outputSchema
        var outputType = Arrays.stream(function.getClass().getAnnotationsByType(OutputType.class)).findFirst().orElse(null);
        if (outputType != null) {
            var outputTypeArr = outputType.value();
            var outputSchema = outputTypeArr.length == 1 ? convertToSchema(outputTypeArr[0]) : oneOf(outputTypeArr);
            if (outputType.description() != null && !outputType.description().isEmpty()) {
                if (outputSchema == null) { // probably "Any"
                    outputSchema = jObject.create();
                }
                jObject.add(outputSchema, "description", outputType.description());
            }
            jObject.add(jo, "outputSchema", outputSchema);
        }
        // arguments
        var args = function.getArguments();
        if (args != null && !args.isEmpty()) {
            var arguments = jArray.create();
            for (var entry : args.entrySet()) {
                var arg = entry.getKey();
                var val = entry.getValue();
                var ao = jObject.create();
                jObject.add(ao, "name", arg);
                if (val.description() != null) {
                    jObject.add(ao, "description", val.description());
                }
                jObject.add(ao, "type", val.type().name().toLowerCase());
                if (val.enumValues().length > 0) {
                    var enumValues = jArray.create();
                    Arrays.stream(val.enumValues()).forEach(item -> jArray.add(enumValues, item));
                    jObject.add(ao, "enum", enumValues);
                }
                if (val.position() > -1) {
                    jObject.add(ao, "position", val.position());
                }
                if (val.required()) {
                    jObject.add(ao, "required", true);
                } else {
                    var defaultValue = function.getDefaultValue(arg);
                    if (defaultValue != null) {
                        jObject.add(ao, "default", adapter.wrap(defaultValue));
                    }
                }
                jArray.add(arguments, ao);
            }
            jObject.add(jo, "arguments", arguments);
        }
        // pipedType
        var piped = function.getClass().getAnnotationsByType(TypeIsPiped.class);
        if (piped.length > 0) {
            jObject.add(jo, "pipedType", true);
        }
        // custom
        var packageName = function.getClass().getPackageName();
        if (!internalPackageName.equals(packageName)) {
            jObject.add(jo, "custom", true);
        }
        return jo;
    }

    public JO describe(Map<String, TransformerFunction<JE, JA, JO>> functions) {
        var result = jObject.create();
        var keys = functions.keySet().stream().sorted().toList();
        for (String key : keys) {
            var func = functions.get(key);
            var desc = describe(func);

            // aliasTo
            var aliases = Arrays.stream(func.getClass().getAnnotationsByType(
                    Aliases.class)).findFirst().orElse(null);
            if (aliases != null) {
                var aliasesValue = aliases.value();
                for (int i = 1; i < aliasesValue.length; i++) {
                    if (Objects.equals(aliasesValue[i], key)) {
                        jObject.add(desc, "aliasTo", aliasesValue[0]);
                        break;
                    }
                }
            }

            // deprecated
            var deprecatedAlias = Arrays.stream(func.getClass().getAnnotationsByType(
                    DeprecatedAlias.class)).findFirst().orElse(null);
            if (deprecatedAlias != null && deprecatedAlias.value().equals(key)) {
                if (aliases != null) {
                    jObject.add(desc, "deprecated", aliases.value()[0]);
                } else {
                    jObject.add(desc, "deprecated", true);
                }
            }

            jObject.add(result, key, desc);
        }
        return result;
    }
}
