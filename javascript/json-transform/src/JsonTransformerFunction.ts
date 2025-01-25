import { ParameterResolver } from "./ParameterResolver";

export interface JsonTransformerFunction {
  transform(path: string, definition: any, resolver: ParameterResolver, allowReturningStreams?: boolean): Promise<any>;
}
