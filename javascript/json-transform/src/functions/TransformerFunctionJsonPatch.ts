import JsonPatch from "fast-json-patch";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionJsonPatch extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        ops: { type: ArgType.Array, position: 0 },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const source = await context.getJsonElement(null);
    if (source == null) {
      return null;
    }
    const ops = (await context.getJsonArray("ops")) ?? [];
    return JsonPatch.applyPatch(source, ops, true).newDocument;
  }
}

export default TransformerFunctionJsonPatch;
