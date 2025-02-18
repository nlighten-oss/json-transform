package co.nlighten.jsontransform.template;

import co.nlighten.jsontransform.MultiAdapterBaseTest;
import co.nlighten.jsontransform.ParameterResolver;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;

public class TextTemplateTests extends MultiAdapterBaseTest {

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void runJsonTransformerFunction(JsonAdapter<?,?,?> adapter) {
        var resolver = adapter.createPayloadResolver(adapter.parse("""
{ "n": "NAME" }
"""), null, true);
        var def = new TextTemplate("Hello {$$lower:$.n}").render(resolver);
        Assertions.assertEquals("Hello name", def);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void runJsonTransformerFunction2(JsonAdapter<?,?,?> adapter) {
        var resolver = adapter.createPayloadResolver(adapter.parse("""
{ "a": ["hello", " ", "world"], "u": "cb2aa228-b265-3f99-aae4-73a58f7be18b" }
"""), null, true);
        var def = new TextTemplate("{$$join:$.a} {$$uuid(v3):$.u}").render(resolver);
        Assertions.assertEquals("hello world 2c14e2de-ca8e-37b4-bf57-bed2799a6b33", def);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void runUnknownJsonTransformerFunction(JsonAdapter<?,?,?> adapter) {
        var resolver = adapter.createPayloadResolver(adapter.parse("""
{ "n": "NAME" }
"""), null, true);
        var def = new TextTemplate("Hello {$$xxx:$.n}").render(resolver);
        // json transformer resolver returns 'name' if not found
        Assertions.assertEquals("Hello $$xxx:$.n", def);
    }


    @Test
    void recursiveWithDefaultValue() {
        var resolver = ParameterResolver.fromMap(Map.of(
                "A", "A",
                "b", "B"
        ));
        var def = new TextTemplate("{a,b}").render(resolver);
        Assertions.assertEquals("b", def);
    }

    @Test
    void recursiveWithDefaultValueNestingParameters() {
        var resolver = ParameterResolver.fromMap(Map.of(
            "A", "A",
            "b", "B"
        ));
        var def = new TextTemplate("{a,{b}}").render(resolver);
        Assertions.assertEquals("B", def);
        def = new TextTemplate("{A,{b}}").render(resolver);
        Assertions.assertEquals("A", def);
        def = new TextTemplate("{x,{y,{b}}}").render(resolver);
        Assertions.assertEquals("B", def);
    }


    @Test
    void escapedTemplateParameter() {
        var resolver = ParameterResolver.fromMap(Map.of(
                "a", "A"
        ));
        var def = new TextTemplate("\\{a}").render(resolver);
        Assertions.assertEquals("{a}", def);
    }

    @Test
    void resolvedTemplateParameterToTemplateLike() {
        var resolver = ParameterResolver.fromMap(Map.of(
                "a", "{b}",
                "b", "B"
        ));
        var def = new TextTemplate("{a}").render(resolver);
        Assertions.assertEquals("{b}", def);
    }

    @Test
    void recursiveWithEscapedTemplateParameterDefaultValue() {
        var resolver = ParameterResolver.fromMap(Map.of(
                "a", "A"
        ));
        var def = new TextTemplate("{b,{a}}").render(resolver);
        Assertions.assertEquals("A", def);
        def = new TextTemplate("{b,\\{a}}").render(resolver);
        Assertions.assertEquals("{a}", def);
    }

    @Test
    void recursiveWithEscapedTemplateParameterDefaultValue2() {
        var resolver = ParameterResolver.fromMap(Map.of(
                "a", "A",
                "b", "{a}"
        ));
        var def = new TextTemplate("{x,{b}}").render(resolver);
        Assertions.assertEquals("{a}", def);
    }

    @Test
    void recursiveWithEscapedTemplateParameterDefaultValue3() {
        var resolver = ParameterResolver.fromMap(Map.of(
                "a", "A"
        ));
        var def = new TextTemplate("{b,\\{a}}").render(resolver);
        Assertions.assertEquals("{a}", def);
    }

    @Test
    void urlEncode() {
        var resolver = ParameterResolver.fromMap(Map.of(
            "href", "https://example.com/"
        ));
        // true
        var def = new TextTemplate("href={href}").render(resolver, true);
        Assertions.assertEquals("href=https%3A%2F%2Fexample.com%2F", def);
        // false
        def = new TextTemplate("href={href}").render(resolver);
        Assertions.assertEquals("href=https://example.com/", def);
    }
}
