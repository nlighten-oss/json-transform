package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.DebuggableTransformerFunctions;
import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.TransformerFunctions;
import com.google.gson.*;

public class GsonJsonTransformer extends JsonTransformer {

    public static final JsonAdapter<JsonElement, JsonArray, JsonObject> ADAPTER = new GsonJsonAdapter();
    public static final TransformerFunctions FUNCTIONS = new TransformerFunctions(ADAPTER);

    public GsonJsonTransformer(final Object definition) {
        this(definition, FUNCTIONS);
    }

    public GsonJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, ADAPTER, functionsAdapter);
    }

    public static DebuggableTransformerFunctions getDebuggableAdapter() {
        return new DebuggableTransformerFunctions(ADAPTER);
    }
}
