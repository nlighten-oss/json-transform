const BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
// Base64 (The Base64 Alphabet)
const RFC2045 = BASE + "+/";
const lookup2045 = typeof Uint8Array === "undefined" ? [] : new Uint8Array(256);
for (let i = 0; i < RFC2045.length; i++) {
  lookup2045[RFC2045.charCodeAt(i)] = i;
}
// Base64 URL (URL and Filename safe Base64 Alphabet)
const RFC4648 = BASE + "-_";
const lookup4648 = typeof Uint8Array === "undefined" ? [] : new Uint8Array(256);
for (let i = 0; i < RFC4648.length; i++) {
  lookup4648[RFC4648.charCodeAt(i)] = i;
}

export const encode = (arraybuffer: ArrayBuffer, url?: boolean): string => {
  const chars = url ? RFC4648 : RFC2045;

  let bytes = new Uint8Array(arraybuffer),
    i,
    len = bytes.length,
    base64 = "";

  for (i = 0; i < len; i += 3) {
    base64 += chars[bytes[i] >> 2];
    base64 += chars[((bytes[i] & 3) << 4) | (bytes[i + 1] >> 4)];
    base64 += chars[((bytes[i + 1] & 15) << 2) | (bytes[i + 2] >> 6)];
    base64 += chars[bytes[i + 2] & 63];
  }

  if (len % 3 === 2) {
    base64 = base64.substring(0, base64.length - 1) + "=";
  } else if (len % 3 === 1) {
    base64 = base64.substring(0, base64.length - 2) + "==";
  }

  console.log(base64.length);
  return base64;
};

export const decode = (base64: string, url?: boolean): ArrayBuffer => {
  const lookup = url ? lookup4648 : lookup2045;
  let bufferLength = base64.length * 0.75,
    encoded1: number,
    encoded2: number,
    encoded3: number,
    encoded4: number;

  if (base64[base64.length - 1] === "=") {
    bufferLength--;
    if (base64[base64.length - 2] === "=") {
      bufferLength--;
    }
  }

  const arraybuffer = new ArrayBuffer(bufferLength),
    bytes = new Uint8Array(arraybuffer);

  for (let i = 0, p = 0, len = base64.length; i < len; i += 4) {
    encoded1 = lookup[base64.charCodeAt(i)];
    encoded2 = lookup[base64.charCodeAt(i + 1)];
    encoded3 = lookup[base64.charCodeAt(i + 2)];
    encoded4 = lookup[base64.charCodeAt(i + 3)];

    bytes[p++] = (encoded1 << 2) | (encoded2 >> 4);
    bytes[p++] = ((encoded2 & 15) << 4) | (encoded3 >> 2);
    bytes[p++] = ((encoded3 & 3) << 6) | (encoded4 & 63);
  }

  return arraybuffer;
};
