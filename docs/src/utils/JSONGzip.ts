const Base64Url = {
  from(b64: string) {
    return b64.replace(/=+/g, "").replace(/\+/g, "-").replace(/\//g, "_");
  },
  toBase64(b64Url: string) {
    return b64Url.replace(/-/g, "+").replace(/_/g, "/");
  },
};

const blobToBase64 = (blob: Blob): Promise<string> => {
  const reader = new FileReader();
  reader.readAsDataURL(blob);
  return new Promise(resolve => {
    reader.onloadend = () => {
      const dataUrl = reader.result as string;
      resolve(dataUrl.substring(dataUrl.indexOf(";") + 1).replace(/^base64,/, ""));
    };
  });
};

const base64ToUint8Array = (base64: string) => {
  base64 = base64.replace(/[\s\t\n]/g, "");
  if (base64.length % 4 !== 0) {
    base64 = base64 + "=".repeat(4 - (base64.length % 4));
  }
  const bytesString = atob(base64);
  const length = bytesString.length;
  const bytes = new Uint8Array(length);
  for (let i = 0; i < length; i++) {
    bytes[i] = bytesString.charCodeAt(i);
  }
  return bytes;
};

class JSONGzip {
  public static async compress(
    value: string,
    format: "base64" | "base64url" = "base64",
    valueType: "string" | "json" = "json",
  ) {
    const stream = new Blob([valueType === "string" ? value : JSON.stringify(value)], {
      type: "application/json",
    }).stream();
    const compressedStream = stream.pipeThrough(new CompressionStream("gzip"));
    const response = new Response(compressedStream);
    const blob = await response.blob();
    const base64 = await blobToBase64(blob);
    // const buffer = await response.blob().then(blob => blob.arrayBuffer());
    // const base64 = btoa(String.fromCharCode(...new Uint8Array(buffer)));
    if (format === "base64url") {
      return Base64Url.from(base64);
    }
    return base64;
  }

  public static async decompress(compressed: string, format: "base64" | "base64url" = "base64") {
    const base64Input = format === "base64url" ? Base64Url.toBase64(compressed) : compressed;
    const buffer = base64ToUint8Array(base64Input);
    const stream = new Blob([buffer], { type: "text/plain" }).stream();
    const decompressedStream = stream.pipeThrough(new DecompressionStream("gzip"));
    const response = new Response(decompressedStream);
    return response
      .blob()
      .then(blob => blob.text())
      .then(text => JSON.parse(text));
  }
}

export default JSONGzip;
