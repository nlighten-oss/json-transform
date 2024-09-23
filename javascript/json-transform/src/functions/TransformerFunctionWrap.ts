import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionWrap extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        prefix: { type: ArgType.String, position: 0, defaultString: "" },
        suffix: { type: ArgType.String, position: 1, defaultString: "" },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const res = await context.getString(null);
    if (res == null) return null;
    return (await context.getString("prefix")) + res + (await context.getString("suffix"));
  }
}

export default TransformerFunctionWrap;
