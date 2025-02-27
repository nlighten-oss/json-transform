import { useLocation } from "@docusaurus/router";
import { Argument, definitions, FunctionDescriptor } from "@nlighten/json-transform-core";
import MDXMarkdown from "@site/src/components/MDXMarkdown";
import getSubfunctionOrFunction from "@site/src/components/getSubfunctionOrFunction";

function compareArgumentPosition(a: Argument, b: Argument) {
  return (a.position ?? Infinity) - (b.position ?? Infinity);
}

const getInlineUsage = (name: string, func: FunctionDescriptor) => {
  let argsString = "";
  const sortedInlineArgs = func.arguments?.filter(a => typeof a.position === "number").sort(compareArgumentPosition);
  if (sortedInlineArgs?.length ?? 0 > 0) {
    argsString = "(";
    argsString += sortedInlineArgs
      .map(a => (a.type == "const" ? `${a.const}` : a.required ? "<" + a.name + ">" : "[" + a.name + "]"))
      .join(",");
    argsString += ")";
  }
  return `"$$${name}${argsString}${func.inputSchema ? ":{input}" : ""}"`;
};

const getObjectUsageValue = (a: Argument, subArgs: Argument[], indent: string) => {
  if (a.type === "const") {
    return JSON.stringify(a.const);
  }
  let val = "";
  let isArray = a.type === "array";
  let hasSubArgs = subArgs.length > 0;
  if (hasSubArgs) {
    indent += "  ";
    val = isArray ? "[" : "{";
  }

  if (typeof a.default !== "undefined") {
    val = JSON.stringify(a.default);
  }
  let comment =
    a.type === "transformer"
      ? `Transformer(${a.transformerArguments?.map(v => v.name).join(",")})`
      : a.type === "enum"
        ? (a.default ? "or " : "") + a.enum?.filter(x => x !== a.default).join(" / ")
        : a.type;
  if (comment.length > 80) {
    comment = a.description;
  }

  val = (val ? val + " " : "") + `/* ${comment} */`;
  if (hasSubArgs) {
    if (isArray) {
      val += `\n${indent}{`;
    }
    val +=
      "\n" +
      subArgs
        .filter(isArray ? x => x.name.startsWith(a.name + "[") : x => x.name.startsWith(a.name + "."))
        .sort(compareArgumentPosition)
        .map(
          sa =>
            `${indent}  "${sa.name.substring(a.name.length + (isArray ? 3 : 1))}": ${getObjectUsageValue(
              sa,
              subArgs.filter(x => x.name.startsWith(sa.name + ".") || x.name.startsWith(sa.name + "[")),
              indent + "  ",
            )}`,
        )
        .join(",\n");
    val += isArray ? `,\n${indent}},\n${indent}...\n${indent.substring(2)}]` : `\n${indent}}`;
  }
  return val;
};

const renderArguments = (args: Argument[], indent: string) => {
  return args
    ? args
        .filter(a => !/[.[]/.test(a.name))
        .sort(compareArgumentPosition)
        .map(
          a =>
            `${indent}"${a.name}": ${getObjectUsageValue(
              a,
              args.filter(x => x.name.startsWith(a.name + ".") || x.name.startsWith(a.name + "[")),
              indent,
            )}`,
        )
        .join(",\n")
    : "";
};

const getObjectUsage = (name: string, func: FunctionDescriptor) => {
  const inp = func.inputSchema;
  return `{ 
  "$$${name}": ${
    inp?.required
      ? inp.type === "array" && !inp?.description?.startsWith("Array")
        ? `[ /* ${inp?.description} */ ]`
        : `/* ${inp?.description} */`
      : inp.type === "array"
        ? "[ /* values */ ]"
        : "/* input */"
  }${func.arguments ? ",\n" : ""}${renderArguments(func.arguments, "  ")}
}`;
};

const getUsageMarkdown = (name: string, definition: FunctionDescriptor) => {
  const inl = "```transformers\n" + getInlineUsage(name, definition) + "\n```\n";
  if (!definition.inputSchema) {
    return inl;
  }
  const obj = "```transformers\n" + getObjectUsage(name, definition) + "\n```\n";
  return obj + inl;
};

export default function FunctionUsage({ func, sub }: { func: FunctionDescriptor; sub?: number }) {
  const location = useLocation();
  const name = location.pathname.split("/").pop();
  const definition: FunctionDescriptor = getSubfunctionOrFunction(func ?? definitions[name], sub);

  let md = getUsageMarkdown(name, definition);
  if (definition.aliases) {
    md += definition.aliases.map(alias => getUsageMarkdown(alias, definition)).join("\n\n");
  }
  let haveAdmonitions = false;
  if (definition.inputSchema && definition.arguments?.some(x => typeof x.default !== "undefined")) {
    md += "\n:::note\nConcrete values in the usage example are default values.\n:::";
    haveAdmonitions = true;
  }
  if (definition.usageNotes) {
    md += "\n" + definition.usageNotes;
    haveAdmonitions = true;
  }

  return <MDXMarkdown md={md} admonitions={haveAdmonitions} />;
}
