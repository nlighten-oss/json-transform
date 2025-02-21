import { functionsParser, JsonPathFunctionRegex, transformUtils } from "@nlighten/json-transform-core";

const FunctionContextRegExp = /##([a-z]+[a-z_\d]*)(((\.(?![-\w$]+\()[-\w$]+)|(\[[^\]\n]+]))+|(?=[^\w.]|$))/g;
const InlineFunctionArgsPunctuationRegExp = /^\(|,\s*|\)$/g;
const FindCommentsRegex = /"(\/\/|\$comment)":\s*"(\\"|[^"])*"/g;

enum TokenType {
  VARIABLE = 0,
  VARIABLE_DEPRECATED,
  MEMBER,
  CONTEXT,
  ANNOTATION,
  FUNCTION,
  FUNCTION_CONTEXT,
  FUNCTION_DEPRECATED,
  COMMENT,
  NO_STYLE,
}

enum TokenModifier {
  DECLARATION = 0,
}

type Token = {
  line: number;
  char: number;
  length: number;
  type: TokenType;
  modifier: TokenModifier;
};

export type TokenizationState = {
  tokens: Token[];
};

export default function tokenizeLine(line: string, lineNumber: number, ts: TokenizationState) {
  // OBJECT FUNCTIONS
  let iter: IterableIterator<RegExpMatchArray> = functionsParser.matchAllObjectFunctionsInLine(line);
  for (
    let iterResult = iter.next(), match: RegExpMatchArray | null | undefined = iterResult.value;
    !iterResult.done;
    iterResult = iter.next(), match = iterResult.value as RegExpMatchArray | null | undefined
  ) {
    if (!match || typeof match.index === "undefined") continue;
    const func = functionsParser.get(match[1]);
    const deprecated = func?.deprecatedInFavorOf;

    ts.tokens.push({
      line: lineNumber,
      char: match.index,
      length: 2 + match[1].length, // $$ and name
      type: deprecated ? TokenType.FUNCTION_DEPRECATED : TokenType.FUNCTION,
      modifier: TokenModifier.DECLARATION,
    });
  }

  // INLINE FUNCTIONS (name and args symbols)
  iter = functionsParser.matchAllInlineFunctionsInLine(line);
  for (
    let iterResult = iter.next(), match: RegExpMatchArray | null | undefined = iterResult.value;
    !iterResult.done;
    iterResult = iter.next(), match = iterResult.value as RegExpMatchArray | null | undefined
  ) {
    if (!match || typeof match.index === "undefined") continue;
    const func = functionsParser.get(match[1]);
    const deprecated = func?.deprecatedInFavorOf;

    ts.tokens.push({
      line: lineNumber,
      char: match.index,
      length: 2 + match[1].length,
      type: deprecated ? TokenType.FUNCTION_DEPRECATED : TokenType.FUNCTION,
      modifier: TokenModifier.DECLARATION,
    });
    // :
    if (match[4][0] === ":") {
      ts.tokens.push({
        line: lineNumber,
        char: match.index + 2 + match[1].length + (match[2]?.length ?? 0),
        length: 1,
        type: TokenType.NO_STYLE,
        modifier: TokenModifier.DECLARATION,
      });
    }

    // arguments
    if (match[2]) {
      const argsIndex = match.index + 2 + match[1].length;
      const puncIter = match[2].matchAll(InlineFunctionArgsPunctuationRegExp);
      for (
        let puncIterResult = puncIter.next(), puncMatch: RegExpMatchArray | null | undefined = puncIterResult.value;
        !puncIterResult.done;
        puncIterResult = puncIter.next(), puncMatch = puncIterResult.value as RegExpMatchArray | null | undefined
      ) {
        if (!puncMatch || typeof puncMatch.index === "undefined") continue;
        ts.tokens.push({
          line: lineNumber,
          char: argsIndex + puncMatch.index,
          length: puncMatch.length,
          type: TokenType.NO_STYLE,
          modifier: TokenModifier.DECLARATION,
        });
      }
    }
  }

  // FUNCTION CONTEXT (##current)
  iter = line.matchAll(FunctionContextRegExp);
  for (
    let iterResult = iter.next(), match: RegExpMatchArray | null | undefined = iterResult.value;
    !iterResult.done;
    iterResult = iter.next(), match = iterResult.value as RegExpMatchArray | null | undefined
  ) {
    if (!match || typeof match.index === "undefined") continue;
    ts.tokens.push({
      line: lineNumber,
      char: match.index,
      length: match[0].length,
      type: TokenType.FUNCTION_CONTEXT,
      modifier: TokenModifier.DECLARATION,
    });
  }

  // VARIABLES ($. #...)
  iter = line.matchAll(transformUtils.variableDetectionRegExp);
  for (
    let iterResult = iter.next(), match: RegExpMatchArray | null | undefined = iterResult.value;
    !iterResult.done;
    iterResult = iter.next(), match = iterResult.value as RegExpMatchArray | null | undefined
  ) {
    if (!match || typeof match.index === "undefined") continue;
    let type = TokenType.VARIABLE;
    const matchLength = match[0].length,
      modifier = TokenModifier.DECLARATION;

    let skip = false;
    if (transformUtils.matchesAnyOfContextVariables(match[0])) {
      type = TokenType.CONTEXT;
    } else if (transformUtils.matchesAnyOfSpecialKeys(match[0])) {
      type = TokenType.MEMBER;
    } else if (transformUtils.matchesAnyOfAdditionalContext(match[0])) {
      type = TokenType.ANNOTATION;
    } else if (match?.[0][0] === "#") {
      // not one of the context variables
      skip = true;
    }

    if (!skip) {
      ts.tokens.push({
        line: lineNumber,
        char: match.index,
        length: matchLength,
        type,
        modifier,
      });

      const matchEnd = match.index + matchLength;
      if (line[matchEnd] === ".") {
        // suspected function call, highlight it if it is (we do it only if it's on the same line)
        const jsonpathMatch = line.substring(matchEnd).match(JsonPathFunctionRegex);
        if (jsonpathMatch) {
          // dot
          ts.tokens.push({
            line: lineNumber,
            char: matchEnd,
            length: 1,
            type: TokenType.NO_STYLE,
            modifier: TokenModifier.DECLARATION,
          });
          // function name
          ts.tokens.push({
            line: lineNumber,
            char: matchEnd + 1,
            length: jsonpathMatch[1].length,
            type: TokenType.FUNCTION,
            modifier: TokenModifier.DECLARATION,
          });
          // parenthesis open and possibly close (if simple function call)
          ts.tokens.push({
            line: lineNumber,
            char: matchEnd + 1 + jsonpathMatch[1].length,
            length: jsonpathMatch[0].length - jsonpathMatch[1].length - 1,
            type: TokenType.NO_STYLE,
            modifier: TokenModifier.DECLARATION,
          });
        }
      }
    }
  }

  // COMMENTS
  iter = line.matchAll(FindCommentsRegex);
  for (
    let iterResult = iter.next(), match: RegExpMatchArray | null | undefined = iterResult.value;
    !iterResult.done;
    iterResult = iter.next(), match = iterResult.value as RegExpMatchArray | null | undefined
  ) {
    if (!match || typeof match.index === "undefined") continue;
    ts.tokens.push({
      line: lineNumber,
      char: match.index,
      length: match[0].length,
      type: TokenType.COMMENT,
      modifier: TokenModifier.DECLARATION,
    });
  }
}
