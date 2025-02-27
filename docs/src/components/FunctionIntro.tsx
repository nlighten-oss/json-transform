import { useLocation } from "@docusaurus/router";
import { definitions, FunctionDescriptor } from "@nlighten/json-transform-core";
import MDXMarkdown from "@site/src/components/MDXMarkdown";
import getSubfunctionOrFunction from "@site/src/components/getSubfunctionOrFunction";

export default function FunctionIntro({ func, sub }: { func: FunctionDescriptor; sub?: number }) {
  const location = useLocation();
  const name = location.pathname.split("/").pop();
  const definition: FunctionDescriptor = getSubfunctionOrFunction(func ?? definitions[name], sub);

  return (
    <MDXMarkdown
      admonitions
      md={`
${definition.description}

${definition.notes ?? ""}
`}
    />
  );
}
