package co.nlighten.jsontransform.playground;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.adapters.gson.GsonJsonAdapter;
import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformer;
import co.nlighten.jsontransform.adapters.jackson.JacksonJsonAdapter;
import co.nlighten.jsontransform.adapters.jackson.JacksonJsonTransformer;
import co.nlighten.jsontransform.adapters.jackson.JacksonJsonTransformerConfiguration;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class ApiController {

    @PostMapping("/v1/transform")
    public TransformTestResponse v1Transform(@RequestBody TransformTestRequest request){
        var adapter = GsonJsonTransformer.getDebuggableAdapter();
        var transformer = new GsonJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext);
        result = GsonJsonTransformer.ADAPTER.unwrap(result);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }

    @PostMapping("/v1/transform/jackson")
    public TransformTestResponse v1TransformJackson(@RequestBody TransformTestRequest request){
        var adapter = JacksonJsonTransformer.getDebuggableAdapter();
        var transformer = new JacksonJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext);
        result = JacksonJsonTransformer.ADAPTER.unwrap(result);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }
}