import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";
import { isTruthy } from "../JsonHelpers";

class TransformerFunctionValue extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    let result = await context.get(null, true);
    if (result instanceof JsonElementStreamer) {
      result = await result.toJsonArray();
    }
    if (isTruthy(result)) {
      return result;
    }
    return null;
  }
}

export default TransformerFunctionValue;
