package co.nlighten.jsontransform.playground;

import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class ApiController {

    @PostMapping("/v1/transform")
    public TransformTestResponse v1Transform(@RequestBody TransformTestRequest request){
        var transformer = new GsonJsonTransformer(request.definition);
        var result = transformer.transform(request.input, null);
        return new TransformTestResponse(result);
    }
}