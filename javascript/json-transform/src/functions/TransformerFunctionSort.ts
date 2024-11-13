import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { createComparator, getAsString, isNullOrUndefined } from "../JsonHelpers";
import CompareBy from "./common/CompareBy";
import JsonElementStreamer from "../JsonElementStreamer";

class TransformerFunctionSort extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        by: { type: ArgType.Transformer, position: 0 },
        order: { type: ArgType.Enum, position: 1, defaultEnum: "ASC" },
        type: { type: ArgType.Enum, position: 2, defaultEnum: "AUTO" },
        then: { type: ArgType.Array, position: 3, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    if (value == null) {
      return null;
    }
    const type = await context.getEnum("type");
    const order = await context.getEnum("order");
    const by = await context.getJsonElement("by", false);

    if (isNullOrUndefined(by)) {
      let comparator = createComparator(type);
      if ("DESC" === order?.toUpperCase()) {
        comparator = comparator.reversed();
      }
      return JsonElementStreamer.fromTransformedStream(context, value.stream().sortedWith(comparator.compare));
    }

    let comparator = CompareBy.createByComparator(0, type);
    if ("DESC" === order?.toUpperCase()) {
      comparator = comparator.reversed();
    }

    const chain: any[] = [by];
    const thenArr = context.has("then") ? await context.getJsonArray("then", false) : null;
    if (thenArr != null) {
      const thenArrSize = thenArr.length;
      for (let i = 0; i < thenArrSize; i++) {
        const thenObj = thenArr[i];
        const thenType = thenObj.type ? getAsString(thenObj.type)?.trim() ?? null : null;
        const thenOrder = thenObj.order;
        let thenComparator = CompareBy.createByComparator(i + 1, thenType);
        const thenDescending = !isNullOrUndefined(thenOrder) && getAsString(thenOrder)?.toUpperCase() === "DESC";
        if (thenDescending) {
          thenComparator = thenComparator.reversed();
        }
        comparator = comparator.then(thenComparator.compare);
        chain.push(thenObj.by);
      }
    }

    return JsonElementStreamer.fromTransformedStream(
      context,
      value
        .stream()
        .map(async item => {
          const cb = new CompareBy(item);
          for (const jsonElement of chain) {
            const byKey = await context.transformItem(jsonElement, item);
            cb.by.push(byKey);
          }
          return cb;
        })
        .sortedWith(comparator.compare)
        .map(itm => itm.value),
    );
  }
}

export default TransformerFunctionSort;
