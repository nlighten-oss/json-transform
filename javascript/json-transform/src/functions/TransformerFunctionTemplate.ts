import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";
import DocumentContext from "../DocumentContext";
import TextTemplate from "../template/TextTemplate";
import ParameterDefaultResolveOptions from "../template/ParameterDefaultResolveOptions";

const DOLLAR = "$";
class TransformerFunctionTemplate extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        payload: { type: ArgType.Any, position: 0, defaultIsNull: true },
        default_resolve: { type: ArgType.Enum, position: 1, defaultEnum: "UNIQUE" },
        url_encode: { type: ArgType.Boolean, position: 2, defaultBoolean: false },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const input = await context.getString(null);
    if (input == null) {
      return null;
    }

    const defaultResolver = (await context.getEnum("default_resolve")) as ParameterDefaultResolveOptions;

    const urlEncode = await context.getBoolean("url_encode");

    let currentResolver = context.getResolver();
    let resolver = currentResolver;
    const payload = await context.getJsonElement("payload");
    if (payload !== null) {
      const dc = new DocumentContext(payload);
      resolver = {
        get: name => {
          if (FunctionContext.pathOfVar("##current", name)) {
            return dc.read(DOLLAR + name.substring(9));
          }
          return currentResolver.get(name);
        },
      };
    }

    const tt = TextTemplate.get(input, defaultResolver);
    return tt.render(resolver, urlEncode);
  }
}

export default TransformerFunctionTemplate;
