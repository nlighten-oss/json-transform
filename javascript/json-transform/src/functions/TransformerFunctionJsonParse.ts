import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";

const DESCRIPTION: FunctionDescription = {
  aliases: ["jsonparse"],
  inputType: ArgType.String,
  description: "",
  outputType: ArgType.Any,
};
class TransformerFunctionJsonParse extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    return JSON.parse(str);
  }
}

export default TransformerFunctionJsonParse;
