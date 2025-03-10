import JSONGzip from "./JSONGzip";

export const SHARE_QS = "shared";
export const GZIP_MARKER = "gzip,";

export type Bundle = {
  i: any;
  d: any;
};

const BASE_URL = "/json-transform/playground";

export const shareLink = async (input: any, definition: any) => {
  const jsonValue = JSON.stringify({ i: input, d: definition });
  const compressed = await JSONGzip.compress(jsonValue, "base64url", "string");
  const encodedCompressedValue = encodeURIComponent(GZIP_MARKER + compressed);
  const encodedJSONValue = encodeURIComponent(jsonValue);
  const encodedValue =
    encodedJSONValue.length > encodedCompressedValue.length
      ? encodedCompressedValue // compressed is better
      : encodedJSONValue;

  return (typeof window !== "undefined" ? window.location.origin : "") + `${BASE_URL}#?${SHARE_QS}=` + encodedValue;
};

export const extractValue = (sharedValue: string) => {
  return sharedValue.startsWith(GZIP_MARKER)
    ? (JSONGzip.decompress(sharedValue.substring(GZIP_MARKER.length), "base64url") as Promise<Bundle>)
    : Promise.resolve(JSON.parse(sharedValue) as Bundle);
};
