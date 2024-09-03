import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";

const DESCRIPTION: FunctionDescription = {
  aliases: ["matchall"],
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
  outputType: ArgType.ArrayOfString,
};
class TransformerFunctionMatchAll extends TransformerFunction {
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
    const matcher = new RegExp(patternString, "g");

    const allMatches: string[] = [];
    const group = await context.getInteger("group");
    for (const match of str.matchAll(matcher)) {
      allMatches.push(match[group ?? 0]);
    }
    return allMatches.length == 0 ? null : allMatches;
  }
}

export default TransformerFunctionMatchAll;
