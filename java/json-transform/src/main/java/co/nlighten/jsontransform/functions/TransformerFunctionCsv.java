package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.formats.csv.CsvFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * @see CsvFormat for arguments explanation
 * For tests
 * @see TransformerFunctionCsvTest
 */
public class TransformerFunctionCsv extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionCsv.class);

    public TransformerFunctionCsv() {
        super(FunctionDescription.of(
            Map.of(
            "no_headers", ArgumentType.of(ArgType.Boolean).position(0).defaultBoolean(false),
            "force_quote", ArgumentType.of(ArgType.Boolean).position(1).defaultBoolean(false),
            "separator", ArgumentType.of(ArgType.String).position(2).defaultString(","),
            "names", ArgumentType.of(ArgType.Array).position(3).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        try {
            if (streamer == null)
                return null;
            var names = context.getJsonArray("names");
            var noHeaders = context.getBoolean("no_headers");
            var forceQuote = context.getBoolean("force_quote");
            var separator = context.getString("separator");
            List<String> namesList = null;
            var adapter = context.getAdapter();
            if (names != null) {
                var stringStream = (Stream<String>) adapter.stream(names).map(context::getAsString);
                namesList = stringStream.collect(Collectors.toList());
            }
            return new CsvFormat(
                    adapter,
                    namesList,
                    noHeaders,
                    forceQuote,
                    separator)
                    .serialize(streamer.toJsonArray());
        } catch (Exception e) {
            logger.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
