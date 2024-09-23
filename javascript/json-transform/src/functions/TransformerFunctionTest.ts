import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionTest extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        pattern: { type: ArgType.String, position: 0 },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return false;
    }
    let patternString = await context.getString("pattern");
    if (patternString == null) {
      return false;
    }
    let flags = "";
    if (patternString.startsWith("(?i)")) {
      flags = "i";
      patternString = patternString.substring(4);
    }
    if (patternString.startsWith("(?m)")) {
      flags += "m";
      patternString = patternString.substring(4);
    }
    return new RegExp(patternString, flags).test(str);
  }
}

export default TransformerFunctionTest;
