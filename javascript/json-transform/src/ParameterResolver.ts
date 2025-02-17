export interface ParameterResolver {
  get(name: string): any;
}

export const parameterResolverFromMap = (map: Record<string, any>): ParameterResolver => {
  return {
    get: (name: string) => map[name],
  };
};

export const isParameterResolver = (obj: any): obj is ParameterResolver => {
  return typeof obj?.get === "function";
};
