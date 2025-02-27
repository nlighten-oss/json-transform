import { useLocation } from "@docusaurus/router";
import { Argument, definitions, FunctionDescriptor } from "@nlighten/json-transform-core";
import MDXMarkdown from "@site/src/components/MDXMarkdown";
import { TypeSchema } from "@nlighten/json-schema-utils";
import getSubfunctionOrFunction from "@site/src/components/getSubfunctionOrFunction";

const renderType = (schema: TypeSchema) => {
  if (schema.type === "array" && schema.items) {
    if (Array.isArray(schema.items)) {
      return "[" + schema.items.map(x => renderType(x)) + "]";
    } else {
      return renderType(schema.items) + "[]";
    }
  }
  if (schema.type === "string" && schema.format) {
    return "string (" + schema.format + ")";
  }
  return schema.type ?? "any";
};

export default function FunctionReturns({ func, sub }: { func: FunctionDescriptor; sub?: number }) {
  const location = useLocation();
  const name = location.pathname.split("/").pop();
  const definition: FunctionDescriptor = getSubfunctionOrFunction(func ?? definitions[name], sub);

  let returns = "Any";
  const schema: TypeSchema | undefined = definition.outputSchemaTemplate ?? definition.outputSchema;
  if (schema) {
    if (schema.type === "object" && schema.description) {
      returns = `_${schema.description}_`;
    } else {
      returns = `\`${renderType(schema)}\``;
      if (schema.$comment) {
        returns += ` (\`${schema.$comment}\`)`;
      }
      if (schema.description) {
        returns += ` - ${schema.description}`;
      }
    }
  }

  return <MDXMarkdown md={returns} />;
}
