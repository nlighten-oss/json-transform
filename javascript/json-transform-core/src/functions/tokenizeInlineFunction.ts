type InlineFunctionArgToken = {
  value: string | null;
  index: number;
  length: number;
};

export type TokenizedInlineFunction = {
  name: string;
  keyLength: number;
  args?: InlineFunctionArgToken[];
  input?: InlineFunctionArgToken;
};

const JsonEscapeCharacters: Record<string, string> = {
  b: "\b",
  f: "\f",
  n: "\n",
  r: "\r",
  t: "\t",
  v: "\v",
};

export function tokenizeInlineFunction(input: string): TokenizedInlineFunction | undefined {
  if (!input || !input.startsWith("$$")) {
    return undefined; // not a function
  }

  let i = 2; // skip '$$'
  const len = input.length;

  const nameStart = i;
  while (i < len && /[\w-]/.test(input[i])) i++;
  const functionName = input.slice(nameStart, i);
  const keyLength = i;

  if (!functionName) {
    return undefined; // syntax error, no function name found
  }

  let args: InlineFunctionArgToken[] | undefined;

  // Check if arguments exist
  if (input[i] === "(") {
    i++; // skip '('
    args = [];
    let current = "";
    let currentStart = i;
    let inQuote = false;
    let escape = false;
    const len = input.length;
    let finished = false;

    for (; i < len; i++) {
      const char = input[i];

      if (escape) {
        let escapedChar = JsonEscapeCharacters[char];
        if (!escapedChar) {
          if (char === "u" || char === "x") {
            // Handle char code escape sequences like \x0f or \u0f0f
            try {
              const width = char === "u" ? 4 : 2; // unicode is 4 digits, hex is 2 digits
              escapedChar = JSON.parse(`"\\u${input.substring(i + 1, i + 1 + width)}"`);
              i += width; // skip unicode sequence
            } catch (e: any) {
              return undefined; // syntax error, bad unicode escape sequence
            }
          } else {
            // If the character is not a recognized escape sequence, treat it as a normal character
            escapedChar = char;
          }
        }
        current += escapedChar;
        escape = false;
        continue;
      }

      if (char === "\\" && inQuote) {
        escape = true;
        continue;
      }

      if (char === "'") {
        if (inQuote) {
          // read whitespaces until next char is not a space
          while (i < len && input[i + 1] === " ") {
            i++;
          }
        } else if (current.length && current.trim().length === 0) {
          current = "";
        }
        inQuote = !inQuote;
        continue;
      }

      if (!inQuote && (char === "," || char === ")")) {
        const length = i - currentStart;
        args.push({
          value: length ? current : null,
          index: currentStart,
          length,
        });
        current = "";
        currentStart = i + 1; // start a new argument value after the comma
        if (char === ")") {
          i++; // skip ')'
          finished = true;
          break;
        }
      } else {
        current += char;
      }
    }
    if (!finished) {
      return undefined; // syntax error, missing closing parenthesis
    }
    if (i < input.length && input[i] !== ":") {
      return undefined; // syntax error, expected ':' or EOS
    }
  }

  // Optional input
  let fnInput: InlineFunctionArgToken | undefined;
  if (input[i] === ":") {
    i++; // skip ':'
    fnInput = {
      value: input.slice(i),
      index: i,
      length: input.length - i,
    };
  }

  return {
    name: functionName,
    keyLength,
    args,
    input: fnInput,
  };
}
