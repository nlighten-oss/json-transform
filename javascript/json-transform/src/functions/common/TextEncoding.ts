const u8Encoder = new TextEncoder();
const u16Encoder = {
  encode: function (input: string) {
    const length = input.length;
    const u16 = new Uint16Array(length);
    for (let i = 0; i < length; i++) {
      u16[i] = input.charCodeAt(i);
    }
    return new Uint8Array(u16.buffer);
  },
};
const iso88591Encoder = {
  encode: function (input: string) {
    const u8 = new Uint8Array(input.length);
    for (let i = 0; i < input.length; i++) {
      u8[i] = input.charCodeAt(i) & 0xff;
    }
    return u8;
  }
}

const TextEncoding = {
  encode: function (input: string, charset: string | null = 'UTF-8') {
    if (charset === 'UTF-16') {
      return u16Encoder.encode(input);
    }
    if (charset === 'ISO-8859-1') {
      return iso88591Encoder.encode(input);
    }
    return u8Encoder.encode(input);
  },
  decode: function (input: Uint8Array, charset: string | null = 'UTF-8') {
    return new TextDecoder(charset?.toLowerCase()).decode(input);
  }
}

export default TextEncoding;