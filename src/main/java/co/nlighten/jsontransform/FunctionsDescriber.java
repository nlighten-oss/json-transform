package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import co.nlighten.jsontransform.adapters.JsonObjectAdapter;
import co.nlighten.jsontransform.functions.annotations.*;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class FunctionsDescriber<JE, JA extends Iterable<JE>, JO extends JE> {

    private final JsonObjectAdapter<JE,JA,JO> OBJECT;
    private final JsonArrayAdapter<JE, JA, JO> ARRAY;
    private final JsonAdapter<JE, JA, JO> adapter;

    public FunctionsDescriber(JsonAdapter<JE, JA, JO> jsonAdapter) {
        this.adapter = jsonAdapter;
        this.ARRAY = jsonAdapter.ARRAY;
        this.OBJECT = jsonAdapter.OBJECT;
    }

    private JO convertToSchema(ArgType argType) {
        if (argType == ArgType.Any || argType == ArgType.Transformer) return null;
        var res = OBJECT.create();
        String type;
        switch (argType) {
            case Enum -> {
                OBJECT.add(res, "enum", ARRAY.create());
                type = "string";
            }
            case BigDecimal -> {
                OBJECT.add(res, "$comment", argType.name());
                type = "number";
            }
            case Long -> {
                OBJECT.add(res, "$comment", argType.name());
                type = "integer";
            }
            case ArrayOfArray -> {
                OBJECT.add(res, "items", convertToSchema(ArgType.Array));
                type = "array";
            }
            case ArrayOfBigDecimal -> {
                OBJECT.add(res, "items", convertToSchema(ArgType.BigDecimal));
                type = "array";
            }
            case ArrayOfString -> {
                OBJECT.add(res, "items", convertToSchema(ArgType.String));
                type = "array";
            }
            default -> {
                type = argType.name().toLowerCase();
            }
        }
        OBJECT.add(res, "type", type);
        return res;
    }

    private JO oneOf(ArgType[] types) {
        var res = OBJECT.create();
        var of = ARRAY.create();
        Arrays.stream(types).forEach(argType -> ARRAY.add(of, convertToSchema(argType)));
        OBJECT.add(res, "oneOf", of);
        return res;
    }

    private JO describe(TransformerFunction<JE, JA, JO> function) {
        var jo = OBJECT.create();
        // description
        var desc = function.getClass().getAnnotationsByType(Documentation.class);
        if (desc.length > 0) {
            var ann = Arrays.stream(desc).findFirst().get();
            OBJECT.add(jo, "description", ann.value());
            if (ann.notes() != null && !ann.notes().isEmpty()) {
                OBJECT.add(jo, "notes", ann.notes());
            }
        }
        // inputSchema
        var inputType = function.getInputType();
        if (inputType != null) {
            var inputTypeArr = inputType.value();
            var inputSchema = inputTypeArr.length == 1 ? convertToSchema(inputTypeArr[0]) : oneOf(inputTypeArr);
            if (inputType.description() != null && !inputType.description().isEmpty()) {
                if (inputSchema == null) { // probably "Any"
                    inputSchema = OBJECT.create();
                }
                OBJECT.add(inputSchema, "description", inputType.description());
            }
            OBJECT.add(jo, "inputSchema", inputSchema);
        }
        // outputSchema
        var outputType = Arrays.stream(function.getClass().getAnnotationsByType(OutputType.class)).findFirst().orElse(null);
        if (outputType != null) {
            var outputTypeArr = outputType.value();
            var outputSchema = outputTypeArr.length == 1 ? convertToSchema(outputTypeArr[0]) : oneOf(outputTypeArr);
            if (outputType.description() != null && !outputType.description().isEmpty()) {
                if (outputSchema == null) { // probably "Any"
                    outputSchema = OBJECT.create();
                }
                OBJECT.add(outputSchema, "description", outputType.description());
            }
            OBJECT.add(jo, "outputSchema", outputSchema);
        }

        var args = function.getArguments();
        if (args != null && !args.isEmpty()) {
            var arguments = ARRAY.create();
            for (var entry : args.entrySet()) {
                var arg = entry.getKey();
                var val = entry.getValue();
                var ao = OBJECT.create();
                OBJECT.add(ao, "name", arg);
                if (val.description() != null) {
                    OBJECT.add(ao, "description", val.description());
                }
                OBJECT.add(ao, "type", val.type().name().toLowerCase());
                if (val.enumValues().length > 0) {
                    var enumValues = ARRAY.create();
                    Arrays.stream(val.enumValues()).forEach(item -> ARRAY.add(enumValues, item));
                    OBJECT.add(ao, "enum", enumValues);
                }
                if (val.position() > -1) {
                    OBJECT.add(ao, "position", val.position());
                }
                if (val.required()) {
                    OBJECT.add(ao, "required", true);
                } else {
                    var defaultValue = function.getDefaultValue(arg);
                    if (defaultValue != null) {
                        OBJECT.add(ao, "default", adapter.wrap(defaultValue));
                    }
                }
                ARRAY.add(arguments, ao);
            }
            OBJECT.add(jo, "arguments", arguments);
        }
        var piped = function.getClass().getAnnotationsByType(TypeIsPiped.class);
        if (piped.length > 0) {
            OBJECT.add(jo, "pipedType", true);
        }
        return jo;
    }

    public JO describe(Map<String, TransformerFunction<JE, JA, JO>> functions) {
        var result = OBJECT.create();
        var keys = functions.keySet().stream().sorted().toList();
        for (String key : keys) {
            var func = functions.get(key);
            var desc = describe(func);

            var aliases = Arrays.stream(func.getClass().getAnnotationsByType(
                    Aliases.class)).findFirst().orElse(null);
            if (aliases != null) {
                var aliasesValue = aliases.value();
                for (int i = 1; i < aliasesValue.length; i++) {
                    if (Objects.equals(aliasesValue[i], key)) {
                        OBJECT.add(desc, "aliasTo", aliasesValue[0]);
                        break;
                    }
                }
            }

            var deprecatedAlias = Arrays.stream(func.getClass().getAnnotationsByType(
                    DeprecatedAlias.class)).findFirst().orElse(null);
            if (deprecatedAlias != null && deprecatedAlias.value().equals(key)) {
                if (aliases != null) {
                    OBJECT.add(desc, "deprecated", aliases.value()[0]);
                } else {
                    OBJECT.add(desc, "deprecated", true);
                }
            }

            OBJECT.add(result, key, desc);
        }
        return result;
    }
}
