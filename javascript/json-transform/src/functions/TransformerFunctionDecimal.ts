import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { MAX_SCALE, MAX_SCALE_ROUNDING, NO_SCALE, RoundingModes } from "./common/FunctionHelpers";

class TransformerFunctionDecimal extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        scale: { type: ArgType.Integer, position: 0, defaultInteger: -1 },
        rounding: { type: ArgType.Enum, position: 1, defaultEnum: "HALF_UP" },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    let result = await context.getBigDecimal(null);
    if (result == null) return null;
    let scale = await context.getInteger("scale");
    const roundingMode = await context.getEnum("rounding");
    if (scale == NO_SCALE && (result.decimalPlaces() ?? 0) > MAX_SCALE) {
      scale = MAX_SCALE;
    }
    if (typeof scale === "number" && scale > NO_SCALE) {
      const rounding = RoundingModes[roundingMode ?? ""] ?? MAX_SCALE_ROUNDING;
      result = result.decimalPlaces(scale, rounding);
    }
    return result;
  }
}

export default TransformerFunctionDecimal;
