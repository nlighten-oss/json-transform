import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["coalesce", "first"],
  inputType: ArgType.Array,
  description: "",
  outputType: ArgType.Any,
};
class TransformerFunctionCoalesce extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null || streamer.knownAsEmpty()) return null;
    return streamer
      .stream()
      .filter(itm => !isNullOrUndefined(itm))
      .firstOrNull();
  }
}

export default TransformerFunctionCoalesce;
