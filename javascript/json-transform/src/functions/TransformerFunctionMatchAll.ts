import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionMatchAll extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        pattern: { type: ArgType.String, position: 0 },
        group: { type: ArgType.Integer, position: 1, defaultInteger: 0 },
      },
    });
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
