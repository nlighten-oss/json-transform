const VALID_ID_REGEXP = /^[a-z_$][a-z0-9_$]*$/i;

function toObjectFieldPath(key: string) {
  return VALID_ID_REGEXP.test(key) ? "." + key : "[" + JSON.stringify(key) + "]";
}

/**
 * Walks through the transformer object (DFS style) and calls the callback function with the path and value of each node.
 * @param transformer
 * @param callback
 * @param path
 */
export const walkTransformer = (transformer: any, callback: (path: string, value: any) => any, path = "$") => {
  if (typeof transformer !== "object" || transformer === null) {
    return callback(path, transformer);
  }
  if (Array.isArray(transformer)) {
    return transformer.forEach((value, index) => {
      walkTransformer(value, callback, `${path}[${index}]`);
    });
  }
  for (const key in transformer) {
    let valuePath = toObjectFieldPath(key);
    walkTransformer(transformer[key], callback, path + valuePath);
  }
};
