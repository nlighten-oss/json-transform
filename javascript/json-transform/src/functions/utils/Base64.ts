class Base64 {
  static readonly BASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  static readonly ALPHABET_URL = Base64.BASE_ALPHABET + "-_";
  static readonly ALPHABET = Base64.BASE_ALPHABET + "+/";
  static readonly MIME_CLEAN = /[^a-zA-Z0-9+/]+/g;
  static readonly MIMELINEMAX = 76;
  static readonly MIME_CRLF = "\r\n";
  static readonly VALIDATE_BASE64 = /^[a-zA-Z0-9+/]+={0,2}$/;
  static readonly VALIDATE_BASE64_URL = /^[a-zA-Z0-9\-_]+={0,2}$/;

  public static encode(input: Uint8Array, type?: "basic" | "url" | "mime", withoutPadding?: boolean | null) {
    const alphabet = type === "url" ? Base64.ALPHABET_URL : Base64.ALPHABET;
    let output = "";
    let chr1;
    let chr2;
    let chr3;
    let i = 0;
    while (i < input.byteLength) {
      chr1 = input[i++];
      chr2 = input[i++];
      chr3 = input[i++];

      // encode 4 character group
      output += alphabet.charAt(chr1 >> 2);
      if (type === "mime" && output.length % Base64.MIMELINEMAX === 0) {
        output += Base64.MIME_CRLF;
      }
      output += alphabet.charAt(((chr1 & 3) << 4) | (chr2 >> 4));
      if (type === "mime" && output.length % Base64.MIMELINEMAX === 0) {
        output += Base64.MIME_CRLF;
      }
      if (!isNaN(chr2)) {
        output += alphabet.charAt(((chr2 & 15) << 2) | (chr3 >> 6));
        if (type === "mime" && output.length % Base64.MIMELINEMAX === 0) {
          output += Base64.MIME_CRLF;
        }
        if (!isNaN(chr3)) {
          output += alphabet.charAt(chr3 & 63);
          if (type === "mime" && output.length % Base64.MIMELINEMAX === 0) {
            output += Base64.MIME_CRLF;
          }
        }
      }
    }
    if (!withoutPadding) {
      output += "==".substring(0, (3 - (input.byteLength % 3)) % 3);
    }
    return output;
  }

  public static decode(input: string, type?: "basic" | "url" | "mime"): Uint8Array {
    if (type === "mime") {
      input = input.replace(Base64.MIME_CLEAN, "");
    }
    let length = input.length;
    if (length % 4 == 0) {
      input = input.replace(/==?$/, "");
      length = input.length;
    }
    if (
      length % 4 == 1 ||
      !(type === "url" ? Base64.VALIDATE_BASE64_URL.test(input) : Base64.VALIDATE_BASE64.test(input))
    ) {
      throw new Error("Invalid Base64 string");
    }
    const alphabet = type === "url" ? Base64.ALPHABET_URL : Base64.ALPHABET;

    const output = new Uint8Array((length * 3) / 4);
    let chr1: number, chr2: number, chr3: number;
    let enc1: number, enc2: number, enc3: number, enc4: number;
    let i = 0,
      j = 0;

    while (i < input.length) {
      enc1 = alphabet.indexOf(input.charAt(i++));
      enc2 = alphabet.indexOf(input.charAt(i++));
      enc3 = alphabet.indexOf(input.charAt(i++));
      enc4 = alphabet.indexOf(input.charAt(i++));

      chr1 = (enc1 << 2) | (enc2 >> 4);
      chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
      chr3 = ((enc3 & 3) << 6) | enc4;

      output[j] = chr1;
      j++;
      if (enc3 != 64) {
        output[j] = chr2;
        j++;
      }
      if (enc4 != 64) {
        output[j] = chr3;
        j++;
      }
    }

    return output;
  }
}
export default Base64;
