import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { JSONBig } from "./common/FunctionHelpers";

class TransformerFunctionJsonParse extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    return JSONBig.parse(str);
  }
}

export default TransformerFunctionJsonParse;
