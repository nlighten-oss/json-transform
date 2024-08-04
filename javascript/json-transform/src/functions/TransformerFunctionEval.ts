import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";

const DESCRIPTION: FunctionDescription = {
  aliases: ["eval"],
  description: "",
  inputType: ArgType.Any,
  outputType: ArgType.Any,
};
class TransformerFunctionEval extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const _eval = await context.getJsonElement(null);
    return await context.transform(_eval, true);
  }
}

export default TransformerFunctionEval;
