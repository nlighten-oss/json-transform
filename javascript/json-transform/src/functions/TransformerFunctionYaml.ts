import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import YamlFormat from "../formats/yaml/YamlFormat";

class TransformerFunctionYaml extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    return YamlFormat.INSTANCE.serialize(await context.getUnwrapped(null, true));
  }
}

export default TransformerFunctionYaml;
