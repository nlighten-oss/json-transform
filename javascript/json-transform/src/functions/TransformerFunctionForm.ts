import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import FormUrlEncodedFormat from "../formats/formurlencoded/FormUrlEncodedFormat";

class TransformerFunctionForm extends TransformerFunction {
  private static readonly FORM_URL_ENCODED_FORMAT = new FormUrlEncodedFormat();

  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getUnwrapped(null);
    return TransformerFunctionForm.FORM_URL_ENCODED_FORMAT.serialize(value);
  }
}

export default TransformerFunctionForm;
