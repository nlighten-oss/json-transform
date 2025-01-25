package co.nlighten.jsontransform.playground;

import co.nlighten.jsontransform.DebuggableTransformerFunctions;
import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class ApiController {

    @PostMapping("/v1/transform")
    public TransformTestResponse v1Transform(@RequestBody TransformTestRequest request){
        var adapter = GsonJsonTransformer.getDebuggableAdapter();
        var transformer = new GsonJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }
}