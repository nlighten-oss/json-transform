import { AsyncSequence, asAsyncSequence } from "@wortise/sequency";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import JsonElementStreamer from "../JsonElementStreamer";

const DESCRIPTION: FunctionDescription = {
  aliases: ["map"],
  description: "",
  inputType: ArgType.Array,
  arguments: {
    to: {
      type: ArgType.Transformer,
      position: 0,
      defaultIsNull: true,
      description: "Transformer to map each element to its value in the result array (inputs: ##current, ##index)",
    },
  },
  outputType: ArgType.Array,
};
class TransformerFunctionMap extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    let inputStream: AsyncSequence<any>;
    let to: any;
    if (context.has("to")) {
      const streamer = await context.getJsonElementStreamer(null);
      if (streamer == null) return null;
      inputStream = streamer.stream();
      to = await context.getJsonElement("to", false); // we don't transform definitions to prevent premature evaluation
    } else {
      // [ input, to ]
      const arr = await context.getJsonArray(null, false); // we don't transform definitions to prevent premature evaluation
      if (arr == null) return null;
      const inputEl = await context.transform(arr[0]);
      if (!Array.isArray(inputEl)) {
        console.warn(`${context.getAlias()} was not specified with an array of items`);
        return null;
      }
      inputStream = asAsyncSequence(inputEl);
      to = arr[1];
    }
    let i = 0;
    return JsonElementStreamer.fromTransformedStream(
      context,
      inputStream.map(x => context.transformItem(to, x, i++)),
    );
  }
}

export default TransformerFunctionMap;
