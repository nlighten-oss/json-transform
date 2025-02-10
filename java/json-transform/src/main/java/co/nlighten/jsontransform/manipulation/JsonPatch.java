package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.Objects;

/**
 * JavaScript Object Notation (JSON) Patch (RFC-6902) implementation
 * over a generic JSON implementation
 */
public class JsonPatch {

    private final JsonAdapter<?, ?, ?> adapter;
    private final JsonPointer jsonPointer;

    public JsonPatch(JsonAdapter<?, ?, ?> adapter) {
        this.adapter = adapter;
        this.jsonPointer = new JsonPointer(adapter);
    }

    public Object patch(Object ops, Object source) {
        if (!adapter.isJsonObject(source) && !adapter.isJsonArray(ops)) {
            throw new IllegalArgumentException("Invalid source argument (an object or array is required)");
        }
        if (ops == null || adapter.size(ops) == 0) {
            return source;
        }

        var result = adapter.clone(source);

        for (var operation : adapter.asIterable(ops)) {
            if (!adapter.isJsonObject(operation)) {
                throw new IllegalArgumentException("Invalid operation: " + operation);
            }
            result = perform(operation, result);
        }

        return result;
    }

    private Object getRequiredValueArgument(Object operation, String context) {
        if (!adapter.has(operation, "value")) {
            throw new IllegalArgumentException(context + " - Missing argument \"value\"");
        }
        return adapter.get(operation, "value");
    }

    private String getRequiredStringArgument(Object operation, String arg, String context) {
        var el = adapter.get(operation, arg);
        if (!adapter.isJsonString(el)) {
            throw new IllegalArgumentException(context + " - Invalid argument \"" + arg + "\" = " + (el == null ? "null" : el));
        }
        return adapter.getAsString(el);
    }

    private String getRequiredPathArgument(Object operation, String arg, String context) {
        var path = getRequiredStringArgument(operation, arg, context);
        if (!path.isEmpty() && path.charAt(0) != '/') {
            throw new IllegalArgumentException(context + " - Invalid argument \"" + arg + "\" = " + path);
        }
        return path;
    }

    private Object perform(Object operation, Object doc) {
        var op = getRequiredStringArgument(operation, "op", "JsonPatch.perform");
        var path = getRequiredPathArgument(operation, "path", op);
        var result = doc;

        switch (op) {
            case "add" -> {
                var value = getRequiredValueArgument(operation, op);
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
                var value = getRequiredValueArgument(operation, op);
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
                var value = getRequiredValueArgument(operation, op);
                if (!Objects.equals(jsonPointer.get(doc, path), value)) {
                    throw new RuntimeException("test - The value does not equal value from path");
                }
            }
            default -> throw new IllegalArgumentException("Invalid argument \"op\" = " + op);
        }
        return result;
    }
}