import { FunctionDescriptor } from "@nlighten/json-transform-core";

export default function getSubfunctionOrFunction(definition: FunctionDescriptor, index?: number): FunctionDescriptor {
  return typeof index === "undefined"
    ? definition
    : {
        outputSchema: definition.outputSchema,
        ...definition.subfunctions[index].then,
      };
}
