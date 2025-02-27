import { useLocation } from "@docusaurus/router";
import { definitions, FunctionDescriptor } from "@nlighten/json-transform-core";
import FunctionIntro from "@site/src/components/FunctionIntro";
import FunctionUsage from "@site/src/components/FunctionUsage";
import FunctionReturns from "@site/src/components/FunctionReturns";
import FunctionArguments from "@site/src/components/FunctionArguments";

export default function FunctionSubfunctions() {
  const location = useLocation();
  const name = location.pathname.split("/").pop();
  const definition: FunctionDescriptor = definitions[name];

  if (!definition.subfunctions) return null;

  const sections: any[] = [];

  for (const o of definition.subfunctions) {
    const subFunc: FunctionDescriptor = {
      ...definition,
      ...o.then,
      // dont copy notes
      notes: undefined,
      usageNotes: undefined,
      argumentsNotes: undefined,
    };
    sections.push(
      o.if.length === 1 ? (
        <h3 key={sections.length}>
          {o.if[0].equals === "TRUE" ? "" : o.if[0].equals} {o.if[0].argument}
        </h3>
      ) : (
        <h3 key={sections.length}>When {o.if.map(c => `${c.argument} = ${c.equals}`).join(" and ")}</h3>
      ),
    );
    sections.push(<FunctionIntro key={sections.length} func={subFunc} />);
    sections.push(<h4 key={sections.length}>Usage</h4>);
    sections.push(<FunctionUsage key={sections.length} func={subFunc} />);
    sections.push(<h4 key={sections.length}>Returns</h4>);
    sections.push(<FunctionReturns key={sections.length} func={subFunc} />);
    sections.push(<h4 key={sections.length}>Arguments</h4>);
    sections.push(<FunctionArguments key={sections.length} func={subFunc} />);
  }

  return sections;
}
