import FunctionContext from "./functions/common/FunctionContext";
import Sequence, { asSequence, emptySequence } from "sequency";

class JsonElementStreamer {
  private readonly context: FunctionContext;
  private readonly transformed: boolean;
  private readonly value?: any[];
  private readonly _stream?: Sequence<any>;

  private constructor(context: FunctionContext, value: any[] | Sequence<any>, transformed: boolean) {
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
    if (this._stream != null) {
      const skipped = skip > 0 ? this._stream.drop(skip) : this._stream;
      return limit > -1 ? skipped.take(limit) : skipped;
    }
    if (this.value == null) {
      return emptySequence();
    }
    let valueStream = asSequence(this.value);
    if (skip > 0) {
      valueStream = valueStream.drop(skip);
    }
    if (limit > -1) {
      valueStream = valueStream.take(limit);
    }
    if (!this.transformed) {
      valueStream = valueStream.map(el => this.context.transform(el));
    }
    return valueStream;
  }

  public static fromJsonArray(context: FunctionContext, value: any[], transformed: boolean) {
    return new JsonElementStreamer(context, value, transformed);
  }

  public static fromTransformedStream(context: FunctionContext, stream: Sequence<any>) {
    return new JsonElementStreamer(context, stream, true);
  }

  public toJsonArray() {
    if (this.value) {
      return this.value;
    }
    const ja: any[] = [];
    if (this._stream) {
      this._stream.forEach(item => ja.push(item));
    }
    return ja;
  }
}

export default JsonElementStreamer;