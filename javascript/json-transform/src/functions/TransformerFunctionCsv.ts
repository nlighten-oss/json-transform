import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { getAsString } from "../JsonHelpers";
import CsvFormat from "../formats/csv/CsvFormat";

class TransformerFunctionCsv extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        no_headers: { type: ArgType.Boolean, position: 0, defaultBoolean: false },
        force_quote: { type: ArgType.Boolean, position: 1, defaultBoolean: false },
        separator: { type: ArgType.String, position: 2, defaultString: "," },
        names: { type: ArgType.Array, position: 3, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    try {
      if (streamer == null) return null;
      const names = await context.getJsonArray("names");
      const noHeaders = await context.getBoolean("no_headers");
      const forceQuote = await context.getBoolean("force_quote");
      const separator = await context.getString("separator");
      const namesList = names?.map(el => getAsString(el) ?? "");
      return new CsvFormat(namesList, noHeaders, forceQuote, separator).serialize(await streamer.toJsonArray());
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionCsv;
