import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";

const DESCRIPTION: FunctionDescription = {
  aliases: ["lower"],
  description: "",
  inputType: ArgType.String,
  outputType: ArgType.String,
};
class TransformerFunctionLower extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const result = await context.getString(null);
    return result == null ? null : result.toLowerCase();
  }
}

export default TransformerFunctionLower;
