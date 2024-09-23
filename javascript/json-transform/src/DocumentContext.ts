import jp from "jsonpath";

function isIndefinitePath(path: string) {
  const parsed = jp.parse(path);
  return parsed.some((x: any) => {
    // deep scan (..)
    if (
      x.scope === "descendant" ||
      // wildcard
      x.expression.type === "wildcard" ||
      x.expression.type === "slice" ||
      // filter expression
      x.expression.type === "filter_expression" ||
      (x.expression.type === "union" &&
        Array.isArray(x.expression.value) &&
        x.expression.value.every((ex: any) => ex.expression.type === "numeric_literal"))
    ) {
      return true;
    }

    return false;
  });
}

class DocumentContext {
  private value: any;
  constructor(value: any) {
    this.value = value;
  }
  read(expression: string) {
    if (expression === "$") return this.value;
    const result = jp.query(this.value, expression);
    if (isIndefinitePath(expression)) {
      return result ?? [];
    }
    if (result.length === 0) {
      return null;
    }
    return result[0];
  }
}

export default DocumentContext;
