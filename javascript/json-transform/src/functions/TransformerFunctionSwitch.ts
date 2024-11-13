import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { getAsString, isMap } from "../JsonHelpers";
import { ArgType } from "./common/ArgType";
import { JSONBig } from "./common/FunctionHelpers";

class TransformerFunctionSwitch extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        cases: { type: ArgType.Object, position: 0, defaultIsNull: true },
        default: { type: ArgType.Any, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const alias = context.getAlias();
    const value = await context.getString(null);
    const caseMap = await context.getJsonElement("cases");
    if (!isMap(caseMap)) {
      console.warn("{}.cases was not specified with an object as case map", alias);
      return null;
    }
    return Object.prototype.hasOwnProperty.call(caseMap, value ?? "null")
      ? caseMap[value ?? "null"]
      : context.getJsonElement("default");
  }
}

export default TransformerFunctionSwitch;
