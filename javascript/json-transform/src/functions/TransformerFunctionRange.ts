import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";
import { createAsyncSequence } from "@wortise/sequency";
import { RoundingModes } from "./common/FunctionHelpers";
import BigNumber from "bignumber.js";

class TransformerFunctionRange extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        start: { type: ArgType.BigDecimal, position: 0, defaultIsNull: true },
        end: { type: ArgType.BigDecimal, position: 1, defaultIsNull: true },
        step: { type: ArgType.BigDecimal, position: 2, defaultBigDecimal: 1 },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    let start: BigNumber | null;
    let end: BigNumber | null;
    let step: BigNumber;
    if (context.has("start")) {
      start = await context.getBigDecimal("start");
      end = await context.getBigDecimal("end");
      step = (await context.getBigDecimal("step")) ?? BigNumber(1);
    } else {
      const arr = await context.getJsonArray(null);
      if (!arr) {
        return null;
      }
      start = arr[0];
      end = arr[1];
      step = arr[2] ?? BigNumber(1);
    }
    // sanity check
    if (
      start === null ||
      end === null ||
      (end.lt(start) && step.isPositive()) ||
      (end.gt(start) && step.isNegative())
    ) {
      return null;
    }
    const size = end.minus(start).dividedToIntegerBy(step).plus(1).integerValue(RoundingModes.DOWN).toNumber();

    let value = start;
    return JsonElementStreamer.fromTransformedStream(
      context,
      createAsyncSequence({
        next: () => {
          const result = value;
          value = value.plus(step);
          return Promise.resolve({ value: result });
        },
      }).take(size),
    );
  }
}

export default TransformerFunctionRange;
