import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import FormUrlEncodedFormat from "../formats/formurlencoded/FormUrlEncodedFormat";

const DESCRIPTION: FunctionDescription = {
  aliases: ["form"],
  description: "",
  inputType: ArgType.Object,
  outputType: ArgType.String,
};
class TransformerFunctionForm extends TransformerFunction {
  private static readonly FORM_URL_ENCODED_FORMAT = new FormUrlEncodedFormat();

  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getUnwrapped(null);
    return TransformerFunctionForm.FORM_URL_ENCODED_FORMAT.serialize(value);
  }
}

export default TransformerFunctionForm;
