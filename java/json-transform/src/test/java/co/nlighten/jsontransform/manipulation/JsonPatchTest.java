package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.BaseTest;
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
public class JsonPatchTest extends BaseTest {

    JsonPatch jsonPatch = new JsonPatch(adapter);

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

    private static Stream<?> tests() throws FileNotFoundException {
        String json = read("JsonPatchTests.json");
        return adapter.stream(adapter.parse(json), true);
    }

    @MethodSource("tests")
    @ParameterizedTest
    void test(final Object test) {
        if (adapter.has(test, "disabled") && adapter.getBoolean(adapter.get(test, "disabled"))) {
            // skip
            return;
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
            assertEquals(expected, actual, message == null ? "unexpected" : adapter.getAsString(message));
        }
    }
}
