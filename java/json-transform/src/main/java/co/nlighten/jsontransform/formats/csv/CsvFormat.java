package co.nlighten.jsontransform.formats.csv;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.formats.FormatDeserializer;
import co.nlighten.jsontransform.formats.FormatSerializer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CsvFormat implements FormatSerializer, FormatDeserializer {
    private static final String COMMA = ",";
    private static final String DEFAULT_SEPARATOR = COMMA;
    private static final String DOUBLE_QUOTES = "\"";
    private static final String EMBEDDED_DOUBLE_QUOTES = "\"\"";
    private static final String NEW_LINE_UNIX = "\n";
    private static final char LINE_FEED = '\n';
    private static final char CARRIAGE_RETURN = '\r';
    private static final String NEW_LINE_WINDOWS = "\r\n";

    /**
     * Formatter -
     * if names provided it will use it as header row (unless noHeaders) and expose only those fields (if objects)
     * if names not provided, it will use the names of the first object and expose all those fields for the rest
     * Parser -
     * if names provided it will extract only those fields
     * if noHeaders is set, it will use these names to map the row values by their indices
     */
    final Collection<String> names;
    /**
     * When used in formatter, it means the headers row will not be written
     * When used in parser, it means that the output of parsing will be an array of arrays (unless names is specified)
     */
    final boolean noHeaders;
    /**
     * Formatting will quote all values
     */
    final boolean forceQuote;
    /**
     * What separator to format with or expect to parse with
     */
    final String separator;

    private final JsonAdapter<?, ?, ?> adapter;

    public CsvFormat(JsonAdapter<?, ?, ?> adapter, List<String> names, Boolean noHeaders, Boolean forceQuote, String separator) {
        this.adapter = adapter;
        this.names = names;
        this.noHeaders = noHeaders != null && noHeaders;
        this.forceQuote = forceQuote != null && forceQuote;
        this.separator = separator != null ? separator : DEFAULT_SEPARATOR;
    }

    public CsvFormat(JsonAdapter<?, ?, ?> adapter) {
        this(adapter, null, null, null, null);
    }

    private Iterable<?> asIterable(Object value) {
        if (adapter.isJsonObject(value)) {
            return null;
        }
        if (adapter.isJsonArray(value)) {
            return adapter.asIterable(value);
        }
        if (value instanceof Iterable<?> iter) {
            return iter;
        }
        if (value instanceof Object[] oa) {
            return Arrays.asList(oa);
        }
        return null;
    }

    public void appendEscaped(StringBuilder sb, Object val) {
        String value;
        if (val == null) {
            value = "";
        } else if (adapter.is(val)) {
            value = adapter.getAsString(val);
        } else {
            value = val.toString();
        }
        if (forceQuote ||
                value.contains(COMMA) ||
                value.contains(DOUBLE_QUOTES) ||
                value.contains(NEW_LINE_UNIX) ||
                value.contains(NEW_LINE_WINDOWS) ||
                value.startsWith(" ") ||
                value.endsWith(" ")) {
            sb.append(DOUBLE_QUOTES);
            sb.append(value.replace(DOUBLE_QUOTES, EMBEDDED_DOUBLE_QUOTES));
            sb.append(DOUBLE_QUOTES);
        } else {
            sb.append(value);
        }
    }

    public void appendHeaders(StringBuilder sb, Collection<String> headers) {
        if (noHeaders) return;
        var first = true;
        for (String name : headers) {
            if (!first) {
                sb.append(separator);
            } else {
                first = false;
            }
            appendEscaped(sb, name);
        }
        sb.append("\n");
    }

    public void appendRow(StringBuilder sb, Collection<String> names, Object value) {
        // check for special case of array of arrays
        Iterable<?> iter = asIterable(value);
        if (iter == null) {
            // this is a normal case of array of objects
            if (!adapter.isJsonObject(value))
                return;
            var first = true;
            for (String name : names) {
                if (!first) {
                    sb.append(separator);
                } else {
                    first = false;
                }
                appendEscaped(sb, adapter.get(value, name));
            }
        } else {
            var first = true;
            for (Object val : iter) {
                if (!first) {
                    sb.append(separator);
                } else {
                    first = false;
                }
                appendEscaped(sb, val);
            }
        }
        sb.append("\n");
    }

    @Override
    public String serialize(Object payload) {
        StringBuilder sb = new StringBuilder();

        var headers = names;
        if (headers != null) {
            appendHeaders(sb, headers);
        }

        // go through the items
        if (payload instanceof Iterable<?> iter) {
            if (headers == null) {
                var it = iter.iterator();
                if (it.hasNext()) {
                    var first = it.next();
                    if (first != null && adapter.isJsonObject(first)) {
                        headers = adapter.keySet(first);
                        appendHeaders(sb, headers);
                    }
                }
            }
            for (Object x : iter) {
                appendRow(sb, headers, x);
            }
        } else if (payload.getClass().isArray()) {
            var len = Array.getLength(payload);
            if (headers == null && len > 0) {
                var first = Array.get(payload, 0);
                if (first != null && adapter.isJsonObject(first)) {
                    headers = adapter.keySet(first);
                    appendHeaders(sb, headers);
                }
            }
            for (var i = 0; i < len; i++) {
                appendRow(sb, headers, Array.get(payload, i));
            }
        } else {
            throw new Error("Unsupported object type to be formatted as CSV");
        }

        return sb.toString();
    }

    private class CsvParserContext {
        public boolean inQuotes = false;
        public Object names = null;
        public boolean namesRead = false;
        public Collection<String> extractNames = null;
    }

    void accumulate(CsvParserContext context, Object results, Object values) {
        if (adapter.isEmpty(results) && !context.namesRead && !noHeaders) {
            context.names = values;
            context.namesRead = true;
            return;
        }
        if (noHeaders && names == null) {
            adapter.add(results, values); // set item as an array of values
            return;
        }
        // there are names, make a map
        if (context.names != null) {
            var item = adapter.createObject();
            int i;
            for (i = 0; i < adapter.size(context.names); i++) {
                var name = adapter.getAsString(adapter.get(context.names, i));
                if ((context.extractNames == null || context.extractNames.contains(name)) &&
                        adapter.size(values) > i) {
                    adapter.add(item, name, adapter.get(values, i));
                }
            }
            // TODO: make it conditional
            // more values than names
            for (; i < adapter.size(values); i++) {
                if (!adapter.has(item, "$" + i)) {
                    adapter.add(item, "$" + i, adapter.get(values, i));
                }
            }
            adapter.add(results, item);
        }
    }
    @Override
    public Object deserialize(String input) {
        var result = adapter.createArray();
        var context = new CsvParserContext();
        if (noHeaders && names != null) {
            context.names = adapter.createArray();
            names.forEach(item -> adapter.add(context.names, item));
        }
        context.extractNames = names;

        var len = input.length();
        var row = adapter.createArray();
        var cell = new StringBuilder();
        var offset = 0;

        while (offset < len) {
            // always take current one and next one (offset may skip 1 or 2 depending on char sequence)
            final int cur = input.codePointAt(offset);
            var curSize = Character.charCount(cur);
            final int next = offset + curSize < len ?  input.codePointAt(offset + curSize) : -1;
            var curAndNextSize = curSize + Character.charCount(next);

            if (cur == separator.codePointAt(0)) {
                if (context.inQuotes) {
                    cell.append(separator);
                } else {
                    adapter.add(row, cell.toString());
                    cell.setLength(0);
                }

                offset += curSize;
            } else if ( (cur == CARRIAGE_RETURN && next == LINE_FEED) || cur == LINE_FEED) {
                var unix = cur == LINE_FEED;
                var eof = offset + (unix ? curSize : curAndNextSize) == len;
                if (!eof) {
                    if (context.inQuotes) {
                        cell.append(unix ? NEW_LINE_UNIX : NEW_LINE_WINDOWS);
                    } else {
                        adapter.add(row, cell.toString());
                        cell.setLength(0);
                        accumulate(context, result, row);
                        row = adapter.createArray();
                    }
                }
                offset += unix ? curSize : curAndNextSize;
            } else if (cur == '"' && next == '"') { // span.startsWith(EMBEDDED_DOUBLE_QUOTES)) {
                if (context.inQuotes) {
                    cell.append(DOUBLE_QUOTES);
                    offset += curAndNextSize;
                } else if (cell.isEmpty()) {
                    // consider only the first one
                    context.inQuotes = !context.inQuotes;
                    offset += curSize;
                } else {
                    cell.append(DOUBLE_QUOTES);
                    offset += curSize;
                }
            } else if (cur == '"') { // span.startsWith(DOUBLE_QUOTES)) {
                context.inQuotes = !context.inQuotes;
                offset += curSize;
            } else if (!context.inQuotes && (cur == ' ' || cur == '\t')) { // (span.codePointAt(0) == ' ' || span.codePointAt(0) == '\t')) {
                // ignore
                offset += curSize;
            } else {
                cell.append(Character.toString(cur));
                offset += curSize;
            }
        }

        if (!adapter.isEmpty(result) || !cell.isEmpty()) {
            adapter.add(row, cell.toString());
            accumulate(context, result, row);
        }
        return result;
    }
}
