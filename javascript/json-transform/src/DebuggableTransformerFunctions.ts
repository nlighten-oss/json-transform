import { FunctionMatchResult, TransformerFunctions } from "./transformerFunctions";
import JsonElementStreamer from "./JsonElementStreamer";
import { ParameterResolver } from "./ParameterResolver";
import { JsonTransformerFunction } from "./JsonTransformerFunction";

export type TransformerDebugInfo = {
  result: any;
};

export default class DebuggableTransformerFunctions extends TransformerFunctions {
  private readonly debugResults: Record<string, TransformerDebugInfo>;

  constructor() {
    super();
    this.debugResults = {};
  }

  public getDebugResults(): Record<string, TransformerDebugInfo> {
    return this.debugResults;
  }

  private async auditAndReturn(path: string, matchResult: FunctionMatchResult | null) {
    if (!matchResult) {
      return null;
    }
    // if the function result is the transformer's output, don't audit it
    if (path === "$") return matchResult;

    const value = matchResult.getResult();
    if (value instanceof JsonElementStreamer) {
      this.debugResults[matchResult.getResultPath()] = { result: await value.toJsonArray() };
      return matchResult;
    }
    this.debugResults[matchResult.getResultPath()] = { result: value };
    return matchResult;
  }

  override async matchObject(
    path: string,
    definition: any,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ) {
    return super.matchObject(path, definition, resolver, transformer).then(result => this.auditAndReturn(path, result));
  }
  override async matchInline(
    path: string,
    value: string,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ) {
    return super.matchInline(path, value, resolver, transformer).then(result => this.auditAndReturn(path, result));
  }
}
