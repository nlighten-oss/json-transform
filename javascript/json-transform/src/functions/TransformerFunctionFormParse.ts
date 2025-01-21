import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import FormUrlEncodedFormat from "../formats/formurlencoded/FormUrlEncodedFormat";

class TransformerFunctionFormParse extends TransformerFunction {
  private static readonly FORM_URL_ENCODED_FORMAT = new FormUrlEncodedFormat();

  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getString(null);
    if (value === null) return null;
    return TransformerFunctionFormParse.FORM_URL_ENCODED_FORMAT.deserialize(value);
  }
}

export default TransformerFunctionFormParse;
