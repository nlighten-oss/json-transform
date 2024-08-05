import BigNumber from "bignumber.js";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { isNullOrUndefined } from "../JsonHelpers";
import { BigDecimal } from "./common/FunctionHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["avg"],
  description: "",
  inputType: ArgType.Array,
  arguments: {
    default: {
      type: ArgType.BigDecimal,
      position: 0,
      defaultBigDecimal: 0,
      description: "The default value to use for empty values",
    },
    by: {
      type: ArgType.Transformer,
      position: 1,
      defaultString: "##current",
      description: "A transformer to extract a property to sum by (using ##current to refer to the current item)",
    },
  },
  outputType: ArgType.BigDecimal,
};
class TransformerFunctionAvg extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    if (value == null || value.knownAsEmpty()) {
      return null;
    }
    const by = await context.getJsonElement("by", false);
    const _default = (await context.getBigDecimal("default")) ?? BigDecimal(0);
    let size = 0;
    const sum = await value
      .stream()
      .map(async t => {
        size++;
        const res = isNullOrUndefined(by) ? t : await context.transformItem(by, t);
        return isNullOrUndefined(res) ? _default : BigDecimal(res);
      })
      .reduce((a: BigNumber, c) => a.plus(c));
    return sum.dividedBy(size);
  }
}

export default TransformerFunctionAvg;
