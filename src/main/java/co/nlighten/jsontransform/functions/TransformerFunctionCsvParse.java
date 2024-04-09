package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.csv.CsvFormat;
import co.nlighten.jsontransform.functions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/*
 * @see CsvFormat for arguments explanation
 * For tests
 * @see TransformerFunctionCsvParseTest
 */
@Aliases("csvparse")
@Documentation("Converts a CSV string into an array of objects/arrays")
@InputType(ArgType.String)
@ArgumentType(value = "no_headers", type = ArgType.Boolean, position = 0, defaultBoolean = false,
              description = "Whether to treat the first row as object keys")
@ArgumentType(value = "separator", type = ArgType.String, position = 1, defaultString = ",",
              description = "Use an alternative field separator")
@ArgumentType(value = "names", type = ArgType.Array, position = 2, defaultIsNull = true,
              description = "Names of fields of input arrays (by indices) or objects (can sift if provided less names than there are in the objects provided)")
@OutputType(ArgType.Array)
public class TransformerFunctionCsvParse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionCsvParse.class);

    public TransformerFunctionCsvParse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var csv = context.getString(null);
        try {
            if (csv == null)
                return null;
            var names = context.getJsonArray("names");
            var noHeaders = context.getBoolean("no_headers");
            var separator = context.getString("separator");
            return new CsvFormat(adapter,
                                 names == null
                                 ? null
                                 : ARRAY.stream(names)
                                         .map(context::getAsString)
                                         .collect(Collectors.toList()),
                                                        noHeaders,
                                                        false, // not relevant for deserialization
                                                        separator)
                    .deserialize(csv);
        } catch (Exception e) {
            logger.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
