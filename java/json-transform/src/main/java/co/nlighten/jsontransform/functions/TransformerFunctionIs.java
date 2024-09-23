package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.math.BigDecimal;
import java.util.Objects;

/*
 * For tests
 * @see TransformerFunctionIsTest
 */
@ArgumentType(value = "op", type = ArgType.Enum, position = 0, defaultIsNull = true)
@ArgumentType(value = "that", type = ArgType.Any, position = 1, defaultIsNull = true)
@ArgumentType(value = "in", type = ArgType.Array, defaultIsNull = true)
@ArgumentType(value = "nin", type = ArgType.Array, defaultIsNull = true)
@ArgumentType(value = "eq", type = ArgType.Any, defaultIsNull = true)
@ArgumentType(value = "neq", type = ArgType.Any, defaultIsNull = true)
@ArgumentType(value = "gt", type = ArgType.Any, defaultIsNull = true)
@ArgumentType(value = "gte", type = ArgType.Any, defaultIsNull = true)
@ArgumentType(value = "lt", type = ArgType.Any, defaultIsNull = true)
@ArgumentType(value = "lte", type = ArgType.Any, defaultIsNull = true)
public class TransformerFunctionIs<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    public TransformerFunctionIs(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    private JE nullableBigDecimalJsonPrimitive(FunctionContext<JE, JA, JO> context, BigDecimal number) {
        return number == null ? adapter.jsonNull() : adapter.wrap(number);
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonElement(null);
        if (context.has("op")) {
            var op = context.getEnum("op");
            JE that = null;
            // if operator is not in/nin then prepare the "that" argument which is a JsonElement
            if (!Objects.equals(op, "IN") && !Objects.equals(op, "NIN")) {
                that = adapter.isJsonNumber(value)
                       ? nullableBigDecimalJsonPrimitive(context, context.getBigDecimal("that"))
                       : context.getJsonElement("that");
            }
            return switch (op) {
                case "IN" -> {
                    var in = context.getJsonElementStreamer("that");
                    yield in != null && in.stream().anyMatch(value::equals);
                }
                case "NIN" -> {
                    var nin = context.getJsonElementStreamer("that");
                    yield nin != null && nin.stream().noneMatch(value::equals);
                }
                case "GT",">" -> {
                    var comparison = context.compareTo(value, that);
                    yield comparison != null && comparison > 0;
                }
                case "GTE",">=" -> {
                    var comparison = context.compareTo(value, that);
                    yield comparison != null && comparison >= 0;
                }
                case "LT","<" -> {
                    var comparison = context.compareTo(value, that);
                    yield comparison != null && comparison < 0;
                }
                case "LTE","<=" -> {
                    var comparison = context.compareTo(value, that);
                    yield comparison != null && comparison <= 0;
                }
                case "EQ","=","==" -> value.equals(that);
                case "NEQ","!=","<>" -> !value.equals(that);
                default -> false;
            };
        }
        var result = true;
        if (context.has("in")) {
            var in = context.getJsonElementStreamer("in");
            result = in != null && in.stream().anyMatch(value::equals);
        }
        if (result && context.has("nin")) {
            var nin = context.getJsonElementStreamer("nin");
            result = nin != null && nin.stream().noneMatch(value::equals);
        }
        if (result && context.has("gt")) {
            var gt = context.getJsonElement("gt");
            var comparison = context.compareTo(value, gt);
            result = comparison != null && comparison > 0;
        }
        if (result && context.has("gte")) {
            var gte = context.getJsonElement("gte");
            var comparison = context.compareTo(value, gte);
            result = comparison != null && comparison >= 0;
        }
        if (result && context.has("lt")) {
            var lt = context.getJsonElement("lt");
            var comparison = context.compareTo(value, lt);
            result = comparison != null && comparison < 0;
        }
        if (result && context.has("lte")) {
            var lte = context.getJsonElement("lte");
            var comparison = context.compareTo(value, lte);
            result = comparison != null && comparison <= 0;
        }
        if (result && context.has("eq")) {
            var eq = context.getJsonElement("eq");
            result = value.equals(eq);
        }
        if (result && context.has("neq")) {
            var neq = context.getJsonElement("neq");
            result = !value.equals(neq);
        }
        return result;
    }
}
