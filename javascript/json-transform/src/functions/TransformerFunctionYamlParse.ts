import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import YamlFormat from "../formats/yaml/YamlFormat";

class TransformerFunctionYamlParse extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) return null;
    return YamlFormat.INSTANCE.deserialize(str);
  }
}

export default TransformerFunctionYamlParse;
