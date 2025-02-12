package co.nlighten.jsontransform.adapters.pojo;

import co.nlighten.jsontransform.DebuggableTransformerFunctions;
import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.TransformerFunctions;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.AbstractList;
import java.util.Map;

public class PojoJsonTransformer extends JsonTransformer {

    public static final JsonAdapter<Object, AbstractList<Object>, Map<String, Object>> ADAPTER = new PojoJsonAdapter();
    public static final TransformerFunctions FUNCTIONS = new TransformerFunctions(ADAPTER);

    public PojoJsonTransformer(final Object definition) {
        this(definition, FUNCTIONS);
    }

    public PojoJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, ADAPTER, functionsAdapter);
    }

    public static DebuggableTransformerFunctions getDebuggableAdapter() {
        return new DebuggableTransformerFunctions(ADAPTER);
    }
}
