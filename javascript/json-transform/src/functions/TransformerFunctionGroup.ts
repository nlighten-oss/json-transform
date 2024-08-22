import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { getAsString, isNullOrUndefined } from "../JsonHelpers";
import CompareBy from "./common/CompareBy";

const DESCRIPTION: FunctionDescription = {
  aliases: ["group"],
  description: "",
  inputType: ArgType.Array,
  arguments: {
    by: {
      type: ArgType.Transformer,
      position: 0,
      required: true,
      description: "A transformer to extract a property to group by (using ##current to refer to the current item)",
    },
    order: {
      type: ArgType.Enum,
      position: 1,
      defaultEnum: "ASC",
      enumValues: ["ASC", "DESC"],
      description: "Direction of ordering (ascending / descending)",
    },
    type: {
      type: ArgType.Enum,
      position: 2,
      defaultEnum: "AUTO",
      enumValues: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
      description: "Type of values to expect when ordering the input array",
    },
    then: {
      type: ArgType.Array,
      position: 3,
      defaultIsNull: true,
      description:
        "An array of subsequent grouping. When previous sort had no difference (only when `by` specified)\n" +
        '{ "by": ..., "order": ..., "type": ...} // same 3 fields as above (`by` is required)',
    },
  },
  outputType: ArgType.Object,
};
class TransformerFunctionGroup extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  add(root: Record<string, any>, by: CompareBy) {
    let elem = root;
    const byby = by.by;
    const bybySize = byby.length ?? 0;
    for (let i = 0; i < bybySize - 1; i++) {
      const byKey = byby[i];
      // when adding a grouping key, fallback on empty string if null
      const key = getAsString(byKey) ?? "";
      if (isNullOrUndefined(root[key])) {
        const jo: any = {};
        root[key] = jo;
        elem = jo;
      } else {
        elem = root[key];
      }
    }
    const byKey = byby[bybySize - 1];
    // when adding a grouping key, fallback on empty string if null
    const key = getAsString(byKey) ?? "";
    const jArr = elem[key] ?? [];
    jArr.push(by.value);
    elem[key] = jArr;
    return root;
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    if (value == null) {
      return {};
    }
    const type = await context.getEnum("type");
    const order = await context.getEnum("order");
    const by = await context.getJsonElement("by", false);
    if (isNullOrUndefined(by)) {
      return {};
    }

    const chain: any[] = [];

    let comparator = CompareBy.createByComparator(0, type);
    if ("DESC" === order?.toUpperCase()) {
      comparator = comparator.reversed();
    }
    chain.push(by);

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

    const result: any = {};
    await value
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
      .forEach(itm => this.add(result, itm));
    return result;
  }
}

export default TransformerFunctionGroup;
