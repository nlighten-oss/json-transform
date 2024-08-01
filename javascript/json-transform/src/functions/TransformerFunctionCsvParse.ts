import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";
import {getAsString, isEqual, isNullOrUndefined, isTruthy} from "../JsonHelpers";
import CsvFormat from "../formats/csv/CsvFormat";

const DESCRIPTION : FunctionDescription = {
  aliases: ["csvparse"],
  description: "",
  inputType: ArgType.String,
  arguments: {
    no_headers: {
      type: ArgType.Boolean, position: 0, defaultBoolean: false,
      description: "Whether to treat the first row as object keys"
    },
    separator: {
      type: ArgType.String, position: 1, defaultString: ",",
      description: "Use an alternative field separator"
    },
    names: {
      type: ArgType.Array, position: 2, defaultIsNull: true,
      description: "Names of fields of input arrays (by indices) or objects (can sift if provided less names than there are in the objects provided)"
    }
  },
  outputType: ArgType.Array
};
class TransformerFunctionCsvParse extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const csv = context.getString(null);
    try {
      if (csv == null)
        return null;
      const names = context.getJsonArray("names");
      const noHeaders = context.getBoolean("no_headers");
      const separator = context.getString("separator");
      const namesList = names?.map(el => getAsString(el) ?? "");
      return new CsvFormat(
        namesList,
        noHeaders,
        false, // not relevant for deserialization
        separator)
        .deserialize(csv);
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionCsvParse;