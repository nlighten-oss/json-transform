import yamljs from "yamljs";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionYaml extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    return yamljs.stringify(await context.getUnwrapped(null, true), Infinity, 2);
  }
}

export default TransformerFunctionYaml;
