import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { isTruthy } from "../JsonHelpers";

class TransformerFunctionXor extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const arr = await context.getJsonElementStreamer(null);
    if (arr == null) {
      return false;
    }
    return (
      (await arr
        .stream()
        .filter(item => isTruthy(item))
        .count()) === 1
    );
  }
}

export default TransformerFunctionXor;
