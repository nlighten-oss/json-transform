import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionUpper extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const result = await context.getString(null);
    return result == null ? null : result.toUpperCase();
  }
}

export default TransformerFunctionUpper;
