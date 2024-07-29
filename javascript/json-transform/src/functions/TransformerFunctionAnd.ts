import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import {FunctionDescription} from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import {isTruthy} from "../JsonHelpers";

const DESCRIPTION : FunctionDescription = {
  aliases: ["and"],
  inputType: ArgType.Array,
  description: "",
  outputType: ArgType.Boolean
};
class TransformerFunctionAnd extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const arr = context.getJsonElementStreamer(null);
    if (arr == null) {
      return false;
    }
    return arr.stream().all(item => isTruthy(item));
  }
}

export default TransformerFunctionAnd;