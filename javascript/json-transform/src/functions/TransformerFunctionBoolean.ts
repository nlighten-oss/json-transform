import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";
import {isNullOrUndefined, isTruthy} from "../JsonHelpers";

const DESCRIPTION : FunctionDescription = {
  alias: "boolean",
  description: "",
  inputType: ArgType.Any,
  arguments: {
    style: {
      type: ArgType.Enum, position: 0, defaultEnum: "JAVA",
      enumValues: ["JAVA", "JS"],
      description: "Style of considering truthy values (JS only relates to string handling; not objects and arrays)"
    }
  },
  outputType: ArgType.Boolean
};
class TransformerFunctionBoolean extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const jsStyle = context.getEnum("style") === "JS";
    return isTruthy(context.getUnwrapped(null), jsStyle);
  }
}

export default TransformerFunctionBoolean;