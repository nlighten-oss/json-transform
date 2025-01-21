import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isMap } from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";

class TransformerFunctionLength extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        type: { type: ArgType.Enum, position: 0, defaultEnum: "AUTO" },
        default_zero: { type: ArgType.Boolean, position: 1, defaultBoolean: false },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const type = await context.getEnum("type");
    const defaultZero = await context.getBoolean("default_zero");
    switch (type) {
      case "STRING": {
        const je = await context.getJsonElement(null);
        if (typeof je === "string") {
          return je.length;
        }
        break;
      }
      case "ARRAY": {
        const obj = await context.get(null);
        if (obj instanceof JsonElementStreamer) {
          return obj.stream().count();
        }
        if (Array.isArray(obj)) {
          return obj.length;
        }
        break;
      }
      case "OBJECT": {
        const obj = await context.getJsonElement(null);
        if (isMap(obj)) {
          return Object.keys(obj).length;
        }
        break;
      }
      default: {
        // AUTO (or null)
        const obj = await context.get(null);
        if (obj instanceof JsonElementStreamer) {
          return obj.stream().count();
        }
        if (Array.isArray(obj)) {
          return obj.length;
        }
        if (isMap(obj)) {
          return Object.keys(obj).length;
        }
        if (typeof obj === "string") {
          return obj.length;
        }
      }
    }
    return defaultZero ? 0 : null;
  }
}

export default TransformerFunctionLength;
