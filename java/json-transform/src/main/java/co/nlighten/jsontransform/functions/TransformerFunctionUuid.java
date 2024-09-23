package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;
import co.nlighten.shortuuid.ShortUuid;
import co.nlighten.shortuuid.UuidConverter;

import java.util.UUID;

/*
 * For tests
 * @see TransformerFunctionUuidTest
 */
@ArgumentType(value = "format", type = ArgType.Enum, position = 0, defaultEnum = "CANONICAL")
@ArgumentType(value = "namespace", type = ArgType.String, position = 1, defaultIsNull = true)
public class TransformerFunctionUuid<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionUuid(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getUnwrapped(null);
        if (value == null) {
            return null;
        }
        var format = context.getEnum("format");
        if ("V3".equals(format) || "V5".equals(format)) {
            var name = context.getAsString(value);
            var namespaceValue = context.get("namespace");
            var namespace = namespaceValue == null
                            ? null
                            : namespaceValue instanceof UUID uu
                              ? uu
                              : UUID.fromString(context.getAsString(namespaceValue));
            return UuidConverter.namedByVersion("V3".equals(format) ? 3 : 5, namespace, name);
        }
        var uuid = value instanceof UUID uu ? uu : UUID.fromString(context.getAsString(value));
        return switch (format) {
            case "N", "NO_HYPHENS" -> uuid.toString().replace("-", "");
            case "B62", "BASE62" -> ShortUuid.toShortUuid(uuid, true);
            case "B64", "BASE64" -> UuidConverter.toBase64(uuid);
            case "B36", "BASE36" -> ShortUuid.toShortUuid(uuid, false);
            default -> uuid.toString();
        };
    }
}
