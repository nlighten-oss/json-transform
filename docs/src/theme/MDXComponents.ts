import MDXComponents from "@theme-original/MDXComponents";
import MDXMarkdown from "@site/src/components/MDXMarkdown";
import FunctionIntro from "@site/src/components/FunctionIntro";
import FunctionUsage from "@site/src/components/FunctionUsage";
import FunctionReturns from "@site/src/components/FunctionReturns";
import FunctionArguments from "@site/src/components/FunctionArguments";
import FunctionExamples from "@site/src/components/FunctionExamples";
import FunctionSubfunctions from "@site/src/components/FunctionSubfunctions";
import FunctionPage from "@site/docs/functions/_FunctionPage.mdx";

export default {
  ...MDXComponents, // Re-use the default mapping
  FunctionIntro,
  FunctionUsage,
  FunctionReturns,
  FunctionArguments,
  FunctionExamples,
  FunctionSubfunctions,
  FunctionPage,
  MDXMarkdown,
};
