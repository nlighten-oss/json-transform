import FunctionContext from "./functions/common/FunctionContext";
import { asAsyncSequence, AsyncSequence, emptyAsyncSequence } from "@wortise/sequency";

class JsonElementStreamer {
  private readonly context: FunctionContext;
  private readonly transformed: boolean;
  private value?: any[];
  private readonly _stream?: AsyncSequence<any>;

  private constructor(context: FunctionContext, value: any[] | AsyncSequence<any>, transformed: boolean) {
    this.context = context;
    this.transformed = transformed;
    if (Array.isArray(value)) {
      this.value = value;
      this._stream = undefined;
    } else {
      this.value = undefined;
      this._stream = value;
    }
  }

  public knownAsEmpty() {
    return this.value && this.value.length == 0;
  }

  public stream(skip: number = 0, limit: number = -1) {
    if (this._stream != null && !this.value) {
      const skipped = skip > 0 ? this._stream.drop(skip) : this._stream;
      return limit > -1 ? skipped.take(limit) : skipped;
    }
    if (this.value == null) {
      return emptyAsyncSequence();
    }
    let valueStream = asAsyncSequence(this.value);
    if (skip > 0) {
      valueStream = valueStream.drop(skip);
    }
    if (limit > -1) {
      valueStream = valueStream.take(limit);
    }
    if (!this.transformed) {
      valueStream = valueStream.map(el => this.context.transform(undefined, el));
    }
    return valueStream;
  }

  public static fromJsonArray(context: FunctionContext, value: any[], transformed: boolean) {
    return new JsonElementStreamer(context, value, transformed);
  }

  public static fromTransformedStream(context: FunctionContext, stream: AsyncSequence<any>) {
    return new JsonElementStreamer(context, stream, true);
  }

  public async toJsonArray() {
    if (this.value) {
      return this.value;
    }
    if (this._stream) {
      return this._stream.toArray().then(arr => {
        this.value = arr;
        return arr;
      });
    }
    return [];
  }
}

export default JsonElementStreamer;
