export const VALID_ID_REGEXP = /^[a-z_$][a-z0-9_$]*$/i;

export function toObjectFieldPath(key: string) {
  return VALID_ID_REGEXP.test(key) ? "." + key : "[" + JSON.stringify(key) + "]";
}

export const jsonpathJoin = (...args: (string | null | undefined)[]): string => {
  return args.reduce<string>((a, c) => {
    if (!a) return c ?? "";
    if (!c) return a;
    return a + (c[0] === "[" ? "" : ".") + c;
  }, "");
};
