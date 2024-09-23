import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { getAsString } from "../JsonHelpers";
import CsvFormat from "../formats/csv/CsvFormat";

class TransformerFunctionCsvParse extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        no_headers: { type: ArgType.Boolean, position: 0, defaultBoolean: false },
        separator: { type: ArgType.String, position: 1, defaultString: "," },
        names: { type: ArgType.Array, position: 2, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const csv = await context.getString(null);
    try {
      if (csv == null) return null;
      const names = await context.getJsonArray("names");
      const noHeaders = await context.getBoolean("no_headers");
      const separator = await context.getString("separator");
      const namesList = names?.map(el => getAsString(el) ?? "");
      return new CsvFormat(
        namesList,
        noHeaders,
        false, // not relevant for deserialization
        separator,
      ).deserialize(csv);
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionCsvParse;
