import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";
import { asAsyncSequence } from "@wortise/sequency";

const DESCRIPTION: FunctionDescription = {
  aliases: ["entries"],
  inputType: ArgType.Any,
  description: "",
  outputType: ArgType.Array,
};
class TransformerFunctionEntries extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const input = await context.getJsonElement(null);
    if (Array.isArray(input)) {
      return JsonElementStreamer.fromTransformedStream(
        context,
        asAsyncSequence(Object.entries(input)).map(a => {
          return [+a[0], a[1]];
        }),
      );
    }
    if (input && typeof input === "object") {
      return JsonElementStreamer.fromJsonArray(context, Object.entries(input), true);
    }
    return null;
  }
}

export default TransformerFunctionEntries;
