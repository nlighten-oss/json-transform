import { describe, expect, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";
import JsonTransformer from "../../JsonTransformer";
import { format } from "date-fns";
import { formatInTimeZone } from "date-fns-tz";

describe("TransformerFunctionDate", () => {
  const date = new Date();
  const now = date.toISOString();

  test("basic", async () => {
    await assertTransformation(now, "$$date:$", date.toISOString());
    await assertTransformation(now, "$$date():$", date.toISOString());
    await assertTransformation(now, "$$date(iso):$", date.toISOString());
    await assertTransformation(now, "$$date(ISO,0):$", date.toISOString().replace(/\.\d+/, ""));
    await assertTransformation(now, "$$date(iso,3):$", date.toISOString());
    await assertTransformation(
      now,
      "$$date(ZONE,America/New_York):$",
      formatInTimeZone(date, "America/New_York", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
    );
    await assertTransformation("2023-01-01T00:00:00Z", "$$date(ZONE,EST):$", "2022-12-31T19:00:00-05:00");
    await assertTransformation(now, "$$date(GMT):$", date.toUTCString());
    await assertTransformation(now, "$$date(date):$", date.toISOString().substring(0, 10));
    await assertTransformation(now, "$$date('date'):$", date.toISOString().substring(0, 10));
  });

  test("format", async () => {
    const formatted = format(date, "dd.MM.yyyy");
    await assertTransformation(now, "$$date(format,'dd.MM.yyyy'):$", formatted);
    const ins = "2023-01-01T00:00:00Z";
    await assertTransformation(ins, "$$date(format,'yyyy-MM-dd HH:mm'):$", "2023-01-01 00:00");
    await assertTransformation(ins, "$$date(format,'yyyy-MM-dd HH:mm',UTC):$", "2023-01-01 00:00");
    await assertTransformation(ins, "$$date(format,'yyyy-MM-dd HH:mm','America/New_York'):$", "2022-12-31 19:00");
  });

  test("epoch", async () => {
    await assertTransformation(now, "$$date(epoch):$", BigDecimal(Math.floor(date.getTime() / 1000)));
    await assertTransformation(now, "$$date(epoch,MS):$", BigDecimal(date.getTime()));

    const x = new JsonTransformer("$$date(epoch,ms):#now");
    const result = await x.transform();
    expect(result).toBeInstanceOf(BigDecimal);
  });

  test("timezoneError", async () => {
    // based on a real scenario
    await assertTransformation(" +01:00", "$$date(ZONE,$):2023-01-01T00:00:00Z", null);
    await assertTransformation("+01:00", "$$date(ZONE,$):2023-01-01T00:00:00Z", "2023-01-01T01:00:00+01:00");
  });

  test("addSub", async () => {
    const exactDate = new Date("2020-12-31T00:00:00Z");
    const exactNow = exactDate.toISOString();

    await assertTransformation(exactNow, "$$date(add):$", null);
    await assertTransformation(
      exactNow,
      "$$date(ADD,MILLIS,100):$",
      new Date("2020-12-31T00:00:00.100Z").toISOString(),
    );
    await assertTransformation(exactNow, "$$date(add,SECONDS,59):$", new Date("2020-12-31T00:00:59Z").toISOString());
    await assertTransformation(exactNow, "$$date(add,MINUTES,59):$", new Date("2020-12-31T00:59:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(add,HOURS,24):$", new Date("2021-01-01T00:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(add, DAYS, 1):$", new Date("2021-01-01T00:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(add,days , 1):$", new Date("2021-01-01T00:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(add,MONTHS,1):$", new Date("2021-01-31T00:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(add,YEARS,1):$", new Date("2021-12-31T00:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(SUB,HOURS,23):$", new Date("2020-12-30T01:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(sub,DAYS,1):$", new Date("2020-12-30T00:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(sub,MONTHS,1):$", new Date("2020-11-30T00:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(sub,YEARS,1):$", new Date("2019-12-31T00:00:00Z").toISOString());
    // sub as add
    await assertTransformation(exactNow, "$$date(add,DAYS,-1):$", new Date("2020-12-30T00:00:00Z").toISOString());
    await assertTransformation(exactNow, "$$date(add,YEARS,-1):$", new Date("2019-12-31T00:00:00Z").toISOString());
    // args from vars
    const dateAndAmount = {
      date: "2020-12-31T00:00:00Z",
      amount: 2,
      unit: "HOURS",
    };
    await assertTransformation(
      dateAndAmount,
      "$$date(add,$.unit,'$.amount'):$.date",
      new Date("2020-12-31T02:00:00Z").toISOString(),
    );
  });
});
