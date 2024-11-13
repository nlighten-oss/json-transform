import yamljs from "yamljs";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionYamlParse extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) return null;
    return yamljs.parse(str);
  }
}

export default TransformerFunctionYamlParse;
