import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";
import { createAsyncSequence } from "@wortise/sequency";
import { RoundingModes } from "./common/FunctionHelpers";

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
    const start = await context.getBigDecimal("start");
    const end = await context.getBigDecimal("end");
    if (start == null || end == null) {
      return [];
    }
    const step = (await context.getBigDecimal("step")) ?? 1;
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
