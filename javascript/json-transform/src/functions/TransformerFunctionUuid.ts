import { parse, v3, v5, validate, stringify } from "uuid";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";
import ShortUuid from "./utils/ShortUuid";
import Base64 from "./utils/Base64";
import md5 from "./utils/md5";
import TextEncoding from "./common/TextEncoding";

const stringifyUuid = (bytes: Uint8Array) => {
  const hex = bytes.reduce((acc, val) => acc + val.toString(16).padStart(2, "0"), "");
  return [hex.slice(0, 8), hex.slice(8, 12), hex.slice(12, 16), hex.slice(16, 20), hex.slice(20)].join("-");
};

class TransformerFunctionUuid extends TransformerFunction {
  HYPHEN_REGEX = /-/g;

  constructor() {
    super({
      arguments: {
        format: { type: ArgType.Enum, position: 0, defaultEnum: "CANONICAL" },
        namespace: { type: ArgType.String, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const uuid = await context.getString(null);
    if (uuid == null) {
      return null;
    }
    const format = (await context.getEnum("format")) ?? "CANONICAL";
    if (format[0] !== "V" && !validate(uuid)) {
      // validate if not v3/v5
      return null;
    }
    switch (format) {
      case "N":
      case "NO_HYPHENS":
        return uuid.toString().replace(this.HYPHEN_REGEX, "");
      case "B62":
      case "BASE62":
        return ShortUuid.toShortUuid(uuid, true);
      case "B64":
      case "BASE64": {
        const buffer = parse(uuid);
        return Base64.encode(buffer, "basic", true);
      }
      case "B36":
      case "BASE36":
        return ShortUuid.toShortUuid(uuid, false);
      case "V3": {
        const ns = await context.getString("namespace");
        if (ns) {
          return v3(uuid, ns);
        }
        // non-standard java implementation
        const bytes = md5(TextEncoding.encode(uuid, "ISO-8859-1"));
        bytes[6] = (bytes[6] & 0x0f) | 0x30 /* version */;
        bytes[8] = (bytes[8] & 0x3f) | 0x80;
        return stringifyUuid(bytes);
      }
      case "V5": {
        const ns = await context.getString("namespace");
        return ns ? v5(uuid, ns) : null;
      }
      default:
        return uuid.toString();
    }
  }
}

export default TransformerFunctionUuid;
