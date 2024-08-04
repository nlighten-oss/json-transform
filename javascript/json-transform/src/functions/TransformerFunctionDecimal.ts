import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { MAX_SCALE, MAX_SCALE_ROUNDING, NO_SCALE, RoundingModes } from "./common/FunctionHelpers";
import BigNumber from "bignumber.js";

const DESCRIPTION: FunctionDescription = {
  aliases: ["decimal"],
  description: "",
  inputType: ArgType.Any,
  arguments: {
    scale: {
      type: ArgType.Integer,
      position: 0,
      defaultInteger: -1,
      description: "Scale of BigDecimal to set (default is 10 max)",
    },
    rounding: {
      type: ArgType.Enum,
      position: 1,
      defaultEnum: "HALF_UP",
      enumValues: ["UP", "DOWN", "CEILING", "FLOOR", "HALF_UP", "HALF_DOWN", "HALF_EVEN"],
      description: "Java's `RoundingMode` (default is HALF_UP)",
    },
  },
  outputType: ArgType.BigDecimal,
};
class TransformerFunctionDecimal extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
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
      const rounding = (RoundingModes[roundingMode ?? ""] ?? MAX_SCALE_ROUNDING) as BigNumber.RoundingMode;
      result = result.decimalPlaces(scale, rounding);
    }
    return result;
  }
}

export default TransformerFunctionDecimal;
