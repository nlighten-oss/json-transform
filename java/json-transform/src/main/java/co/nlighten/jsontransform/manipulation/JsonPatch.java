package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.Objects;

/**
 * JavaScript Object Notation (JSON) Patch (RFC-6902) implementation
 * over GSON's JSON implementation
 */
public class JsonPatch<JE, JA extends Iterable<JE>, JO extends JE> {

    private final JsonAdapter<JE, JA, JO> adapter;
    private final JsonPointer<JE, JA, JO> jsonPointer;

    public JsonPatch(JsonAdapter<JE, JA, JO> adapter) {
        this.adapter = adapter;
        this.jsonPointer = new JsonPointer<>(adapter);
    }

    public JE patch(JA ops, JE source) {
        if (!adapter.jObject.is(source) && !adapter.jArray.is(ops)) {
            throw new IllegalArgumentException("Invalid source argument (an object or array is required)");
        }
        if (ops == null || adapter.jArray.size(ops) == 0) {
            return source;
        }

        var result = adapter.clone(source);

        for (JE operation : ops) {
            if (!adapter.jObject.is(operation)) {
                throw new IllegalArgumentException("Invalid operation: " + operation);
            }
            result = perform((JO)operation, result);
        }

        return result;
    }

    private JE getRequiredJEArgument(JO operation, String arg, String context) {
        if (!adapter.jObject.has(operation, arg)) {
            throw new IllegalArgumentException(context + " - Missing argument \"" + arg);
        }
        return adapter.jObject.get(operation, arg);
    }

    private String getRequiredStringArgument(JO operation, String arg, String context) {
        var el = adapter.jObject.get(operation, arg);
        if (!adapter.isJsonString(el)) {
            throw new IllegalArgumentException(context + " - Invalid argument \"" + arg + "\" = " + (el == null ? "null" : el));
        }
        return adapter.getAsString(el);
    }

    private String getRequiredPathArgument(JO operation, String arg, String context) {
        var path = getRequiredStringArgument(operation, arg, context);
        if (path.length() != 0 && path.charAt(0) != '/') {
            throw new IllegalArgumentException(context + " - Invalid argument \"" + arg + "\" = " + path);
        }
        return path;
    }

    private JE perform(JO operation, JE doc) {
        var op = getRequiredStringArgument(operation, "op", "JsonPatch.perform");
        var path = getRequiredPathArgument(operation, "path", op);
        var result = doc;

        switch (op) {
            case "add" -> {
                var value = getRequiredJEArgument(operation, "value", op);
                result = jsonPointer.set(doc, path, value, true);
            }
            case "remove" -> {
                var pathValue = jsonPointer.get(doc, path);
                if (pathValue == null) {
                    throw new RuntimeException("remove - Path location MUST exist");
                }
                jsonPointer.remove(doc, path);
            }
            case "replace" -> {
                var value = getRequiredJEArgument(operation, "value", op);
                if (jsonPointer.get(doc, path) == null) {
                    throw new RuntimeException("replace - Target location MUST exist");
                }
                result = jsonPointer.set(doc, path, value);
            }
            case "move" -> {
                String from = getRequiredPathArgument(operation, "from", op);
                var fromValue = jsonPointer.get(doc, from);
                if (fromValue == null) {
                    throw new RuntimeException("move - From location MUST exist");
                }
                jsonPointer.remove(doc, from);
                result = jsonPointer.set(doc, path, fromValue, true);
            }
            case "copy" -> {
                var from = getRequiredPathArgument(operation, "from", op);
                var fromValue = jsonPointer.get(doc, from);
                if (fromValue == null) {
                    throw new RuntimeException("copy - From location MUST exist");
                }
                result = jsonPointer.set(doc, path, fromValue, true);
            }
            case "test" -> {
                var value = getRequiredJEArgument(operation, "value", op);
                if (!Objects.equals(jsonPointer.get(doc, path), value)) {
                    throw new RuntimeException("test - The value does not equal value from path");
                }
            }
            default -> throw new IllegalArgumentException("Invalid argument \"op\" = " + op);
        }
        return result;
    }
}