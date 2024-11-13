import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { getAsString } from "../JsonHelpers";
import { ArgType } from "./common/ArgType";
import { JSONBig } from "./common/FunctionHelpers";

class TransformerFunctionString extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        json: { type: ArgType.Boolean, position: 0, defaultBoolean: false },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getUnwrapped(null);
    if (await context.getBoolean("json")) {
      return JSONBig.stringify(value);
    }
    return getAsString(value);
  }
}

export default TransformerFunctionString;
