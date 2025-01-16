import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isMap, isNullOrUndefined } from "../JsonHelpers";

class TransformerFunctionFlatten extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        target: { type: ArgType.Object, position: 0, defaultIsNull: true },
        prefix: { type: ArgType.String, position: 1, defaultIsNull: true },
        array_prefix: { type: ArgType.String, position: 2, defaultString: "$" },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const jeTarget = await context.getJsonElement("target");
    let target;
    if (isMap(jeTarget)) {
      target = jeTarget;
    } else {
      target = {};
    }

    return flatten(
      await context.getJsonElement(null, true),
      target,
      (await context.getString("prefix")) ?? undefined,
      (await context.getString("array_prefix")) ?? undefined,
    );
  }
}

function flatten(source: any, target: Record<string, any>, prefix?: string, arrayPrefix?: string) {
  if (isNullOrUndefined(source)) {
    return target;
  }
  const noPrefix = isNullOrUndefined(prefix);
  if (isMap(source)) {
    Object.entries(source).forEach(([key, val]) =>
      flatten(val, target, noPrefix ? key : prefix + "." + key, arrayPrefix),
    );
  } else if (Array.isArray(source)) {
    if (!isNullOrUndefined(arrayPrefix)) {
      const size = source.length;
      for (let i = 0; i < size; i++) {
        flatten(source[i], target, (noPrefix ? "" : prefix + ".") + arrayPrefix + i, arrayPrefix);
      }
    } else if (!noPrefix) {
      target[prefix] = source;
    }
  } else {
    if (noPrefix || prefix === "") {
      return source;
    }
    target[prefix] = source;
  }
  return target;
}

export default TransformerFunctionFlatten;
