import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionTrim extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        type: { type: ArgType.Enum, position: 0, defaultEnum: "BOTH" },
      },
    });
  }

  static isJavaWhitespace(codePoint: number): boolean {
    return codePoint <= 0x0020;
  }

  /**
   * Java definition of White Space:
   * @param codePoint
   */
  static isWhitespace(codePoint: number): boolean {
    switch (codePoint) {
      case 0x0009: // '\t' HORIZONTAL TABULATION
      case 0x000a: // '\n' LINE FEED
      case 0x000b: // '\u000B' VERTICAL TABULATION
      case 0x000c: // '\f' FORM FEED
      case 0x000d: // '\r' CARRIAGE RETURN
      case 0x001c: // '\u001C' FILE SEPARATOR
      case 0x001d: // '\u001D' GROUP SEPARATOR
      case 0x001e: // '\u001E' RECORD SEPARATOR
      case 0x001f: // '\u001F' UNIT SEPARATOR
      case 0x0020: // ' ' SPACE
      case 0x2007: // '\u2007' FIGURE SPACE
      case 0x2028: // '\u2028' LINE SEPARATOR
      case 0x2029: // '\u2029' PARAGRAPH SEPARATOR
      case 0x202f: // '\u202F' NARROW NO-BREAK SPACE
        return true;
      default:
        return false;
    }
  }

  static indexOfJavaNonWhitespace(str: string) {
    for (let i = 0; i < str.length; i++) {
      if (!TransformerFunctionTrim.isJavaWhitespace(str.codePointAt(i) ?? 0)) {
        return i;
      }
    }
    return str.length;
  }

  static lastIndexOfJavaNonWhitespace(str: string) {
    for (let i = str.length - 1; i >= 0; i--) {
      if (!TransformerFunctionTrim.isJavaWhitespace(str.codePointAt(i) ?? 0)) {
        return i + 1;
      }
    }
    return 0;
  }

  static indexOfNonWhitespace(str: string) {
    for (let i = 0; i < str.length; i++) {
      if (!TransformerFunctionTrim.isWhitespace(str.codePointAt(i) ?? 0)) {
        return i;
      }
    }
    return str.length;
  }

  static lastIndexOfNonWhitespace(str: string) {
    for (let i = str.length - 1; i >= 0; i--) {
      if (!TransformerFunctionTrim.isWhitespace(str.codePointAt(i) ?? 0)) {
        return i + 1;
      }
    }
    return 0;
  }

  static outdent(lines: string[]) {
    let outdent = Infinity;
    for (const line of lines) {
      let leadingWhitespace = TransformerFunctionTrim.indexOfNonWhitespace(line);
      if (leadingWhitespace !== line.length) {
        outdent = Math.min(outdent, leadingWhitespace);
      }
    }
    const lastLine = lines.at(-1);
    if (lastLine === "") {
      outdent = Math.min(outdent, lastLine.length);
    }
    return outdent;
  }

  static stripIndent(str: string) {
    let length = str.length;
    if (length == 0) {
      return "";
    }
    const lastChar = str.charAt(length - 1);
    const optOut = lastChar === "\n" || lastChar === "\r";
    const lines: string[] = str.split(/[\n\r]/g);
    const outdent = optOut ? 0 : TransformerFunctionTrim.outdent(lines);
    return (
      lines
        .map(line => {
          let firstNonWhitespace = TransformerFunctionTrim.indexOfNonWhitespace(line);
          let lastNonWhitespace = TransformerFunctionTrim.lastIndexOfNonWhitespace(line);
          let incidentalWhitespace = Math.min(outdent, firstNonWhitespace);
          return firstNonWhitespace > lastNonWhitespace ? "" : line.substring(incidentalWhitespace, lastNonWhitespace);
        })
        .join("\n") + (optOut ? "\n" : "")
    );
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    switch (await context.getEnum("type")) {
      case "START": {
        const index = TransformerFunctionTrim.indexOfNonWhitespace(str);
        return str.substring(index);
      }
      case "END": {
        const index = TransformerFunctionTrim.lastIndexOfNonWhitespace(str);
        return str.substring(0, index);
      }
      case "INDENT":
        return TransformerFunctionTrim.stripIndent(str);
      case "JAVA": {
        const firstIndex = TransformerFunctionTrim.indexOfJavaNonWhitespace(str);
        const lastIndex = TransformerFunctionTrim.lastIndexOfJavaNonWhitespace(str);
        return str.substring(firstIndex, lastIndex);
      }
      default: {
        const firstIndex = TransformerFunctionTrim.indexOfNonWhitespace(str);
        const lastIndex = TransformerFunctionTrim.lastIndexOfNonWhitespace(str);
        return str.substring(firstIndex, lastIndex);
      }
    }
  }
}

export default TransformerFunctionTrim;
