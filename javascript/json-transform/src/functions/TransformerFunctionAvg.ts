import BigNumber from "bignumber.js";
import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";
import {isNullOrUndefined} from "../JsonHelpers";
import {BigDecimal} from "./common/FunctionHelpers";

const DESCRIPTION : FunctionDescription = {
  aliases: ["avg"],
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
    const value = context.getJsonElementStreamer(null);
    if (value == null || value.knownAsEmpty()) {
      return null;
    }
    const by = context.getJsonElement( "by", false);
    const _default = context.getBigDecimal("default") ?? BigDecimal(0);
    let size = 0;
    const result = value.stream()
      .map(t => {
        size++;
        let res = !isNullOrUndefined(by) ? context.transformItem(by, t) : t;
        return isNullOrUndefined(res) ? _default : BigDecimal(res);
      })
      .reduce((a: BigNumber, c) => a.plus(c))
      .dividedBy(size);
    return result;
  }
}

export default TransformerFunctionAvg;