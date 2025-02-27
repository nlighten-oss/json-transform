package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class TransformerFunctionDate extends TransformerFunction {
    public static final DateTimeFormatter ISO_INSTANT_0 = new DateTimeFormatterBuilder().appendInstant(0).toFormatter();
    public static final DateTimeFormatter ISO_INSTANT_3 = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
    public static final DateTimeFormatter ISO_INSTANT_6 = new DateTimeFormatterBuilder().appendInstant(6).toFormatter();
    public static final DateTimeFormatter ISO_INSTANT_9 = new DateTimeFormatterBuilder().appendInstant(9).toFormatter();

    public TransformerFunctionDate() {
        super(FunctionDescription.of(
            Map.of(
            "format", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("ISO"),
            "digits", ArgumentType.of(ArgType.Integer).position(1).defaultInteger(-1),
            "units", ArgumentType.of(ArgType.Enum).position(1),
            "amount", ArgumentType.of(ArgType.Long).position(2).defaultLong(0L),
            "resolution", ArgumentType.of(ArgType.Enum).position(1).defaultEnum("UNIX"),
            "pattern", ArgumentType.of(ArgType.String).position(1),
            "timezone", ArgumentType.of(ArgType.String).position(2).defaultString("UTC"),
            "zone", ArgumentType.of(ArgType.String).position(1).defaultString("UTC"),
            "end", ArgumentType.of(ArgType.String).position(2)
            )
        ));
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
    public Object apply(FunctionContext context) {
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
            case "DIFF" -> {
                var end = parseInstant(context.getUnwrapped("end"));
                var units = ChronoUnit.valueOf(context.getEnum("units"));
                if (ChronoUnit.MONTHS.equals(units)) {
                    var endLocalDate = LocalDate.ofInstant(end, ZoneId.of("UTC"));
                    var startLocalDate = LocalDate.ofInstant(instant, ZoneId.of("UTC"));
                    yield BigDecimal.valueOf(Period.between(startLocalDate, endLocalDate).toTotalMonths());
                } else if (ChronoUnit.YEARS.equals(units)) {
                    var endLocalDate = LocalDate.ofInstant(end, ZoneId.of("UTC"));
                    var startLocalDate = LocalDate.ofInstant(instant, ZoneId.of("UTC"));
                    yield BigDecimal.valueOf(Period.between(startLocalDate, endLocalDate).getYears());
                } else {
                    yield BigDecimal.valueOf(instant.until(end, units));
                }
            }
            case "EPOCH" -> switch (context.getEnum("resolution")) {
                case "MS" -> BigDecimal.valueOf(instant.toEpochMilli());
                default -> BigDecimal.valueOf(instant.getEpochSecond());
            };
            case "FORMAT" -> DateTimeFormatter.ofPattern(context.getString("pattern"))
                    .withZone(ZoneId.of(context.getString("timezone").trim(), ZoneId.SHORT_IDS))
                    .format(instant);
            case "ZONE" -> DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    .withZone(ZoneId.of(context.getString("zone").trim(), ZoneId.SHORT_IDS))
                    .format(instant);
            default -> null;
        };
    }
}
