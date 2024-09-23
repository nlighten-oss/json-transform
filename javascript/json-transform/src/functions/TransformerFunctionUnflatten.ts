import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";
import { isMap, mergeInto } from "../JsonHelpers";

class TransformerFunctionUnflatten extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        target: { type: ArgType.Object, position: 0, defaultIsNull: true },
        path: { type: ArgType.String, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    let target: Record<string, any>;
    var targetValue = await context.getJsonElement("target");
    if (isMap(targetValue)) {
      target = targetValue;
    } else {
      target = {};
    }

    const source = await context.getJsonElement(null, true);
    const path = await context.getString("path", true);
    if (isMap(source)) {
      Object.entries(source).forEach(([key, value]) =>
        mergeInto(target, value, (path != null ? path + "." : "") + key),
      );
    }

    return target;
  }
}

export default TransformerFunctionUnflatten;
