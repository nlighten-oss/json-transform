import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import { getAsString, isNullOrUndefined } from "../JsonHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["join"],
  inputType: ArgType.Array,
  description: "",
  arguments: {
    delimiter: {
      type: ArgType.String,
      position: 0,
      defaultString: "",
      aliases: ["$$delimiter "],
      description: "Delimiter to join any 2 adjacent elements",
    },
    prefix: { type: ArgType.String, position: 1, defaultString: "", description: "A string to prefix the result" },
    suffix: { type: ArgType.String, position: 2, defaultString: "", description: "A string to suffix the result" },
    keep_nulls: {
      type: ArgType.Boolean,
      position: 3,
      defaultBoolean: false,
      description: "Whether to keep null values when joining",
    },
  },
  outputType: ArgType.String,
};
class TransformerFunctionJoin extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    let delimiter = await context.getString("$$delimiter"); // backwards compat.
    if (delimiter == null) {
      delimiter = await context.getString("delimiter");
    }
    const prefix = (await context.getString("prefix")) ?? undefined;
    const suffix = (await context.getString("suffix")) ?? undefined;
    let stream = streamer.stream().map(getAsString);
    if (!(await context.getBoolean("keep_nulls"))) {
      stream = stream.filter(el => !isNullOrUndefined(el));
    }
    return stream.joinToString({
      prefix: prefix,
      postfix: suffix,
      separator: delimiter ?? "",
    });
  }
}

export default TransformerFunctionJoin;
