import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";
import {getAsString, isEqual, isNullOrUndefined, isTruthy} from "../JsonHelpers";
import CsvFormat from "../formats/csv/CsvFormat";

const DESCRIPTION : FunctionDescription = {
  aliases: ["csv"],
  description: "",
  inputType: ArgType.Array,
  arguments: {
    no_headers: {
      type: ArgType.Boolean, position: 0, defaultBoolean: false,
      description: "Whether to include object keys as headers (taken from first object if no `names`)"
    },
    force_quote: {
      type: ArgType.Boolean, position: 1, defaultBoolean: false,
      description: "Whether to quote all the values"
    },
    separator: {
      type: ArgType.String, position: 2, defaultString: ",",
      description: "Use an alternative field separator"
    },
    names: {
      type: ArgType.Array, position: 3, defaultIsNull: true,
      description: "Names of fields to extract into csv if objects (will be used as the header row, unless `no_headers`)"
    }
  },
  outputType: ArgType.String
};
class TransformerFunctionCsv extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const streamer = context.getJsonElementStreamer(null);
    try {
      if (streamer == null)
        return null;
      const names = context.getJsonArray("names");
      const noHeaders = context.getBoolean("no_headers");
      const forceQuote = context.getBoolean("force_quote");
      const separator = context.getString("separator");
      const namesList = names?.map(el => getAsString(el) ?? "");
      return new CsvFormat(
        namesList,
        noHeaders,
        forceQuote,
        separator)
        .serialize(streamer.toJsonArray());
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionCsv;