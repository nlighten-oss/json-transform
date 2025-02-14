package transformers;

import co.nlighten.jsontransform.JsonTransformerTest;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 50, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(value = Scope.Benchmark)
public class TransformersFunctionsBenchmarksTests {

    @Param({"gson", "jackson", "pojo", "jsonOrg", "jsonSmart"})
    public String adapterType;

    public JsonAdapter<?, ?, ?> getAdapterByType(String adapterType) {
        switch (adapterType) {
            case "gson":
                return new co.nlighten.jsontransform.adapters.gson.GsonJsonAdapter();
            case "jackson":
                return new co.nlighten.jsontransform.adapters.jackson.JacksonJsonAdapter();
            case "pojo":
                return new co.nlighten.jsontransform.adapters.pojo.PojoJsonAdapter();
            case "jsonOrg":
                return new co.nlighten.jsontransform.adapters.jsonorg.JsonOrgJsonAdapter();
            case "jsonSmart":
                return new co.nlighten.jsontransform.adapters.jsonsmart.JsonSmartJsonAdapter();
            default:
                throw new IllegalArgumentException("Invalid adapter type: " + adapterType);
        }
    }

    @Benchmark
    @Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public void measureInputExtractorSpreadArray2() {
        var test = new JsonTransformerTest();
        test.testInputExtractorSpreadArray2(getAdapterByType(adapterType));
    }
}
