package co.nlighten.jsontransform.playground;

import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformer;
import co.nlighten.jsontransform.adapters.jackson.JacksonJsonTransformer;
import co.nlighten.jsontransform.adapters.jsonorg.JsonOrgJsonTransformer;
import co.nlighten.jsontransform.adapters.pojo.PojoJsonTransformer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class ApiController {

    @PostMapping("/v1/transform/gson")
    public TransformTestResponse v1TransformGson(@RequestBody TransformTestRequest request){
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

    @PostMapping("/v1/transform/pojo")
    public TransformTestResponse v1TransformPojo(@RequestBody TransformTestRequest request){
        var adapter = PojoJsonTransformer.getDebuggableAdapter();
        var transformer = new PojoJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext);
        result = PojoJsonTransformer.ADAPTER.unwrap(result);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }

    @PostMapping("/v1/transform/jsonorg")
    public TransformTestResponse v1TransformJsonOrg(@RequestBody TransformTestRequest request){
        var adapter = JsonOrgJsonTransformer.getDebuggableAdapter();
        var transformer = new JsonOrgJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext);
        result = JsonOrgJsonTransformer.ADAPTER.unwrap(result);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }
}