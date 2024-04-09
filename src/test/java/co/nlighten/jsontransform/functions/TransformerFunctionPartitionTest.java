package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionPartitionTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation(fromJson("""
[1,2,3,4,5,6,7,8,9,10]
"""), "$$partition(3):$", fromJson("""
[[1,2,3],[4,5,6],[7,8,9],[10]]
"""));
    }
    @Test
    void object() {
        assertTransformation(fromJson("""
[1,2,3,4,5,6,7,8,9,10]
"""), fromJson("""
            { "$$partition": "$", "size": 3}"""), fromJson("""
[[1,2,3],[4,5,6],[7,8,9],[10]]
"""));
    }
}
