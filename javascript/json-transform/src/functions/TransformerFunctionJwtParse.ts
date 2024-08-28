import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import Base64 from "./utils/Base64";
import TextEncoding from "./common/TextEncoding";

const DESCRIPTION: FunctionDescription = {
  aliases: ["jwtparse"],
  inputType: ArgType.String,
  description: "",
  outputType: ArgType.Any,
};
class TransformerFunctionJwtParse extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const jwt = await context.getString(null);
    try {
      if (jwt === null) {
        return null;
      }
      const dot1 = jwt.indexOf(".");
      if (dot1 == -1) {
        throw new Error("Invalid serialized JWT object: Missing part delimiters");
      }
      const dot2 = jwt.indexOf(".", dot1 + 1);
      if (dot2 == -1) {
        throw new Error("Invalid serialized KWT object: Missing second delimiter");
      }
      const encodedClaimsString = jwt.substring(dot1 + 1, dot2);
      const claimsString = TextEncoding.decode(Base64.decode(encodedClaimsString, "url"));
      if (claimsString.startsWith("{") && claimsString.endsWith("}")) {
        return JSON.parse(claimsString);
      } else {
        return claimsString;
      }
    } catch (e: any) {
      console.warn(`${context.getAlias()} - Failed parsing JWT`, e);
      return null;
    }
  }
}

export default TransformerFunctionJwtParse;
