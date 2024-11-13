import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import FormUrlEncodedFormat from "../formats/formurlencoded/FormUrlEncodedFormat";

class TransformerFunctionUriParse extends TransformerFunction {
  private static readonly FORM_URL_ENCODED_FORMAT = new FormUrlEncodedFormat();

  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    var str = await context.getString(null);
    if (str == null) {
      return null;
    }
    try {
      const result: any = {};
      const uri = new URL(str);
      result.scheme = uri.protocol.slice(0, -1);
      if (uri.username || uri.password) {
        result.user_info = `${uri.username}:${uri.password}`;
      }
      result.host = uri.host;
      result.hostname = uri.hostname;
      result.authority = `${result.user_info ? result.user_info + "@" : ""}${result.host}`;
      if (uri.port !== "") {
        result.port = +uri.port;
      }
      result.path = uri.pathname;
      if (uri.search) {
        result.query = TransformerFunctionUriParse.FORM_URL_ENCODED_FORMAT.deserialize(uri.search);
        result.query_raw = uri.search.substring(1);
      }
      if (uri.hash) {
        result.fragment = uri.hash.substring(1);
      }
      return result;
    } catch (e: any) {
      console.warn("Failed parsing uri", e);
      return null;
    }
  }
}

export default TransformerFunctionUriParse;
