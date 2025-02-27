import JSONGzip from "./JSONGzip";

export const SHARE_QS = "shared";
export const GZIP_MARKER = "gzip,";

export type Bundle = {
  i: any;
  d: any;
}

const BASE_URL = "/json-transform/"

export const shareLink = async (input: any, definition: any) => {
  const bundleString = JSON.stringify({i: input, d: definition});
  const compressed = await JSONGzip.compress(bundleString, "base64url", "string");
  const encodedInput = encodeURIComponent(bundleString);
  const value = encodedInput.length > compressed.length + GZIP_MARKER.length
    ? GZIP_MARKER + compressed // compressed is better
    : encodedInput;

  return window.location.origin + `${BASE_URL}playground#?${SHARE_QS}=` + value;
}

export const extractValue = (sharedValue: string) => {
  return sharedValue.startsWith(GZIP_MARKER)
    ? JSONGzip.decompress(sharedValue.substring(GZIP_MARKER.length), "base64url") as Promise<Bundle>
    : Promise.resolve(JSON.parse(sharedValue) as Bundle)
}