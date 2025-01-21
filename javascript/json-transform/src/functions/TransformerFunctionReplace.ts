import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionReplace extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        find: { type: ArgType.String, position: 0, defaultString: "" },
        replacement: { type: ArgType.String, position: 1, defaultString: "" },
        type: { type: ArgType.Enum, position: 2, defaultEnum: "STRING" },
        from: { type: ArgType.Integer, position: 3, defaultInteger: 0 },
      },
    });
  }

  replaceAll(str: string, find: string, replacement: string) {
    let i = -1;

    if (!str) return str;
    if (!find) return str;

    while ((i = str.indexOf(find, i >= 0 ? i + replacement.length : 0)) !== -1) {
      str = str.substring(0, i) + replacement + str.substring(i + find.length);
    }
    return str;
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const find = await context.getString("find");
    if (find == null) {
      return str;
    }
    const replacement = (await context.getString("replacement")) ?? "";
    const from = (await context.getInteger("from")) ?? 0;
    const validFrom = from > 0 && str.length > from;
    switch ((await context.getEnum("type")) ?? "STRING") {
      case "FIRST":
        return validFrom
          ? str.substring(0, from) + str.substring(from).replace(find, replacement)
          : str.replace(find, replacement);
      case "REGEX": {
        const findRe = new RegExp(find, "g");
        return validFrom
          ? str.substring(0, from) + str.substring(from).replace(findRe, replacement)
          : str.replace(findRe, replacement);
      }
      case "REGEX-FIRST": {
        const findRe = new RegExp(find);
        return validFrom
          ? str.substring(0, from) + str.substring(from).replace(findRe, replacement)
          : str.replace(findRe, replacement);
      }
      default: {
        return validFrom
          ? str.substring(0, from) + this.replaceAll(str.substring(from), find, replacement)
          : this.replaceAll(str, find, replacement);
      }
    }
  }
}

export default TransformerFunctionReplace;
