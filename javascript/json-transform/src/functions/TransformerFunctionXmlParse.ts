import xml2js from "xml2js";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionXmlParse extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        keep_strings: { type: ArgType.Boolean, position: 0, defaultBoolean: false },
        cdata_tag_name: { type: ArgType.String, position: 1, defaultString: "$content" },
        convert_nil_to_null: { type: ArgType.Boolean, position: 2, defaultBoolean: false },
        force_list: { type: ArgType.ArrayOfString, position: 3, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const xml = await context.getString(null);
    if (xml == null) return null;
    try {
      const keepStrings = (await context.getBoolean("keep_strings")) ?? undefined;
      const cDataTagName = (await context.getString("cdata_tag_name")) ?? undefined;
      const convertNilAttributeToNull = (await context.getBoolean("convert_nil_to_null")) ?? undefined;
      const forceList = (await context.getJsonArray("force_list")) ?? undefined;

      const parser = new xml2js.Parser({
        charkey: cDataTagName,
        ignoreAttrs: convertNilAttributeToNull,
        explicitArray: Boolean(forceList?.length),
        attrValueProcessors: keepStrings
          ? undefined
          : [xml2js.processors.parseNumbers, xml2js.processors.parseBooleans],
      });
      return parser.parseStringPromise(xml);
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionXmlParse;
