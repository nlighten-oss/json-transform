import { decode, encode } from "./Base64";

class JSONGzip {
  public static async compress(
    value: string,
    format: "base64" | "base64url" = "base64",
    valueType: "string" | "json" = "json",
  ) {
    const input = valueType === "string" ? value : JSON.stringify(value);
    const compressionStream = new Blob([input], { type: "application/json" })
      .stream()
      .pipeThrough(new CompressionStream("gzip"));
    return new Response(compressionStream).arrayBuffer().then(buffer => encode(buffer, format === "base64url"));
  }

  public static async decompress(compressed: string, format: "base64" | "base64url" = "base64") {
    const buffer = decode(compressed, format === "base64url");
    const decompressionStream = new Blob([buffer], { type: "application/json" })
      .stream()
      .pipeThrough(new DecompressionStream("gzip"));
    return new Response(decompressionStream)
      .blob()
      .then(blob => blob.text())
      .then(text => JSON.parse(text));
  }
}

export default JSONGzip;
