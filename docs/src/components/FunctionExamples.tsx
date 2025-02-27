import { useEffect, useState } from "react";
import { useLocation } from "@docusaurus/router";
import { examples, JsonTransformExample } from "@nlighten/json-transform-core";
import MDXMarkdown from "@site/src/components/MDXMarkdown";
import { shareLink } from "@site/src/utils/shareLink";

const isDifferentLang = (format: string) => {
  return format === "xml" || format === "csv" || format === "yaml" || format === "big-decimal";
};

export default function FunctionExamples() {
  const location = useLocation();
  const name = location.pathname.split("/").pop();
  const fExamples: JsonTransformExample[] = examples[name];

  const [examplesToShow, setExamplesToShow] = useState<any[]>([]);

  useEffect(() => {
    const eToS = fExamples.filter(x => typeof x.expect.equal !== "undefined" || x.expect.isNull);
    setExamplesToShow(eToS);

    for (let i = 0; i < eToS.length; i++) {
      shareLink(eToS[i].given.input, eToS[i].given.definition).then(link => {
        setExamplesToShow((e: any[]) => e.map((x, j) => (i !== j ? x : { ...x, link })));
      });
    }
  }, [fExamples]);

  const markdown = `

**Input**

**Definition**

**Output**

${examplesToShow
  .map(
    x => `
\`\`\`${isDifferentLang(x.given.inputFormat) ? x.given.inputFormat : "json"}
${isDifferentLang(x.given.inputFormat) ? x.given.input : JSON.stringify(x.given.input, null, 2)}
\`\`\`
\`\`\`transformers 
${JSON.stringify(x.given.definition, null, 2)}
\`\`\`
\`\`\`${isDifferentLang(x.expect.format) ? x.expect.format : "json"}
${
  isDifferentLang(x.expect.format) ? x.expect.equal : x.expect.isNull ? "null" : JSON.stringify(x.expect.equal, null, 2)
}
\`\`\`

<div className="action" />

<div className="action ${!(x as any).link ? "action--hidden" : ""}">
    <a title="Play this example on the transformers playground" href="${
      (x as any).link
    }" className="button button--primary transform-button shadow--lw">Test</a>
</div>

<div className="action" />
`,
  )
  .join("\n")}
`;

  return (
    <div className="examples_grid">
      <MDXMarkdown md={markdown} />
    </div>
  );
}
