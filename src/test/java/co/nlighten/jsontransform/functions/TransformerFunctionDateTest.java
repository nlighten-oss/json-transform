package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TransformerFunctionDateTest extends BaseTest {

    Instant date = Instant.now();
    String now = date.toString();

    @Test
    void basic() {
        assertTransformation(now, "$$date:$", date.toString());
        assertTransformation(now, "$$date():$", date.toString());
        assertTransformation(now, "$$date(iso):$", date.toString());
        assertTransformation(now, "$$date(ISO,0):$", TransformerFunctionDate.ISO_INSTANT_0.format(date));
        assertTransformation(now, "$$date(iso,3):$", TransformerFunctionDate.ISO_INSTANT_3.format(date));
        assertTransformation(now, "$$date(ZONE,America/New_York):$", DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("America/New_York")).format(date));
        assertTransformation("2023-01-01T00:00:00Z", "$$date(ZONE,EST):$", "2022-12-31T19:00:00-05:00");
        assertTransformation(now, "$$date(GMT):$", DateTimeFormatter.RFC_1123_DATE_TIME.format(
                ZonedDateTime.ofInstant(date, ZoneId.of("GMT"))));
        assertTransformation(now, "$$date(date):$", date.toString().substring(0, 10));
        assertTransformation(now, "$$date('date'):$", date.toString().substring(0, 10));
    }

    @Test
    void format() {
        var formatted = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.of("UTC")).format(date);
        assertTransformation(now, "$$date(format,'dd.MM.yyyy'):$", formatted);
        var ins = "2023-01-01T00:00:00Z";
        assertTransformation(ins, "$$date(format,'yyyy-MM-dd HH:mm'):$", "2023-01-01 00:00");
        assertTransformation(ins, "$$date(format,'yyyy-MM-dd HH:mm',UTC):$", "2023-01-01 00:00");
        assertTransformation(ins, "$$date(format,'yyyy-MM-dd HH:mm','America/New_York'):$", "2022-12-31 19:00");
    }

    @Test
    void epoch() {
        assertTransformation(now, "$$date(epoch):$", BigDecimal.valueOf(date.getEpochSecond()));
        assertTransformation(now, "$$date(epoch,MS):$", BigDecimal.valueOf(date.toEpochMilli()));

        var result = transform(null, "$$date(epoch,ms):#now", null);
        Assertions.assertInstanceOf(BigDecimal.class, adapter.unwrap(adapter.type.cast(result), false));
    }

    @Test
    void timezoneError() {
        // based on a real scenario
        assertTransformation(" +01:00", "$$date(ZONE,$):2023-01-01T00:00:00Z", null);
        assertTransformation("+01:00", "$$date(ZONE,$):2023-01-01T00:00:00Z", "2023-01-01T01:00:00+01:00");
    }

    @Test
    void addSub() {
        var exactDate = Instant.parse("2020-12-31T00:00:00Z");
        var exactNow = exactDate.toString();

        assertTransformation(exactNow, "$$date(add):$", null);
        assertTransformation(exactNow, "$$date(ADD,MILLIS,100):$", Instant.parse("2020-12-31T00:00:00.100Z").toString());
        assertTransformation(exactNow, "$$date(add,SECONDS,59):$", Instant.parse("2020-12-31T00:00:59Z").toString());
        assertTransformation(exactNow, "$$date(add,MINUTES,59):$", Instant.parse("2020-12-31T00:59:00Z").toString());
        assertTransformation(exactNow, "$$date(add,HOURS,24):$", Instant.parse("2021-01-01T00:00:00Z").toString());
        assertTransformation(exactNow, "$$date(add, DAYS, 1):$", Instant.parse("2021-01-01T00:00:00Z").toString());
        assertTransformation(exactNow, "$$date(add,days , 1):$", Instant.parse("2021-01-01T00:00:00Z").toString());
        assertTransformation(exactNow, "$$date(add,MONTHS,1):$", Instant.parse("2021-01-31T00:00:00Z").toString());
        assertTransformation(exactNow, "$$date(add,YEARS,1):$", Instant.parse("2021-12-31T00:00:00Z").toString());
        assertTransformation(exactNow, "$$date(SUB,HOURS,23):$", Instant.parse("2020-12-30T01:00:00Z").toString());
        assertTransformation(exactNow, "$$date(sub,DAYS,1):$", Instant.parse("2020-12-30T00:00:00Z").toString());
        assertTransformation(exactNow, "$$date(sub,MONTHS,1):$", Instant.parse("2020-11-30T00:00:00Z").toString());
        assertTransformation(exactNow, "$$date(sub,YEARS,1):$", Instant.parse("2019-12-31T00:00:00Z").toString());
        // sub as add
        assertTransformation(exactNow, "$$date(add,DAYS,-1):$", Instant.parse("2020-12-30T00:00:00Z").toString());
        assertTransformation(exactNow, "$$date(add,YEARS,-1):$", Instant.parse("2019-12-31T00:00:00Z").toString());
        // args from vars
        var dateAndAmount = fromJson("""
{
  "date": "2020-12-31T00:00:00Z",
  "amount": 2,
  "unit": "HOURS"
}""");
        assertTransformation(dateAndAmount, "$$date(add,$.unit,'$.amount'):$.date", Instant.parse("2020-12-31T02:00:00Z").toString());
    }
}
