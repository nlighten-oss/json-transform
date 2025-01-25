import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined, isTruthy } from "../JsonHelpers";

class TransformerFunctionIf extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        then: { type: ArgType.Any, position: 0, defaultIsNull: true },
        else: { type: ArgType.Any, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    let condition: boolean;
    if (context.has("then")) {
      const value = await context.getJsonElement(null);
      if (isTruthy(value)) {
        return await context.getJsonElement("then", true);
      } else if (context.has("else")) {
        return await context.getJsonElement("else", true);
      }
    } else {
      const arr = await context.getJsonArray(null);
      if (arr == null || arr.length < 2) return null;
      const cje = arr[0];
      if (isNullOrUndefined(cje)) {
        condition = false;
      } else if (typeof cje === "boolean") {
        condition = cje;
      } else {
        condition = isTruthy(await context.transform(context.getPathFor(0), cje));
      }

      if (condition) {
        return await context.transform(context.getPathFor(1), arr[1]);
      } else if (arr.length > 2) {
        return await context.transform(context.getPathFor(2), arr[2]);
      }
    }
    return null; // default falsy value
  }
}

export default TransformerFunctionIf;
