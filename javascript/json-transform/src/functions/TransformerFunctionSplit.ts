import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { createComparator, getAsString, isNullOrUndefined } from "../JsonHelpers";
import CompareBy from "./common/CompareBy";
import JsonElementStreamer from "../JsonElementStreamer";
import javaSplit from "./utils/javaSplit";

class TransformerFunctionSplit extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        delimiter: { type: ArgType.String, position: 0, defaultString: "" },
        limit: { type: ArgType.Integer, position: 1, defaultInteger: 0 },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const delimiter = (await context.getString("delimiter")) ?? "";
    const limit = await context.getInteger("limit");
    return javaSplit(str, delimiter, limit);
  }
}

export default TransformerFunctionSplit;
