package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/*
 * For tests
 * @see TransformerFunctionDateTest
 */
@Aliases("date")
@Documentation(value = "Date formatting utility", notes = """
    $$date(ISO,[digits]) - ISO-8601 with specified precision, default is max
    $$date(GMT) - rfc-1123 date format
    $$date(DATE) - only the date part of ISO-8601
    $$date(ADD,{units},{amount}) - add an amount by chronological unit
    $$date(SUB,{units},{amount}) - subtract an amount by chronological unit
    $$date(EPOCH,[resolution])
    $$date(FORMAT,{pattern},[timezone])
    $$date(ZONE,{zone}) - ISO-8601 with offset by specifying a timezone""")
@InputType(ArgType.String)
@ArgumentType(value = "format", type = ArgType.Enum, position = 0, defaultEnum = "ISO",
              enumValues = {"ISO","GMT","DATE","ADD","SUB","EPOCH","FORMAT","ZONE"},
              description = "Formatter to use")
@ArgumentType(value = "digits", type = ArgType.Integer, position = 1, defaultInteger = -1,
              description = "(ISO) precision for time part (scale) 0|3|6|9|-1")
@ArgumentType(value = "units", type = ArgType.Enum, position = 1, required = true,
              enumValues = {"NANOS", "MICROS", "MILLIS", "SECONDS", "MINUTES", "HOURS", "HALF_DAYS", "DAYS", "MONTHS", "YEARS"},
              description = "(ADD/SUB) Units to use (ChronoUnit)")
@ArgumentType(value = "amount", type = ArgType.Long, position = 2, defaultLong = 0L,
              description = "(ADD/SUB) Amount of units to add or subtract (can be negative)")
@ArgumentType(value = "resolution", type = ArgType.Enum, position = 1, defaultEnum = "UNIX",
              enumValues = { "UNIX", "MS"},
              description = "(EPOCH) Resolution of epoch (Seconds or Milliseconds)")
@ArgumentType(value = "pattern", type = ArgType.String, position = 1, required = true,
              description = "(FORMAT) Pattern to use")
@ArgumentType(value = "timezone", type = ArgType.String, position = 2, defaultString = "UTC",
              description = "(FORMAT)")
@ArgumentType(value = "zone", type = ArgType.String, position = 1, defaultString = "UTC",
              description = "(ZONE)")
@OutputType(value = {ArgType.String, ArgType.Integer})
public class TransformerFunctionDate<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public static final DateTimeFormatter ISO_INSTANT_0 = new DateTimeFormatterBuilder().appendInstant(0).toFormatter();
    public static final DateTimeFormatter ISO_INSTANT_3 = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
    public static final DateTimeFormatter ISO_INSTANT_6 = new DateTimeFormatterBuilder().appendInstant(6).toFormatter();
    public static final DateTimeFormatter ISO_INSTANT_9 = new DateTimeFormatterBuilder().appendInstant(9).toFormatter();

    public TransformerFunctionDate(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    static final DateTimeFormatter ISO_DATE = new DateTimeFormatterBuilder()
            // date and offset
            .append(DateTimeFormatter.ISO_DATE)
            // default values for hour and minute
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    static final DateTimeFormatter ISO_TIME = new DateTimeFormatterBuilder()
            // date and offset
            .append(DateTimeFormatter.ISO_TIME)
            // default values for hour and minute
            .parseDefaulting(ChronoField.YEAR, 1970)
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .toFormatter().withZone(ZoneId.of("UTC"));

    static Instant parseInstant(Object value) {
        if (value instanceof Instant i)
            return i;
        if (value instanceof Date d)
            return d.toInstant();
        if (value instanceof String s) {
            if (s.contains("T"))
                return DateTimeFormatter.ISO_INSTANT.parse(s, Instant::from);
            else if (s.contains(":"))
                return ISO_TIME.parse(s, Instant::from);
            else if (s.contains("-"))
                return ISO_DATE.parse(s, Instant::from);
            value = Long.parseLong(s);
        }
        if (value instanceof Number n) {
            if (n.longValue() < 2671726769L)
                return Instant.ofEpochSecond(n.longValue());
            return Instant.ofEpochMilli(n.longValue());
        }
        return Instant.parse(value.toString());
    }

    static Instant calendarAddToDate(Instant instant, int field, int amount) {
        var c = Calendar.getInstance();
        c.setTime(Date.from(instant));
        c.add(field, amount);
        return c.getTime().toInstant();
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var unwrapped = context.getUnwrapped(null);
        if (unwrapped == null) {
            return null;
        }
        var instant = parseInstant(unwrapped);
        return switch (context.getEnum("format")) {
            case "ISO" -> switch (context.getInteger("digits")) {
                case 0 -> ISO_INSTANT_0.format(instant); // no second fractions
                case 3 -> ISO_INSTANT_3.format(instant); // milliseconds
                case 6 -> ISO_INSTANT_6.format(instant); // microseconds
                case 9 -> ISO_INSTANT_9.format(instant); // nanoseconds
                default -> DateTimeFormatter.ISO_INSTANT.format(instant);
            };
            case "GMT" -> DateTimeFormatter.RFC_1123_DATE_TIME.format(
                    ZonedDateTime.ofInstant(instant, ZoneId.of("GMT")));
            case "ADD" -> switch (ChronoUnit.valueOf(context.getEnum("units"))) {
                case NANOS, MICROS, MILLIS, SECONDS, MINUTES, HOURS, HALF_DAYS, DAYS ->
                        DateTimeFormatter.ISO_INSTANT.format(instant.plus(context.getLong("amount"), ChronoUnit.valueOf(context.getEnum("units"))));
                case MONTHS -> DateTimeFormatter.ISO_INSTANT.format(calendarAddToDate(instant, Calendar.MONTH, context.getInteger("amount")));
                case YEARS -> DateTimeFormatter.ISO_INSTANT.format(calendarAddToDate(instant, Calendar.YEAR, context.getInteger("amount")));
                default -> DateTimeFormatter.ISO_INSTANT.format(instant);
            };
            case "SUB" -> switch (ChronoUnit.valueOf(context.getEnum("units"))) {
                case NANOS, MICROS, MILLIS, SECONDS, MINUTES, HOURS, HALF_DAYS, DAYS ->
                        DateTimeFormatter.ISO_INSTANT.format(instant.minus(context.getLong("amount"), ChronoUnit.valueOf(context.getEnum("units"))));
                case MONTHS -> DateTimeFormatter.ISO_INSTANT.format(calendarAddToDate(instant, Calendar.MONTH, -context.getInteger("amount")));
                case YEARS -> DateTimeFormatter.ISO_INSTANT.format(calendarAddToDate(instant, Calendar.YEAR, -context.getInteger("amount")));
                default -> DateTimeFormatter.ISO_INSTANT.format(instant);
            };
            case "DATE" -> DateTimeFormatter.ISO_INSTANT.format(instant).substring(0, 10);
            case "EPOCH" -> switch (context.getEnum("resolution")) {
                case "MS" -> BigDecimal.valueOf(instant.toEpochMilli());
                default -> BigDecimal.valueOf(instant.getEpochSecond());
            };
            case "FORMAT" -> DateTimeFormatter.ofPattern(context.getString("pattern"))
                    .withZone(ZoneId.of(context.getString("timezone"), ZoneId.SHORT_IDS))
                    .format(instant);
            case "ZONE" -> DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    .withZone(ZoneId.of(context.getString("zone"), ZoneId.SHORT_IDS))
                    .format(instant);
            default -> null;
        };
    }
}
