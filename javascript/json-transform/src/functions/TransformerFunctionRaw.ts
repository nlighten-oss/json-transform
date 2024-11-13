import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionRaw extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    return context.getJsonElement(null, false);
  }
}

export default TransformerFunctionRaw;
