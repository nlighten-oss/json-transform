import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";

class TransformerFunctionSlice extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        begin: { type: ArgType.Integer, position: 0, defaultInteger: 0 },
        end: { type: ArgType.Integer, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    if (value == null) {
      return null;
    }
    const begin = (await context.getInteger("begin")) ?? 0;
    const end = await context.getInteger("end");
    if (begin >= 0) {
      if (end == null) {
        // // slice() / slice(1)
        return begin == 0 ? value : JsonElementStreamer.fromTransformedStream(context, value.stream(begin));
      }
      if (end >= 0) {
        if (end <= begin) {
          // slice(3, 1) -- can't have indexes not in order, result is empty
          return [];
        }
        // slice(1, 3)
        return JsonElementStreamer.fromTransformedStream(context, value.stream(begin, end - begin));
      }
      // slice(1, -2)
      const result: any[] = [];
      // at least skip the start index and then convert to array
      await value.stream(begin).forEach(item => result.push(item));
      for (let i = 0; i < -end; i++) {
        result.splice(result.length - 1, 1);
      }
      return result;
    }
    // begin < 0
    if (end != null && end >= 0) {
      // slice(-1, 3) -- if begin is negative, end must be negative too, result is empty
      return [];
    }
    // end == null || end < 0

    // slice(-1) / slice(-3, -1)
    // any negative indices means that we now need to convert the stream to an array to determine the size
    const arr = await value.toJsonArray();
    // const arrSize = arr.length;
    // const result: any[] = [];
    // for (let i = arrSize + begin; i < arrSize + (end == null ? 0 : end) ; i++) {
    //   result.push(arr[i]);
    // }
    return end == null ? arr.slice(begin) : arr.slice(begin, end);
  }
}

export default TransformerFunctionSlice;
