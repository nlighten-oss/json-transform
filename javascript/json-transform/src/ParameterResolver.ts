export interface ParameterResolver {
  get(name: string): any;
}