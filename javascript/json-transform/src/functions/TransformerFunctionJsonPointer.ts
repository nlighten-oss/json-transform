import jsonPointer from "json-pointer";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionJsonPointer extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        op: { type: ArgType.Enum, position: 0, defaultString: "GET" },
        pointer: { type: ArgType.String, position: 1, defaultIsNull: true },
        value: { type: ArgType.Any, position: 2, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const source = await context.getJsonElement(null);
    if (source == null) {
      return null;
    }
    const pointer = await context.getString("pointer");
    if (pointer == null) {
      return null;
    }
    const op = await context.getEnum("op");

    switch (op) {
      case "GET":
        return jsonPointer.get(source, pointer);
      case "SET":
        jsonPointer.set(source, pointer, await context.getJsonElement("value"));
        return source;
      case "REMOVE":
        jsonPointer.remove(source, pointer);
        return source;
    }
    return null;
  }
}

export default TransformerFunctionJsonPointer;
