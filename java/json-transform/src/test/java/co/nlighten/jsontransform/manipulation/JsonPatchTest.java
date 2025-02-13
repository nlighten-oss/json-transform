package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.MultiAdapterBaseTest;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Taken from https://github.com/egerardus/simple-json-patch/blob/main/src/test/resources/testPatches.json
 */
public class JsonPatchTest extends MultiAdapterBaseTest {

    public static String read(final String filename) throws FileNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(filename)) {
            if (is == null) {
                throw new FileNotFoundException(filename);
            }
            try (var isr = new InputStreamReader(is, StandardCharsets.UTF_8); var reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record AdapterAndTest(JsonAdapter<?, ?, ?> adapter, Object test) {}

    private static Stream<AdapterAndTest> tests() throws FileNotFoundException {
        String json = read("JsonPatchTests.json");
        var streams = MultiAdapterBaseTest.provideJsonAdapters()
                .map(adapter -> {
                    var tests = adapter.parse(json);
                    return adapter.stream(tests).map(t -> new AdapterAndTest(adapter, t));
                }).toArray(Stream[]::new);
        return Stream.of(streams)
                .reduce(Stream::concat)
                .orElseGet(Stream::empty);
    }

    @MethodSource("tests")
    @ParameterizedTest
    void test(final AdapterAndTest aat) {
        var adapter = aat.adapter;
        var test = aat.test;
        var jsonPatch = new JsonPatch(adapter);
        if (adapter.has(test, "disabled") && adapter.getBoolean(adapter.get(test, "disabled"))) {
            // skip
            return;
        }
        if (adapter.has(test, "skipFor")) {
            var skipFor = adapter.get(test, "skipFor");
            if (adapter.isJsonArray(skipFor)) {
                for (var skip : adapter.asIterable(skipFor)) {
                    if (adapter.isJsonString(skip) && adapter.getClass().getName().contains(adapter.getAsString(skip))) {
                        // skip
                        return;
                    }
                }
            }
        }

        var doc = adapter.get(test, "doc");
        var patch = adapter.get(test, "patch");
        var hasError = adapter.has(test, "error");
        if (hasError) {
            Assertions.assertThrows(Exception.class, () -> {
                jsonPatch.patch(patch, doc);
            });
        } else {
            var actual = jsonPatch.patch(patch, doc);
            var message = adapter.get(test, "comment");
            var expected = adapter.get(test, "expected");
            assertEquals(adapter, expected, actual, message == null ? "unexpected" : adapter.getAsString(message));
        }
    }
}
