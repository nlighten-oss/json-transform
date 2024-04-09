package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;
import co.nlighten.shortuuid.ShortUuid;
import co.nlighten.shortuuid.UuidConverter;

import java.util.UUID;

/*
 * For tests
 * @see TransformerFunctionUuidTest
 */
@Aliases("uuid")
@Documentation(value = "Format and transform UUID",
               notes = """
- `NO_HYPHENS` (can also be specified as `N`) - Same as `CANONICAL` with hyphens removed
- `BASE36` (can also be specified as `B36`) - alphanumeric alphabet
- `BASE62` (can also be specified as `B62`) - alphanumeric alphabet, case sensitive
- `BASE64` (can also be specified as `B64`) - "URL and Filename safe Base64 Alphabet"
- `V3` - Consider input as name and generate a UUIDv3 (name-based, RFC 4122) (namespace optionally used)
- `V5` - Consider input as name and generate a UUIDv5 (name-based, RFC 4122) (namespace optionally used)
""")
@InputType(value = ArgType.String, description = "Input must be a UUID in standard string format (RFC 4122; with hyphens), can be used in conjunction with `#uuid`")
@ArgumentType(value = "format", type = ArgType.Enum, position = 0, defaultEnum = "CANONICAL",
              enumValues = {"CANONICAL", "NO_HYPHENS", "BASE62", "BASE64", "BASE36", "V3", "V5" },
              description = "Formatting (or generation in case of v3/v5)")
@ArgumentType(value = "namespace", type = ArgType.String, position = 1, defaultIsNull = true,
              description = "UUID to be used as salt (for V3/V5)")
@OutputType(ArgType.String)
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
