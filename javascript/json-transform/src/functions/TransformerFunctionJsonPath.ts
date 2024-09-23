import jp from "jsonpath";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import DocumentContext from "../DocumentContext";

class TransformerFunctionJsonPath extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        path: { type: ArgType.String, position: 0 },
        options: { type: ArgType.ArrayOfString, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const source = await context.getJsonElement(null);
    if (source == null) {
      return null;
    }
    const path = await context.getString("path");
    if (path == null) {
      return null;
    }

    const optionsArray = await context.getJsonArray("options");
    if (optionsArray != null && optionsArray.length > 0) {
      const options = optionsArray.reduce(
        (acc, val) => {
          acc[val] = true;
          return acc;
        },
        {} as Record<string, true>,
      );

      if (options.ALWAYS_RETURN_LIST) {
        try {
          return jp.query(source, path);
        } catch (e: any) {
          if (options.SUPPRESS_EXCEPTIONS) {
            return [];
          }
          throw e;
        }
      }
    }

    return new DocumentContext(source).read(path);
  }
}

export default TransformerFunctionJsonPath;
