export default {
  plain: {
    color: "#9cdcfe",
    backgroundColor: "#1e1e1e",
  },
  styles: [
    {
      types: ["prolog"],
      style: {
        color: "#d7a6ff",
      },
    },
    {
      types: ["comment"],
      style: {
        color: "rgb(106, 153, 85)",
      },
    },
    {
      types: ["builtin", "changed", "keyword", "interpolation-punctuation", "boolean"],
      style: {
        color: "rgb(86, 156, 214)",
      },
    },
    {
      types: ["number", "inserted"],
      style: {
        color: "#b5cea8",
      },
    },
    {
      types: ["constant"],
      style: {
        color: "#7d7df3",
      },
    },
    {
      types: ["attr-name"],
      style: {
        color: "#9cdcfe",
      },
    },
    {
      types: ["variable"],
      style: {
        color: "#80ff80",
      },
    },
    {
      types: ["deleted", "string", "attr-value", "template-punctuation"],
      style: {
        color: "#ce9178",
      },
    },
    {
      types: ["selector"],
      style: {
        color: "rgb(215, 186, 125)",
      },
    },
    {
      // Fix tag color
      types: ["tag"],
      style: {
        color: "rgb(78, 201, 176)",
      },
    },
    {
      // Fix tag color for HTML
      types: ["tag"],
      languages: ["markup"],
      style: {
        color: "rgb(86, 156, 214)",
      },
    },
    {
      types: ["punctuation", "operator"],
      style: {
        color: "rgb(212, 212, 212)",
      },
    },
    {
      // Fix punctuation color for HTML
      types: ["punctuation"],
      languages: ["markup"],
      style: {
        color: "#808080",
      },
    },
    {
      types: ["function"],
      style: {
        color: "#ffff80",
      },
    },
    {
      types: ["class-name"],
      style: {
        color: "rgb(78, 201, 176)",
      },
    },
    {
      types: ["char"],
      style: {
        color: "rgb(209, 105, 105)",
      },
    },
  ],
};
