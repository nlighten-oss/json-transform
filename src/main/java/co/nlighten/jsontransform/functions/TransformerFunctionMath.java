package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/*
 * For tests
 * @see TransformerFunctionMathTest
 */
@Aliases("math")
@Documentation("Evaluate a mathematical expression")
@InputType(value = {ArgType.BigDecimal, ArgType.Enum, ArgType.Array}, description = "op1 / op / [op1, op, op2] / [op, op1, op2]")
@ArgumentType(value = "op1", type = ArgType.BigDecimal, position = 0 /* or 1 */, defaultBigDecimal = 0d, required = true,
              description = "First operand")
@ArgumentType(value = "op", type = ArgType.Enum, position = 1 /* or 0 */, defaultEnum = "0", required = true,
              enumValues = {"+","-","*","/","//","%","^","&","|","~","<<",">>","MIN","MAX","SQRT","ROUND","FLOOR","CEIL","ABS","NEG","SIG"},
              description = "")
@ArgumentType(value = "op2", type = ArgType.BigDecimal, position = 2, defaultBigDecimal = 0d,
              description = "Second operand or scale for ROUND/FLOOR/CEIL")
@OutputType(ArgType.BigDecimal)
public class TransformerFunctionMath<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionMath.class);

    public TransformerFunctionMath(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonArray(null);
        String parsedOp;
        MathOp op;
        BigDecimal op1;
        BigDecimal op2;
        if (value != null) {
            var size = ARRAY.size(value);
            if (size <= 1) return null; // invalid input
            var arg0 = ARRAY.get(value, 0);
            var arg1 = ARRAY.get(value, 1);
            parsedOp = context.getAsString(arg0);
            op = parseMathOp(parsedOp) ;
            if (size > 2 && op == MathOp.UNKNOWN) {
                parsedOp = context.getAsString(arg1);
                op = parseMathOp(parsedOp);
                op1 = adapter.getNumberAsBigDecimal(arg0);
            } else {
                op1 = adapter.getNumberAsBigDecimal(arg1);
            }
            op2 = size < 3 ? BigDecimal.ZERO : adapter.getNumberAsBigDecimal(ARRAY.get(value, 2));
        } else {
            // order of arguments ( op1, op, op2 )
            parsedOp = context.getEnum("op1");
            op = parseMathOp(parsedOp);
            if (op == MathOp.UNKNOWN) {
                // op was not detected as the first argument, so we assume it is in the second argument
                // -> op1, op, [op2]
                parsedOp = context.getEnum("op");
                op = parseMathOp(parsedOp);
                op1 = context.getBigDecimal("op1");
                op2 = context.getBigDecimal("op2");
            } else {
                var mainArgValue = context.getUnwrapped(null);
                if (mainArgValue != null) {
                    // we set operand 1 as main argument value for the sake of functions with only one operand
                    // -> op, [op2] : op1
                    op1 = new BigDecimal(mainArgValue.toString());
                    op2 = context.getBigDecimal("op");
                } else {
                    // -> op, op1, op2
                    op1 = context.getBigDecimal("op");
                    op2 = context.getBigDecimal("op2");
                }
            }
        }

        if (op == MathOp.UNKNOWN) {
            logger.warn("{} was specified with an unknown op ({})", context.getAlias(), parsedOp);
            return null;
        }
        var result = eval(op, op1, op2);

        if (result == null) {
            return null;
        }
        // cap scale at max
        if (result.scale() > FunctionHelpers.MAX_SCALE) {
            result = result.setScale(FunctionHelpers.MAX_SCALE, FunctionHelpers.MAX_SCALE_ROUNDING);
        }
        return result;
    }

    enum MathOp {
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        INTEGER_DIVISION,
        MODULU,
        POWER,
        SQUARE_ROOT,
        MIN,
        MAX,
        ROUND,
        FLOOR,
        CEIL,
        ABSOLUTE,
        NEGATION,
        SIGNUM,
        BITAND,
        BITOR,
        BITXOR,
        SHIFT_LEFT,
        SHIFT_RIGHT,
        UNKNOWN
    }

    static MathOp parseMathOp(String value) {
        return switch (value.toUpperCase()) {
            case "+", "ADD" -> MathOp.ADDITION;
            case "-", "SUB", "SUBTRACT" -> MathOp.SUBTRACTION;
            case "*", "MUL", "MULTIPLY" -> MathOp.MULTIPLICATION;
            case "/", "DIV", "DIVIDE" -> MathOp.DIVISION;
            case "//", "INTDIV" -> MathOp.INTEGER_DIVISION;
            case "%", "MOD", "REMAINDER" -> MathOp.MODULU;
            case "^", "**", "POW", "POWER" -> MathOp.POWER;
            case "&", "AND" -> MathOp.BITAND;
            case "|", "OR" -> MathOp.BITOR;
            case "~", "XOR" -> MathOp.BITXOR;
            case "<<", "SHL" -> MathOp.SHIFT_LEFT;
            case ">>", "SHR" -> MathOp.SHIFT_RIGHT;
            case "MIN" -> MathOp.MIN;
            case "MAX" -> MathOp.MAX;
            case "SQRT" -> MathOp.SQUARE_ROOT;
            case "ROUND" -> MathOp.ROUND;
            case "FLOOR" -> MathOp.FLOOR;
            case "CEIL" -> MathOp.CEIL;
            case "ABS" -> MathOp.ABSOLUTE;
            case "NEG", "NEGATE" -> MathOp.NEGATION;
            case "SIG", "SIGNUM" -> MathOp.SIGNUM;
            default -> MathOp.UNKNOWN;
        };
    }

    static BigDecimal eval(MathOp op, BigDecimal op1, BigDecimal op2) {
        return switch (op) {
            // 2 operands
            case ADDITION -> op1.add(op2);
            case SUBTRACTION -> op1.subtract(op2);
            case MULTIPLICATION -> op1.multiply(op2);
            case DIVISION -> op1.divide(op2, MathContext.DECIMAL128);
            case INTEGER_DIVISION -> op1.divideToIntegralValue(op2);
            case MODULU -> op1.remainder(op2);
            case POWER -> op1.pow(op2.intValue());
            case MIN -> op1.min(op2);
            case MAX -> op1.max(op2);
            // only one operand
            case SQUARE_ROOT -> op1.sqrt(MathContext.DECIMAL128);
            case ROUND -> op1.setScale(op2.intValue(), RoundingMode.HALF_UP);
            case FLOOR -> op1.setScale(op2.intValue(), RoundingMode.FLOOR);
            case CEIL -> op1.setScale(op2.intValue(), RoundingMode.CEILING);
            case ABSOLUTE -> op1.abs();
            case NEGATION -> op1.negate();
            case SIGNUM -> new BigDecimal(op1.signum());
            // bitwise
            case BITAND -> new BigDecimal(op1.toBigInteger().and(op2.toBigInteger()));
            case BITOR -> new BigDecimal(op1.toBigInteger().or(op2.toBigInteger()));
            // special case where only 1 op (~x) acts as NOT (op2 acts like ~0)
            case BITXOR -> new BigDecimal(op2 == null ? op1.toBigInteger().not() : op1.toBigInteger().xor(op2.toBigInteger()));
            case SHIFT_LEFT -> new BigDecimal(op1.toBigInteger().shiftLeft(op2.intValue()));
            case SHIFT_RIGHT -> new BigDecimal(op1.toBigInteger().shiftRight(op2.intValue()));

            default -> null;
        };
    }
}
