import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import { isTruthy } from "../JsonHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["not"],
  inputType: ArgType.Any,
  description: "",
  arguments: {
    style: {
      type: ArgType.Enum,
      position: 0,
      defaultEnum: "JAVA",
      enumValues: ["JAVA", "JS"],
      description: "Style of considering truthy values (JS only relates to string handling; not objects and arrays)",
    },
  },
  outputType: ArgType.Boolean,
};
class TransformerFunctionNot extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const jsStyle = (await context.getEnum("style")) === "JS";
    return !isTruthy(await context.getUnwrapped(null), jsStyle);
  }
}

export default TransformerFunctionNot;
