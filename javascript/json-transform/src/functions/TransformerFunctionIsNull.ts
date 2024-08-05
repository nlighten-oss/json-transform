import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { isNullOrUndefined } from "../JsonHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["isnull"],
  description: "",
  inputType: ArgType.Any,
  outputType: ArgType.Boolean,
};
class TransformerFunctionIsNull extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElement(null);
    return isNullOrUndefined(value);
  }
}

export default TransformerFunctionIsNull;
