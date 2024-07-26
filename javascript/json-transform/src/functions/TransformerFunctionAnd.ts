import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import {FunctionDescription} from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import {isTruthy} from "../JsonHelpers";

const DESCRIPTION : FunctionDescription = {
  alias: "and",
  inputType: ArgType.Array,
  description: "",
  outputType: ArgType.Boolean
};
class TransformerFunctionAnd extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const arr = context.getJsonArray(null);
    if (arr == null) {
      return null;
    }
    return arr.every(item => isTruthy(item));
  }
}

export default TransformerFunctionAnd;