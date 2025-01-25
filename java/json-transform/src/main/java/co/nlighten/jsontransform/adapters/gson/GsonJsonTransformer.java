package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.DebuggableTransformerFunctions;
import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.TransformerFunctions;
import com.google.gson.*;

public class GsonJsonTransformer extends JsonTransformer<JsonElement, JsonArray, JsonObject> {

    public static final JsonAdapter<JsonElement, JsonArray, JsonObject> ADAPTER = new GsonJsonAdapter();
    public static final TransformerFunctions<JsonElement, JsonArray, JsonObject> FUNCTIONS = new TransformerFunctions<>(ADAPTER);

    public GsonJsonTransformer(final JsonElement definition) {
        this(definition, FUNCTIONS);
    }

    public GsonJsonTransformer(final JsonElement definition, TransformerFunctionsAdapter<JsonElement, JsonArray, JsonObject> functionsAdapter) {
        super(ADAPTER, definition, functionsAdapter);
    }

    public static DebuggableTransformerFunctions<JsonElement, JsonArray, JsonObject> getDebuggableAdapter() {
        return new DebuggableTransformerFunctions<>(ADAPTER);
    }
}
