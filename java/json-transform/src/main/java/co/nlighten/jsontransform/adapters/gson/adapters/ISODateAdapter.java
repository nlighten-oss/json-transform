package co.nlighten.jsontransform.adapters.gson.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class ISODateAdapter extends TypeAdapter<Date> {

    final ThreadLocal<DateFormat> threadLocalSimpleDateFormat;

    public ISODateAdapter() {
        threadLocalSimpleDateFormat = ThreadLocal.withInitial(() -> {
            var smt = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
            smt.setTimeZone(TimeZone.getTimeZone("UTC"));
            return smt;
        });



    }

    public static String formatMillis(Date start, Date end) {
        return formatMillis(end.getTime() - start.getTime());
    }

    public static String formatMillis(long val) {
        StringBuilder buf = new StringBuilder(20);
        String sgn = "";

        if (val < 0) {
            sgn = "-";
            val = Math.abs(val);
        }

        append(buf, sgn, 0, (val / 3600000));
        val %= 3600000;
        append(buf, ":", 2, (val / 60000));
        val %= 60000;
        append(buf, ":", 2, (val / 1000));
        val %= 1000;
        append(buf, ".", 3, (val));
        return buf.toString();
    }

    /**
     * Append a right-aligned and zero-padded numeric value to a `StringBuilder`.
     */
    private static void append(StringBuilder tgt, String pfx, int dgt, long val) {
        tgt.append(pfx);
        if (dgt > 1) {
            int pad = (dgt - 1);
            for (long xa = val; xa > 9 && pad > 0; xa /= 10) {
                pad--;
            }
            tgt.append("0".repeat(Math.max(0, pad)));
        }
        tgt.append(val);
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(threadLocalSimpleDateFormat.get().format(value));
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in != null && in.hasNext()) {
            var peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (peek == JsonToken.BEGIN_OBJECT) {

                in.beginObject();
                while (in.peek() != JsonToken.END_OBJECT) {
                    if (Objects.equals(in.nextName(), "$date")) {
                        String val = in.nextString();
                        try {
                            return threadLocalSimpleDateFormat.get().parse(val);
                        } catch (ParseException e) {
                            var epoch = Long.parseLong(val);
                            return new Date(epoch);
                        } finally {
                            //move to end
                            while (in.peek() != JsonToken.END_OBJECT) {
                                in.nextName();
                                in.skipValue();
                            }
                            in.endObject();
                        }
                    } else {
                        in.skipValue();
                    }
                }

                in.endObject();
                return null;
            }
            String val = in.nextString();
            try{
                return Date.from(parseInstant(val));
            }catch (Throwable t){
            try {
                return threadLocalSimpleDateFormat.get().parse(val);
            } catch (ParseException e) {
                var epoch = Long.parseLong(val);
                return new Date(epoch);
            }}
        } else {
            return null;
        }
    }



    //--------
    static Instant parseInstant(Object value) {
        if (value instanceof Instant i)
            return i;
        if (value instanceof Date d)
            return d.toInstant();
        if (value instanceof String s) {
            if (s.contains("T"))
                return DateTimeFormatter.ISO_INSTANT.parse(s, Instant::from);
            else if (s.contains(":"))
                return ISO_TIME.get().parse(s, Instant::from);
            else if (s.contains("-"))
                return ISO_DATE.get().parse(s, Instant::from);
            value = Long.parseLong(s);
        }
        if (value instanceof Number n) {
            if (n.longValue() < 2671726769L)
                return Instant.ofEpochSecond(n.longValue());
            return Instant.ofEpochMilli(n.longValue());
        }
        return Instant.parse(value.toString());
    }

    static final ThreadLocal<DateTimeFormatter> ISO_DATE = ThreadLocal.withInitial(()->new DateTimeFormatterBuilder()
            // date and offset
            .append(DateTimeFormatter.ISO_DATE)
            // default values for hour and minute
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter()
            .withZone(ZoneId.of("UTC")));

    static final ThreadLocal<DateTimeFormatter> ISO_TIME = ThreadLocal.withInitial(()->new DateTimeFormatterBuilder()
            // date and offset
            .append(DateTimeFormatter.ISO_TIME)
            // default values for hour and minute
            .parseDefaulting(ChronoField.YEAR, 1970)
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .toFormatter().withZone(ZoneId.of("UTC")));
}
