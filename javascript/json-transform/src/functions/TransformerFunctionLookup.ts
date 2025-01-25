import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { getAsString, isMap, isNullOrUndefined, isTruthy, mergeInto } from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";

type UsingEntry = {
  with: any[];
  as: string;
  on: any;
};

class TransformerFunctionLookup extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        using: { type: ArgType.Array, position: 0 },
        to: { type: ArgType.Transformer, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;
    const usingArray = await context.getJsonArray("using", false);
    if (usingArray == null) return null;

    // prepare matches map (this will be used in each iteration to create the merged item)
    const matches: Record<string, any> = {};
    const usingMap: Record<number, UsingEntry> = {};
    const usingArraySize = usingArray.length;
    for (let w = 0; w < usingArraySize; w++) {
      // prepare matches map
      const using = usingArray[w];
      const asDef = using.as;
      if (isNullOrUndefined(asDef)) continue; // as - null
      const as = getAsString(asDef);
      delete matches["##" + as];

      // collect using
      const withDef = using.with;
      if (isNullOrUndefined(withDef)) continue; // with - null
      const $with = await context.transform(context.getPathFor("with"), withDef);
      if (!Array.isArray($with)) continue; // with - not array
      usingMap[w] = { with: $with, as, on: using.on };
    }

    const to = context.has("to") ? await context.getJsonElement("to", false) : null; // we don't transform definitions to prevent premature evaluation

    const index = 0;
    return JsonElementStreamer.fromTransformedStream(
      context,
      streamer.stream().map(async item1 => {
        const i = index;
        for (let w = 0; w < usingArraySize; w++) {
          if (!Object.prototype.hasOwnProperty.call(usingMap, w)) continue;
          const e = usingMap[w];

          let match: any = null;
          const withSize = e.with.length;
          for (let j = 0; j < withSize; j++) {
            const item2 = e.with[j];
            const conditionResult = await context.transformItem(e.on, item1, i, "##" + e.as, item2);
            if (isTruthy(conditionResult)) {
              match = item2;
              break;
            }
          }
          matches["##" + e.as] = match;
        }

        if (to == null) {
          if (isMap(item1)) {
            let merged = item1;
            for (let val of Object.values(matches)) {
              if (!isMap(val)) continue; // edge case - tried to merge with an item which is not an object
              merged = mergeInto(merged, val, null);
            }
            return merged;
          } else {
            // edge case - tried to merge to an item which is not an object
            return item1;
          }
        } else {
          return context.transformItem(to, item1, i, matches);
        }
      }),
    );
  }
}

export default TransformerFunctionLookup;
