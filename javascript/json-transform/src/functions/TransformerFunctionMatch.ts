import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";

const DESCRIPTION: FunctionDescription = {
  aliases: ["match"],
  description: "",
  inputType: ArgType.Object,
  arguments: {
    pattern: {
      type: ArgType.String,
      position: 0,
      required: true,
      description: "Regular expression to match and extract from input string",
    },
    group: { type: ArgType.Integer, position: 1, defaultInteger: 0, description: "The group id to get" },
  },
  outputType: ArgType.String,
};
class TransformerFunctionMatch extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const patternString = await context.getString("pattern");
    if (patternString == null) {
      return null;
    }
    const matcher = new RegExp(patternString);
    const result = str.match(matcher);
    if (!result) return null; // not found
    const group = await context.getInteger("group");
    return result[group ?? 0];
  }
}

export default TransformerFunctionMatch;
