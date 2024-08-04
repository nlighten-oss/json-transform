import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import TextEncoding from "./common/TextEncoding";
import Base64 from "./utils/Base64";
import md5 from "./utils/md5";

const globalCrypto = typeof global.crypto !== "undefined" ? global.crypto : require("crypto");

function hashCode(input: string) {
  let h: Int32Array | null = new Int32Array(1);
  for (let i = 0; i < input.length; i++) {
    h[0] = 31 * h[0] + (input.charCodeAt(i) & 0xff);
  }
  const result = h[0];
  h = null; // free buffer
  return result;
}
function formatHex(a: ArrayBuffer) {
  return [...new Uint8Array(a)].map(x => x.toString(16).padStart(2, "0")).join("");
}

const DESCRIPTION: FunctionDescription = {
  aliases: ["digest"],
  description: "",
  inputType: ArgType.Any,
  arguments: {
    algorithm: {
      type: ArgType.Enum,
      position: 0,
      defaultEnum: "SHA-1",
      enumValues: ["SHA-1", "SHA-256", "SHA-384", "SHA-512", "MD5", "JAVA"],
      description: "Hashing algorithm (as defined by Java Security Standard Algorithm Names)",
    },
    format: {
      type: ArgType.Enum,
      position: 1,
      defaultEnum: "BASE64",
      enumValues: ["BASE64", "BASE64URL", "HEX"],
      description:
        'Format of output (BASE64 = "The Base64 Alphabet" from RFC-2045, BAS64URL = "URL and Filename safe Base64 Alphabet" from RFC-4648, HEX = Hexadecimal string)',
    },
  },
  outputType: ArgType.String,
};
class TransformerFunctionDigest extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const algorithm = await context.getEnum("algorithm");
    if (algorithm === "JAVA") {
      return hashCode(str);
    }
    const digest = !algorithm
      ? new ArrayBuffer(0)
      : algorithm === "MD5"
        ? md5(str)
        : await globalCrypto.subtle.digest({ name: algorithm }, TextEncoding.encode(str, "ISO-8859-1"));
    switch (await context.getEnum("format")) {
      case "BASE64":
        return Base64.encode(new Uint8Array(digest), "basic");
      case "BASE64URL":
        return Base64.encode(new Uint8Array(digest), "url");
      default:
        return formatHex(digest);
    }
  }
}

export default TransformerFunctionDigest;