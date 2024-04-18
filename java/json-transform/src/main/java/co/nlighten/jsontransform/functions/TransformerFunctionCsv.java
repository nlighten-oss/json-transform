package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.csv.CsvFormat;
import co.nlighten.jsontransform.functions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * @see CsvFormat for arguments explanation
 * For tests
 * @see TransformerFunctionCsvTest
 */
@Aliases("csv")
@Documentation("Converts an array of objects/arrays to a CSV string")
@InputType(ArgType.Array)
@ArgumentType(value = "no_headers", type = ArgType.Boolean, position = 0, defaultBoolean = false,
              description = "Whether to include object keys as headers (taken from first object if no `names`)")
@ArgumentType(value = "force_quote", type = ArgType.Boolean, position = 1, defaultBoolean = false,
              description = "Whether to quote all the values")
@ArgumentType(value = "separator", type = ArgType.String, position = 2, defaultString = ",",
              description = "Use an alternative field separator")
@ArgumentType(value = "names", type = ArgType.Array, position = 3, defaultIsNull = true,
              description = "Names of fields to extract into csv if objects (will be used as the header row, unless `no_headers`)")
@OutputType(ArgType.String)
public class TransformerFunctionCsv<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionCsv.class);

    public TransformerFunctionCsv(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        try {
            if (streamer == null)
                return null;
            var names = context.getJsonArray("names");
            var noHeaders = context.getBoolean("no_headers");
            var forceQuote = context.getBoolean("force_quote");
            var separator = context.getString("separator");
            List<String> namesList = null;
            if (names != null) {
                var stringStream = (Stream<String>) jArray.stream(names).map(context::getAsString);
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
