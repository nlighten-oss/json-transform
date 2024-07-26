import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import TextEncoding from "./common/TextEncoding";
import Base64 from "./utils/Base64";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";

const DESCRIPTION : FunctionDescription = {
  alias: "at",
  description: "",
  inputType: ArgType.Array,
  arguments: {
    index: {
      type: ArgType.Integer, position: 0, required: true, defaultIsNull: true,
      description: "Index of element to fetch (negative values will be fetch from the end)"
    }
  },
};
class TransformerFunctionAt extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const value = context.getJsonArray(null);
    if (value == null) {
      return null;
    }
    const index = context.getInteger("index");
    if (index == null) {
      return null;
    }
    return value.at(index);
  }
}

export default TransformerFunctionAt;