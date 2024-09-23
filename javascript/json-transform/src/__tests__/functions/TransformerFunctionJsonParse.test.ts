import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal, JSONBig } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionJsonParse", () => {
  test("fromString", async () => {
    const strVal = "text";
    await assertTransformation(JSON.stringify(strVal), "$$jsonparse:$", strVal);
    await assertTransformation(JSON.stringify(strVal), "$$jsonparse():$", strVal);
  });
  test("fromBoolean", async () => {
    const boolVal = true;
    await assertTransformation(JSON.stringify(boolVal), "$$jsonparse:$", boolVal);
  });
  test("fromNumber", async () => {
    const numVal = 123;
    const str = JSONBig.stringify(numVal);
    await assertTransformation(str, "$$jsonparse:$", JSONBig.parse(str));
  });

  test("fromBigDecimal", async () => {
    const numVal = BigDecimal("1234567890.098765432123456789");
    const numStr = JSONBig.stringify(numVal);
    await assertTransformation(numStr, "$$jsonparse:$", JSONBig.parse(numStr));
    const bigVal = BigDecimal("123456789123456789123456789123456789");
    const bigStr = JSONBig.stringify(bigVal);
    await assertTransformation(bigStr, "$$jsonparse:$", JSONBig.parse(bigStr));
  });

  test("fromObject", async () => {
    const jsonString = '{"a":"b"}';
    const jsonAsObj = JSON.parse(jsonString);
    await assertTransformation(jsonString, "$$jsonparse:$", jsonAsObj);
  });
  test("fromArray", async () => {
    const jsonString = '["a","b"]';
    const jsonAsObj = JSON.parse(jsonString);
    await assertTransformation(jsonString, "$$jsonparse:$", jsonAsObj);
  });

  test("objectFromObject", async () => {
    const jsonString = '{"a":"b"}';
    const jsonAsObj = JSON.parse(jsonString);
    await assertTransformation(
      jsonString,
      {
        $$jsonparse: "$",
      },
      jsonAsObj,
    );
  });
});
