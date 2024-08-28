import JsonPatch from "fast-json-patch";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";

const DESCRIPTION: FunctionDescription = {
  aliases: ["jsonpatch"],
  inputType: ArgType.Any,
  description: "",
  arguments: {
    ops: { type: ArgType.Array, position: 0, required: true, description: "A list of operations" },
  },
  outputType: ArgType.Any,
};
class TransformerFunctionJsonPatch extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
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
