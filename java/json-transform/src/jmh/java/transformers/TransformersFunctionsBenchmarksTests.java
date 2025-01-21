package transformers;

import co.nlighten.jsontransform.JsonTransformerTest;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 50, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(value = Scope.Benchmark)
public class TransformersFunctionsBenchmarksTests {

    @Benchmark
    @Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public void measureInputExtractorSpreadArray2(){
        var test = new JsonTransformerTest();
        test.testInputExtractorSpreadArray2();
    }
}
