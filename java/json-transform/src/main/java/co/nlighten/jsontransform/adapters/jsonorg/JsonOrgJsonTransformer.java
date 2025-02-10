package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.DebuggableTransformerFunctions;
import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.TransformerFunctions;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonOrgJsonTransformer extends JsonTransformer {

    public static final JsonAdapter<Object, JSONArray, JSONObject> ADAPTER = new JsonOrgJsonAdapter();
    public static final TransformerFunctions FUNCTIONS = new TransformerFunctions(ADAPTER);

    public JsonOrgJsonTransformer(final Object definition) {
        this(definition, FUNCTIONS);
    }

    public JsonOrgJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, ADAPTER, functionsAdapter);
    }

    public static DebuggableTransformerFunctions getDebuggableAdapter() {
        return new DebuggableTransformerFunctions(ADAPTER);
    }
}