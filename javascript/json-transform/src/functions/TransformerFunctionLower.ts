import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";

const DESCRIPTION : FunctionDescription = {
  alias: "lower",
  description: "",
  inputType: ArgType.String,
  outputType: ArgType.String
};
class TransformerFunctionLower extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const result = context.getString(null);
    return result == null ? null : result.toLowerCase();
  }
}

export default TransformerFunctionLower;