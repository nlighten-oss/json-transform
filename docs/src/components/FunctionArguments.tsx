import { Fragment } from "react";
import { useLocation } from "@docusaurus/router";
import { Argument, definitions, FunctionDescriptor } from "@nlighten/json-transform-core";
import MDXMarkdown from "@site/src/components/MDXMarkdown";
import getSubfunctionOrFunction from "@site/src/components/getSubfunctionOrFunction";

const getType = (arg: Argument) => {
  if (arg.type === "transformer") {
    return (
      <span>
        Transformer(
        {arg.transformerArguments?.map((v, i, a) => (
          <Fragment key={v.name}>
            <code>{v.name}</code>
            {i < a.length - 1 ? "," : ""}
          </Fragment>
        ))}
        )
      </span>
    );
  }
  return <code>{arg.type}</code>;
};

const getValues = (arg: Argument) => {
  if (arg.type === "const") {
    return (
      <>
        <code>{`${arg.const}`}</code>
      </>
    );
  }
  if (arg.type === "boolean") {
    return (
      <>
        <code>false</code>/<code>true</code>
      </>
    );
  }
  if (arg.type === "enum" && arg.enum) {
    return (
      <>
        {arg.enum.map((v, i, a) => (
          <Fragment key={v}>
            <code>{v}</code>
            {i < a.length - 1 ? "/" + (i % 5 === 0 ? " " : "") : ""}
          </Fragment>
        ))}
      </>
    );
  }
  return null;
};

const getRequiredOrDefault = (arg: Argument) => {
  if (arg.required) return <strong>Yes</strong>;
  if (typeof arg.default !== "undefined") {
    return (
      <code>
        {arg.type === "string" || arg.type === "array" || arg.type.endsWith("[]")
          ? JSON.stringify(arg.default)
          : `${arg.default}`}
      </code>
    );
  }
  return null;
};

const argumentRow = (arg?: Argument, index?: number) => (
  <tr key={index}>
    <td>{arg?.name ? <code>{arg.name}</code> : "Primary"}</td>
    <td>{arg && getType(arg)}</td>
    <td>{arg && getValues(arg)}</td>
    <td>{arg && getRequiredOrDefault(arg)}</td>
    <td>
      {arg?.description && <MDXMarkdown md={arg.description.replace(/</g, "&lt;").replace(/>/g, "&gt;")} />}
      {arg?.type === "transformer" && (
        <ul>
          {arg.transformerArguments?.map(v => (
            <li key={v.name}>
              <code>{v.name}</code> - {v.description}
            </li>
          ))}
        </ul>
      )}
    </td>
  </tr>
);

const generateTable = (key: any, inArg: Omit<Argument, "name">, args?: Argument[]) => (
  <table key={key}>
    <thead>
      <tr>
        <th>Argument</th>
        <th>Type</th>
        <th>Values</th>
        <th>Required / Default&nbsp;Value</th>
        <th>Description</th>
      </tr>
    </thead>
    <tbody>
      {argumentRow(inArg as Argument)}
      {args?.map(argumentRow)}
    </tbody>
  </table>
);

export default function FunctionArguments({ func, sub }: { func: FunctionDescriptor; sub?: number }) {
  const location = useLocation();
  const name = location.pathname.split("/").pop();
  const definition: FunctionDescriptor = getSubfunctionOrFunction(func ?? definitions[name], sub);

  const inArg = definition.inputSchema;

  const tables = [generateTable(0, inArg, definition.arguments)];

  if (definition.argumentsNotes) {
    tables.push(<MDXMarkdown key={tables.length} md={definition.argumentsNotes} />);
  }

  return tables;
}
