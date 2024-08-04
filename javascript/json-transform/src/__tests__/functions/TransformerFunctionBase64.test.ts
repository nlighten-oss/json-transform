import { describe, expect, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import TextEncoding from "../../functions/common/TextEncoding";

describe("TransformerFunctionBase64", () => {
  const t = "hello-world";
  const s = [t, t, t, t, t].join();
  const b = TextEncoding.encode(s);
  const b16 = TextEncoding.encode(s, "UTF-16");
  const b64 = Buffer.from(b).toString("base64");
  const b64_u16 = Buffer.from(b16).toString("base64");
  const b64_wp = b64.replace(/==?$/, "");
  const b64u = Buffer.from(b).toString("base64url") + "=";
  const b64u_wp = b64u.replace(/==?$/, "");
  const large = Buffer.from(TextEncoding.encode(s.repeat(5))).toString("base64");
  const b64m = large.substring(0, 76) + "\r\n" + b64.substring(76);
  const b64m_wp = b64m.replace(/==?$/, "");

  const tu = "hello-unicode-😀👨‍👩‍👧‍👦🪬";
  const tu_b64 = Buffer.from(TextEncoding.encode(tu)).toString("base64");
  const tu16_b64 = Buffer.from(TextEncoding.encode(tu, "UTF-16")).toString("base64");

  test("encode", async () => {
    await assertTransformation(s, "$$base64:$", b64);
    await assertTransformation(s, "$$base64():$", b64);
    await assertTransformation(s, "$$base64(encode):$", b64);
    await assertTransformation(s, "$$base64(enc):$", b64);
    await assertTransformation(s, "$$base64(e,Basic):$", b64);
    await assertTransformation(s, "$$base64(e,b):$", b64);
    expect(b64_u16.length > b64.length).toBeTruthy();
    await assertTransformation(s, "$$base64(ENCODE,BASIC,false,UTF-16):$", b64_u16);
    await assertTransformation(s, "$$base64(e,b,true):$", b64_wp);
    await assertTransformation(s, "$$base64(e,url):$", b64u);
    await assertTransformation(s, "$$base64(e,u):$", b64u);
    await assertTransformation(s, "$$base64(e,u,true):$", b64u_wp);
    await assertTransformation(s, "$$base64(E,Mime):$", b64m);
    await assertTransformation(s, "$$base64(e,m):$", b64m);
    await assertTransformation(s, "$$base64(e,m,true):$", b64m_wp);
  });

  test("encodeObject", async () => {
    await assertTransformation(s, { $$base64: "$" }, b64);
    await assertTransformation(s, { $$base64: "$", action: "encode" }, b64);
    await assertTransformation(s, { $$base64: "$", action: "enc" }, b64);
    await assertTransformation(s, { $$base64: "$", action: "e", rfc: "BASIC" }, b64);
    await assertTransformation(s, { $$base64: "$", action: "e", rfc: "B" }, b64);
    expect(b64_u16.length > b64.length).toBeTruthy();
    await assertTransformation(s, { $$base64: "$", charset: "UTF-16" }, b64_u16);
    await assertTransformation(s, { $$base64: "$", action: "e", rfc: "B", without_padding: true }, b64_wp);
    await assertTransformation(s, { $$base64: "$", action: "e", rfc: "url" }, b64u);
    await assertTransformation(s, { $$base64: "$", action: "e", rfc: "u" }, b64u);
    await assertTransformation(s, { $$base64: "$", action: "e", rfc: "U", without_padding: true }, b64u_wp);
    await assertTransformation(s, { $$base64: "$", action: "E", rfc: "Mime" }, b64m);
    await assertTransformation(s, { $$base64: "$", action: "E", rfc: "M" }, b64m);
    await assertTransformation(s, { $$base64: "$", action: "E", rfc: "M", without_padding: true }, b64m_wp);
  });

  test("decode", async () => {
    await assertTransformation(b64, "$$base64('Decode',b):$", s);
    await assertTransformation(b64, "$$base64(decode,b):$", s);
    await assertTransformation(b64_wp, "$$base64(d,b):$", s);
    await assertTransformation(b64u, "$$base64(d,u):$", s);
    await assertTransformation(b64u_wp, "$$base64(d,u):$", s);
    await assertTransformation(b64m, "$$base64(d,m):$", s);
    await assertTransformation(b64m_wp, "$$base64(d,m):$", s);
    await assertTransformation(b64m_wp, "$$base64(d,m):$", s);
    expect(tu16_b64.length > tu_b64.length).toBeTruthy();
    await assertTransformation(tu_b64, "$$base64(d,b,,UTF-8):$", tu);
    await assertTransformation(tu16_b64, "$$base64(d,b,,UTF-16):$", tu);
  });

  test("decodeObject", async () => {
    await assertTransformation(b64, { $$base64: "$", action: "DECODE" }, s);
    await assertTransformation(b64_wp, { $$base64: "$", action: "DECODE", rfc: "b" }, s);
    await assertTransformation(b64u, { $$base64: "$", action: "DECODE", rfc: "url" }, s);
    await assertTransformation(b64u_wp, { $$base64: "$", action: "DECODE", rfc: "u" }, s);
    await assertTransformation(b64m, { $$base64: "$", action: "DECODE", rfc: "mime" }, s);
    await assertTransformation(b64m_wp, { $$base64: "$", action: "DECODE", rfc: "m" }, s);
    expect(tu16_b64.length > tu_b64.length).toBeTruthy();
    await assertTransformation(tu_b64, { $$base64: "$", action: "DECODE", charset: "UTF-8" }, tu);
    await assertTransformation(tu16_b64, { $$base64: "$", action: "DECODE", charset: "UTF-16" }, tu);
  });
});
