import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

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
    const result: any[] = []; //new BigDecimal[end.subtract(start).divide(step, RoundingMode.FLOOR).add(BigDecimal.ONE).intValue()];
    let index = 0;
    for (let l = start; l.comparedTo(end) <= 0; l = l.plus(step)) {
      result.push(l);
      index++;
    }
    return result;
  }
}

export default TransformerFunctionRange;
