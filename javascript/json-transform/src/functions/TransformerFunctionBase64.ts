import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import TextEncoding from "./common/TextEncoding";
import Base64 from "./utils/Base64";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";

const DESCRIPTION : FunctionDescription = {
  aliases: ["base64"],
  inputType: ArgType.String,
  description: "",
  arguments: {
    action: {
      type: ArgType.Enum, position: 0, enumValues: ['ENCODE', 'DECODE'], defaultEnum: 'ENCODE',
      description: "Whether to encode or decode input"
    },
    rfc: {
      type: ArgType.Enum, position: 1, enumValues: ['BASIC', 'URL', 'MIME'], defaultEnum: 'BASIC',
      description: "Which alphabet to use (BASIC = \"The Base64 Alphabet\" from RFC-2045, URL = \"URL and Filename safe Base64 Alphabet\" from RFC-4648, MIME = Same as BASIC but in lines with no more than 76 characters each)"
    },
    without_padding: {
      type: ArgType.Boolean, position: 2, defaultBoolean: false,
      description: "Don't add padding at the end of the output (The character `=`)"
    },
    charset: {
      type: ArgType.Enum, position: 3, defaultEnum: 'UTF-8',
      description: "Character set to use before encoding or when decoding to string"
    }
  },
  outputType: ArgType.String
};
class TransformerFunctionBase64 extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const str = context.getString(null);
    if (str == null) {
      return null;
    }
    // parse arguments
    const encode = context.getEnum("action")?.charAt(0) == 'E'; // E / Enc / Encode (anything else like d / dec / decode is for decoding)
    const rfc = context.getEnum("rfc") ?? "B"; // B = basic / U = url / M = mime
    const withoutPadding = context.getBoolean("without_padding", false);
    const charset = context.getEnum("charset");
    const rfcType = rfc.charAt(0) === 'U' ? 'url' : rfc.charAt(0) === 'M' ? 'mime' : 'basic';

    try {
      if (encode) {
        let input = TextEncoding.encode(str, charset);
        return Base64.encode(input, rfcType, withoutPadding);
      } else {
        const buffer = Base64.decode(str, rfcType);
        return TextEncoding.decode(buffer, charset);
      }
    }
    catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionBase64;