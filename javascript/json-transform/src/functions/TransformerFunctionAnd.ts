import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { isTruthy } from "../JsonHelpers";

class TransformerFunctionAnd extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const arr = await context.getJsonElementStreamer(null);
    if (arr == null) {
      return false;
    }
    return arr.stream().all(item => isTruthy(item));
  }
}

export default TransformerFunctionAnd;
