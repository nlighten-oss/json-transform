import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";
import {isNullOrUndefined} from "../JsonHelpers";

const DESCRIPTION : FunctionDescription = {
  alias: "avg",
  description: "",
  inputType: ArgType.Array,
  arguments: {
    default: {
      type: ArgType.BigDecimal, position: 0, defaultBigDecimal: 0,
      description: "The default value to use for empty values"
    },
    by: {
      type: ArgType.Transformer, position: 1, defaultString: "##current",
      description: "A transformer to extract a property to sum by (using ##current to refer to the current item)"
    }
  },
  outputType: ArgType.BigDecimal
};
class TransformerFunctionAvg extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const value = context.getJsonArray(null);
    if (value == null) {
      return null;
    }
    const by = context.getJsonElement( "by", false);
    const def = context.getBigDecimal("default") ?? 0;
    let size = value.length; // TODO: when streams will be used, needs to count during reduce
    return Math.round(value.reduce((a, c) => {
      let val = !isNullOrUndefined(by) ? context.transformItem(by, c) : c;
        return a + (isNullOrUndefined(val) ? def : val);
    }, 0) / size);
  }
}

export default TransformerFunctionAvg;