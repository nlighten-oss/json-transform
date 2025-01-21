import xml2js from "xml2js";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

const DUMMY_ROOT = "R__O__O__T";
class TransformerFunctionXml extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        root: { type: ArgType.String, position: 0, defaultIsNull: true },
        xslt: { type: ArgType.String, position: 1, defaultIsNull: true },
        indent: { type: ArgType.Boolean, position: 2, defaultBoolean: false },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const obj = await context.getJsonElement(null);
    if (obj == null) return null;
    try {
      const rootName = (await context.getString("root")) ?? DUMMY_ROOT;
      const indent = (await context.getBoolean("indent")) ?? undefined;
      const builder = new xml2js.Builder({
        headless: !indent,
        rootName,
        charkey: "$content",
        renderOpts: { pretty: indent },
        xmldec: {
          version: "1.0",
          encoding: "UTF-8",
          standalone: undefined,
        },
      });
      let xml = builder.buildObject(obj);
      if (rootName === DUMMY_ROOT) {
        xml = xml.slice(DUMMY_ROOT.length + 2, -DUMMY_ROOT.length - 3);
      }
      if (indent) {
        // remove new line after xmldec
        xml = xml.replace("?>\n", "?>") + "\n";
      }
      const xslt = await context.getString("xslt");
      if (xslt != null && xslt !== "") {
        throw new Error("XSLT not supported"); // TODO: add XSLT support
      }
      return xml;
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionXml;
