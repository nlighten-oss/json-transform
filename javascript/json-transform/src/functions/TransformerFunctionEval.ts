import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionEval extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const _eval = await context.getJsonElement(null);
    return await context.transform(context.getPath() + "/.", _eval, true);
  }
}

export default TransformerFunctionEval;
