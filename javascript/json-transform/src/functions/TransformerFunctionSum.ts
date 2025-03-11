import BigNumber from "bignumber.js";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";
import { BigDecimal, MAX_SCALE, MAX_SCALE_ROUNDING } from "./common/FunctionHelpers";

class TransformerFunctionSum extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        default: { type: ArgType.BigDecimal, position: 0, defaultBigDecimal: 0 },
        by: { type: ArgType.Transformer, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    if (value == null || value.knownAsEmpty()) {
      return null;
    }
    const hasBy = context.has("by");
    const by = await context.getJsonElement("by", false);
    const _default = (await context.getBigDecimal("default")) ?? BigDecimal(0);
    let size = 0;
    let sum = await value
      .stream()
      .map(async t => {
        size++;
        const res = !hasBy ? t : await context.transformItem(by, t);
        return isNullOrUndefined(res) ? _default : BigDecimal(res);
      })
      .reduce((a: BigNumber, c) => a.plus(c));
    if ((sum.decimalPlaces() ?? 0) > MAX_SCALE) {
      sum = sum.decimalPlaces(MAX_SCALE, MAX_SCALE_ROUNDING);
    }
    return sum;
  }
}

export default TransformerFunctionSum;
