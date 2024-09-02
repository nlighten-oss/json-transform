import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";

const DESCRIPTION: FunctionDescription = {
  aliases: ["long"],
  description: "",
  inputType: ArgType.Any,
  outputType: ArgType.Long,
};
class TransformerFunctionLong extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    return context.getLong(null);
  }
}

export default TransformerFunctionLong;
