package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.formats.csv.CsvFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

/*
 * @see CsvFormat for arguments explanation
 * For tests
 * @see TransformerFunctionCsvParseTest
 */
public class TransformerFunctionCsvParse extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionCsvParse.class);

    public TransformerFunctionCsvParse() {
        super(FunctionDescription.of(
            Map.of(
            "no_headers", ArgumentType.of(ArgType.Boolean).position(0).defaultBoolean(false),
            "separator", ArgumentType.of(ArgType.String).position(1).defaultString(","),
            "names", ArgumentType.of(ArgType.Array).position(2).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var csv = context.getString(null);
        try {
            if (csv == null)
                return null;
            var names = context.getJsonArray("names");
            var noHeaders = context.getBoolean("no_headers");
            var separator = context.getString("separator");
            var adapter = context.getAdapter();
            return new CsvFormat(adapter,
                                 names == null
                                 ? null
                                 : adapter.stream(names)
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
