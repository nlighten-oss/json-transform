import { getAsString, isMap, isNullOrUndefined } from "../../JsonHelpers";
import { FormatSerializer } from "../FormatSerializer";
import { FormatDeserializer } from "../FormatDeserializer";

const MIN_SUPPLEMENTARY_CODE_POINT = 0x010000;
function charCount(codePoint: number) {
  return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT ? 2 : 1;
}

class CsvFormat implements FormatSerializer, FormatDeserializer {
  private static readonly COMMA = ",";
  private static readonly DEFAULT_SEPARATOR = CsvFormat.COMMA;
  private static readonly DOUBLE_QUOTES = '"';
  private static readonly EMBEDDED_DOUBLE_QUOTES = '""';
  private static readonly NEW_LINE_UNIX = "\n";
  private static readonly LINE_FEED = "\n".codePointAt(0);
  private static readonly CARRIAGE_RETURN = "\r".codePointAt(0);
  private static readonly NEW_LINE_WINDOWS = "\r\n";

  private readonly names?: string[];
  private readonly noHeaders: boolean;
  private readonly forceQuote: boolean;
  private readonly separator: string;

  constructor(
    names?: string[] | null,
    noHeaders?: boolean | null,
    forceQuote?: boolean | null,
    separator?: string | null,
  ) {
    this.names = names ?? undefined;
    this.noHeaders = isNullOrUndefined(noHeaders) ? false : noHeaders;
    this.forceQuote = isNullOrUndefined(forceQuote) ? false : forceQuote;
    this.separator = isNullOrUndefined(separator) ? CsvFormat.DEFAULT_SEPARATOR : separator;
  }

  private appendEscaped(sb: StringBuilder, val: any): void {
    let value: string;
    if (val === null || val === undefined) {
      value = "";
    } else {
      value = getAsString(val) ?? "";
    }
    if (
      this.forceQuote ||
      value.includes(CsvFormat.COMMA) ||
      value.includes(CsvFormat.DOUBLE_QUOTES) ||
      value.includes(CsvFormat.NEW_LINE_UNIX) ||
      value.includes(CsvFormat.NEW_LINE_WINDOWS) ||
      value.startsWith(" ") ||
      value.endsWith(" ")
    ) {
      sb.append(CsvFormat.DOUBLE_QUOTES);
      sb.append(value.replace(new RegExp(CsvFormat.DOUBLE_QUOTES, "g"), CsvFormat.EMBEDDED_DOUBLE_QUOTES));
      sb.append(CsvFormat.DOUBLE_QUOTES);
    } else {
      sb.append(value);
    }
  }

  private appendHeaders(sb: StringBuilder, headers: string[]): void {
    if (this.noHeaders) return;
    let first = true;
    for (const name of headers) {
      if (!first) {
        sb.append(this.separator);
      } else {
        first = false;
      }
      this.appendEscaped(sb, name);
    }
    sb.append("\n");
  }

  private appendRow(sb: StringBuilder, names: string[] | null | undefined, value: any): void {
    if (!Array.isArray(value) && names) {
      if (typeof value !== "object" || value === null) return;
      let first = true;
      for (const name of names) {
        if (!first) {
          sb.append(this.separator);
        } else {
          first = false;
        }
        this.appendEscaped(sb, value[name]);
      }
    } else {
      let first = true;
      for (const val of value) {
        if (!first) {
          sb.append(this.separator);
        } else {
          first = false;
        }
        this.appendEscaped(sb, val);
      }
    }
    sb.append("\n");
  }

  serialize(payload: any): string | null {
    const sb = new StringBuilder();
    let headers = this.names;
    if (headers) {
      this.appendHeaders(sb, headers);
    }

    if (Array.isArray(payload)) {
      if (!headers && payload.length > 0 && isMap(payload[0])) {
        headers = Object.keys(payload[0]);
        this.appendHeaders(sb, headers);
      }
      for (const x of payload) {
        this.appendRow(sb, headers, x);
      }
    } else {
      throw new Error("Unsupported object type to be formatted as CSV");
    }

    return sb.toString();
  }

  private accumulate(context: CsvParserContext, result: any[], values: any[]): void {
    if (result.length === 0 && !context.namesRead && !this.noHeaders) {
      context.names = values;
      context.namesRead = true;
      return;
    }
    if (this.noHeaders && isNullOrUndefined(this.names)) {
      result.push(values);
      return;
    }
    if (!isNullOrUndefined(context.names)) {
      const item: Record<string, any> = {};
      let i = 0;
      for (i = 0; i < context.names.length; i++) {
        const name = getAsString(context.names[i]) ?? "";
        if (
          (context.extractNames === null || Object.prototype.hasOwnProperty.call(context.extractNames, name)) &&
          values.length > i
        ) {
          item[name] = values[i];
        }
      }
      for (; i < values.length; i++) {
        if (!Object.prototype.hasOwnProperty.call(item, `$${i}`)) {
          item[`$${i}`] = values[i];
        }
      }
      result.push(item);
    }
  }

  deserialize(input: string | null): any {
    if (input === null) {
      return null;
    }
    const result: any[] = [];
    const context = new CsvParserContext();
    if (this.noHeaders && !isNullOrUndefined(this.names)) {
      const names: string[] = [];
      this.names.forEach(item => names.push(item));
      context.names = names;
    }
    context.extractNames = this.names ?? null;

    const len = input.length;
    let row: any[] = [];
    const cell = new StringBuilder();
    let offset = 0;

    while (offset < len) {
      const cur = input.codePointAt(offset) as number;
      const curSize = charCount(cur);
      const next = offset + curSize < len ? (input.codePointAt(offset + curSize) as number) : -1;
      const curAndNextSize = curSize + charCount(next);

      if (cur === this.separator.codePointAt(0)) {
        if (context.inQuotes) {
          cell.append(this.separator);
        } else {
          row.push(cell.toString());
          cell.clear();
        }

        offset += curSize;
      } else if ((cur === CsvFormat.CARRIAGE_RETURN && next === CsvFormat.LINE_FEED) || cur === CsvFormat.LINE_FEED) {
        const unix = cur === CsvFormat.LINE_FEED;
        const eof = offset + (unix ? curSize : curAndNextSize) === len;
        if (!eof) {
          if (context.inQuotes) {
            cell.append(unix ? CsvFormat.NEW_LINE_UNIX : CsvFormat.NEW_LINE_WINDOWS);
          } else {
            row.push(cell.toString());
            cell.clear();
            this.accumulate(context, result, row);
            row = [];
          }
        }
        offset += unix ? curSize : curAndNextSize;
      } else if (cur === 34 && next === 34) {
        if (context.inQuotes) {
          cell.append(CsvFormat.DOUBLE_QUOTES);
          offset += curAndNextSize;
        } else if (cell.length === 0) {
          context.inQuotes = !context.inQuotes;
          offset += curSize;
        } else {
          cell.append(CsvFormat.DOUBLE_QUOTES);
          offset += curSize;
        }
      } else if (cur === 34) {
        context.inQuotes = !context.inQuotes;
        offset += curSize;
      } else if (!context.inQuotes && (cur === 32 || cur === 9)) {
        offset += curSize;
      } else {
        cell.append(String.fromCodePoint(cur));
        offset += curSize;
      }
    }

    if (result.length || cell.length > 0) {
      row.push(cell.toString());
      this.accumulate(context, result, row);
    }
    return result as any;
  }
}

class CsvParserContext {
  public inQuotes = false;
  public names: string[] | null = null;
  public namesRead = false;
  public extractNames: string[] | null = null;
}

class StringBuilder {
  private strings: string[] = [];

  public append(str: string): void {
    this.strings.push(str);
  }

  public toString(): string {
    return this.strings.join("");
  }

  public clear(): void {
    this.strings.length = 0;
  }

  public get length(): number {
    return this.toString().length;
  }
}

export default CsvFormat;
