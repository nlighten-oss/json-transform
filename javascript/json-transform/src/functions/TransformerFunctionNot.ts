import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isTruthy } from "../JsonHelpers";

class TransformerFunctionNot extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        style: { type: ArgType.Enum, position: 0, defaultEnum: "JAVA" },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const jsStyle = (await context.getEnum("style")) === "JS";
    return !isTruthy(await context.getUnwrapped(null), jsStyle);
  }
}

export default TransformerFunctionNot;
