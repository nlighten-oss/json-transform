import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { isTruthy } from "../JsonHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["or"],
  description: "",
  inputType: ArgType.Array,
  outputType: ArgType.Boolean,
};
class TransformerFunctionOr extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    return value?.stream().any(isTruthy);
  }
}

export default TransformerFunctionOr;
