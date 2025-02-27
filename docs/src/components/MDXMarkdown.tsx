import { useEffect, useState } from "react";
import * as runtime from "react/jsx-runtime";
import * as provider from "@mdx-js/react";
import remarkGfm from "remark-gfm";
import Admonition from "@theme/Admonition";
import { evaluate, EvaluateOptions } from "@mdx-js/mdx";

const Options: EvaluateOptions = {
  ...provider,
  ...runtime,
  /**
   * Adds table support (GitHub flavored markdown)
   * @see https://mdxjs.com/guides/gfm/
   */
  remarkPlugins: [remarkGfm],
};

const ADMONITION_REGEXP = /\s(?=:::)/gm;

type AdmonitionDescription = {
  type: string;
  title?: string;
  content: string;
};

export default function MDXMarkdown({ md, admonitions }: { md: string; admonitions?: boolean }) {
  const [rendered, setRendered] = useState<any>(null);

  useEffect(() => {
    if (admonitions) {
      const mdList = md
        .split(ADMONITION_REGEXP)
        .map(x => {
          if (x.startsWith(":::")) {
            if (x[3] === "\n") {
              return x.substring(4);
            }
            const matches = x.match(/:::(\w+)(\[([^\]]+)])?/);
            const type = matches?.[1];
            if (!type) {
              console.error(`Failed parsing type [${x}]`);
              return null;
            }
            const title = matches?.[3];
            const content = x.substring(x.indexOf("\n") + 1);
            return { type, title, content } as AdmonitionDescription;
          }
          return x;
        })
        .filter(Boolean);
      Promise.all(
        mdList.map((x, i) => {
          if (typeof x === "string") {
            return evaluate(x, Options).then(MM => <MM.default key={i} />);
          }
          return Promise.resolve(
            <Admonition key={i} {...(x.title ? { title: x.title } : null)} type={x.type}>
              <MDXMarkdown md={x.content} />
            </Admonition>,
          );
        }),
      )
        .then(setRendered)
        .catch(error => {
          setRendered(
            <Admonition type="danger" title="Error">
              {error.message}
            </Admonition>,
          );
        });
    } else {
      evaluate(md, Options)
        .then(result => {
          if (result.default) {
            setRendered(<result.default />);
          }
        })
        .catch(error => {
          setRendered(
            <Admonition type="danger" title="Error">
              {error.message}
            </Admonition>,
          );
        });
    }
  }, [md, admonitions]);

  return rendered;
}
