import {ParameterResolver} from "./ParameterResolver";

export interface JsonTransformerFunction {
  transform(definition: any, resolver: ParameterResolver, allowReturningStreams?: boolean): Promise<any>;
}