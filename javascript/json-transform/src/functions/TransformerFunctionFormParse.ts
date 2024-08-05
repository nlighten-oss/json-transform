import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import FormUrlEncodedFormat from "../formats/formurlencoded/FormUrlEncodedFormat";

const DESCRIPTION: FunctionDescription = {
  aliases: ["formparse"],
  description: "",
  inputType: ArgType.String,
  outputType: ArgType.Object,
};
class TransformerFunctionFormParse extends TransformerFunction {
  private static readonly FORM_URL_ENCODED_FORMAT = new FormUrlEncodedFormat();

  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getString(null);
    if (value === null) return null;
    return TransformerFunctionFormParse.FORM_URL_ENCODED_FORMAT.deserialize(value);
  }
}

export default TransformerFunctionFormParse;
