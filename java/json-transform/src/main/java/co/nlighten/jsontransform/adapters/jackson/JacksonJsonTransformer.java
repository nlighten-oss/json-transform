package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.DebuggableTransformerFunctions;
import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.TransformerFunctions;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonJsonTransformer extends JsonTransformer {

    public static final JsonAdapter<JsonNode, ArrayNode, ObjectNode> ADAPTER = new JacksonJsonAdapter();
    public static final TransformerFunctions FUNCTIONS = new TransformerFunctions(ADAPTER);

    public JacksonJsonTransformer(final Object definition) {
        this(definition, FUNCTIONS);
    }

    public JacksonJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, ADAPTER, functionsAdapter);
    }

    public static DebuggableTransformerFunctions getDebuggableAdapter() {
        return new DebuggableTransformerFunctions(ADAPTER);
    }
}
