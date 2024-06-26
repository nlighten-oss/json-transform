package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.TransformerFunctions;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

class JsonOrgJsonTransformer extends JsonTransformer<Object, JSONArray, JSONObject> {

    public static final JsonAdapter<Object, JSONArray, JSONObject> ADAPTER = new JsonOrgJsonAdapter();
    public static final TransformerFunctions<Object, JSONArray, JSONObject> FUNCTIONS = new TransformerFunctions<>(ADAPTER);

    public JsonOrgJsonTransformer(final Object definition) {
        super(FUNCTIONS, ADAPTER, definition);
    }
}
