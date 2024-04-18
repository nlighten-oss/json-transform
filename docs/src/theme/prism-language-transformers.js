const variable =
  /(?<![\])}?!@#$%^&*+\w])(#[a-z_]+[a-z_\d]*|\$(?!\$))(((\.(?![-\w$]+\()[-\w$]+)|(\[[^\]\n]+]))+|(?=[^\w.]|$))/g;
const number = /-?\b\d+(?:\.\d+)?(?:e[+-]?\d+)?\b/i;
const function_context = {
  pattern: /##([a-z]+[a-z_\d]*)(((\.(?![-\w$]+\()[-\w$]+)|(\[[^\]\n]+]))+|(?=[^\w.]|$))/,
  alias: "constant",
};

Prism.languages.transformers = {
  "object-function": {
    pattern: /"\$\$\w+"/,
    alias: "function",
  },
  // base is "json"
  property: {
    pattern: /(^|[^\\])"(?:\\.|[^\\"\r\n])*"(?=\s*:)/,
    lookbehind: true,
    greedy: true,
    alias: "attr-name",
  },
  string: {
    pattern: /(^|[^\\])"(?:\\.|\$\$|[^\\"\r\n])*"(?!\s*:)/,
    inside: {
      "inline-function": {
        pattern: /\$\$\w+(\((.*?)\))?(:|$|(?="))/i,
        inside: {
          function: /^\$\$\w+/i, // prefix of matched pattern
          punctuation: /^\(|,\s*|\)?:|\)$/,
          "required-argument": {
            pattern: /<\w+>/,
            alias: "changed",
          },
          "optional-argument": {
            pattern: /(?<!\$)\[\w+]/,
            alias: "selector",
          },
          "function-context": function_context,
          prolog: /#\w+[^"]*/,
          variable,
        },
        alias: "string", // everything else: blue
      },
      "function-context": function_context,
      prolog: /#\w+[^"]*/,
      variable,
    },
    lookbehind: true,
    greedy: true,
  },
  comment: {
    pattern: /\/\/.*|\/\*[\s\S]*?(?:\*\/|$)/,
    greedy: true,
  },
  number,
  punctuation: /[{}[\],]/,
  operator: /:/,
  boolean: /\b(?:false|true)\b/,
  null: {
    pattern: /\b(null|undefined)\b/,
    alias: "keyword",
  },
};
