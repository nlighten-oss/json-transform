import { isNullOrUndefined } from "../../JsonHelpers";

function javaSplit(str: string, delimiter: string, limit?: number | null) {
  const delimiterRe = new RegExp(delimiter, "g");

  const result: string[] = [];
  let iter: IterableIterator<RegExpMatchArray> = str.matchAll(delimiterRe);
  let lastMatchIndex = 0;
  for (
    let iterResult = iter.next(), match: RegExpMatchArray | null = iterResult.value;
    !iterResult.done;
    iterResult = iter.next(), match = iterResult.value as RegExpMatchArray | null
  ) {
    if (!match || typeof match.index === "undefined" || (match.index === 0 && match[0].length === 0)) continue;
    if (!isNullOrUndefined(limit) && limit > 0 && result.length === limit - 1) {
      break;
    }
    result.push(str.substring(lastMatchIndex, match.index));
    lastMatchIndex = match.index + match[0].length;
  }
  result.push(str.substring(lastMatchIndex));

  if (isNullOrUndefined(limit) || limit === 0) {
    while (result.at(-1) === "") {
      result.pop();
    }
  }

  return result;
}

export default javaSplit;
