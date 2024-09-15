import BigNumber from "bignumber.js";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { getAsString, isNullOrUndefined } from "../JsonHelpers";
import { BigDecimal, MAX_SCALE, MAX_SCALE_ROUNDING } from "./common/FunctionHelpers";

enum MathOp {
  ADDITION = 1,
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
  UNKNOWN,
}

const BigZero = BigDecimal(0);

const toBigInt = (value: BigNumber) => BigInt(BigDecimal(value).toFixed(0, BigNumber.ROUND_DOWN));

const DESCRIPTION: FunctionDescription = {
  aliases: ["math"],
  description: "",
  inputType: ArgType.Any,
  arguments: {
    op1: {
      type: ArgType.BigDecimal,
      position: 0 /* or 1 */,
      defaultBigDecimal: 0,
      required: true,
      description: "First operand",
    },
    op: {
      type: ArgType.Enum,
      position: 1 /* or 0 */,
      defaultEnum: "0",
      required: true,
      enumValues: [
        "+",
        "-",
        "*",
        "/",
        "//",
        "%",
        "^",
        "&",
        "|",
        "~",
        "<<",
        ">>",
        "MIN",
        "MAX",
        "SQRT",
        "ROUND",
        "FLOOR",
        "CEIL",
        "ABS",
        "NEG",
        "SIG",
      ],
      description: "",
    },
    op2: {
      type: ArgType.BigDecimal,
      position: 2,
      defaultBigDecimal: 0,
      description: "Second operand or scale for ROUND/FLOOR/CEIL",
    },
  },
  outputType: ArgType.BigDecimal,
};
class TransformerFunctionMath extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    var value = await context.getJsonArray(null);
    let parsedOp: string | null = null;
    let op: MathOp;
    let op1: BigNumber | null = null;
    let op2: BigNumber | null = null;
    if (value != null) {
      const size = value.length;
      if (size <= 1) return null; // invalid input
      const arg0 = value[0];
      const arg1 = value[1];
      parsedOp = getAsString(arg0);
      op = TransformerFunctionMath.parseMathOp(parsedOp);
      if (size > 2 && op == MathOp.UNKNOWN) {
        parsedOp = getAsString(arg1);
        op = TransformerFunctionMath.parseMathOp(parsedOp);
        op1 = BigDecimal(arg0);
      } else {
        op1 = BigDecimal(arg1);
      }
      op2 = size < 3 ? BigDecimal(0) : BigDecimal(value[2]);
    } else {
      // order of arguments ( op1, op, op2 )
      parsedOp = await context.getEnum("op1");
      op = TransformerFunctionMath.parseMathOp(parsedOp);
      if (op == MathOp.UNKNOWN) {
        // op was not detected as the first argument, so we assume it is in the second argument
        // -> op1, op, [op2]
        parsedOp = await context.getEnum("op");
        op = TransformerFunctionMath.parseMathOp(parsedOp);
        op1 = await context.getBigDecimal("op1");
        op2 = await context.getBigDecimal("op2");
      } else {
        var mainArgValue = await context.getUnwrapped(null);
        if (mainArgValue != null) {
          // we set operand 1 as main argument value for the sake of functions with only one operand
          // -> op, [op2] : op1
          op1 = BigDecimal(mainArgValue.toString());
          op2 = await context.getBigDecimal("op");
        } else {
          // -> op, op1, op2
          op1 = await context.getBigDecimal("op");
          op2 = await context.getBigDecimal("op2");
        }
      }
    }

    if (op == MathOp.UNKNOWN) {
      console.warn("{} was specified with an unknown op ({})", context.getAlias(), parsedOp);
      return null;
    }
    var result = TransformerFunctionMath.eval(op, op1 ?? BigZero, op2 ?? BigZero);

    if (result == null) {
      return null;
    }
    // cap scale at max
    if ((result.decimalPlaces() ?? 0) > MAX_SCALE) {
      result = result.decimalPlaces(MAX_SCALE, MAX_SCALE_ROUNDING);
    }
    return result;
  }

  static parseMathOp(value: string | null): MathOp {
    switch (value?.toUpperCase()) {
      case "+":
      case "ADD":
        return MathOp.ADDITION;
      case "-":
      case "SUB":
      case "SUBTRACT":
        return MathOp.SUBTRACTION;
      case "*":
      case "MUL":
      case "MULTIPLY":
        return MathOp.MULTIPLICATION;
      case "/":
      case "DIV":
      case "DIVIDE":
        return MathOp.DIVISION;
      case "//":
      case "INTDIV":
        return MathOp.INTEGER_DIVISION;
      case "%":
      case "MOD":
      case "REMAINDER":
        return MathOp.MODULU;
      case "^":
      case "**":
      case "POW":
      case "POWER":
        return MathOp.POWER;
      case "&":
      case "AND":
        return MathOp.BITAND;
      case "|":
      case "OR":
        return MathOp.BITOR;
      case "~":
      case "XOR":
        return MathOp.BITXOR;
      case "<<":
      case "SHL":
        return MathOp.SHIFT_LEFT;
      case ">>":
      case "SHR":
        return MathOp.SHIFT_RIGHT;
      case "MIN":
        return MathOp.MIN;
      case "MAX":
        return MathOp.MAX;
      case "SQRT":
        return MathOp.SQUARE_ROOT;
      case "ROUND":
        return MathOp.ROUND;
      case "FLOOR":
        return MathOp.FLOOR;
      case "CEIL":
        return MathOp.CEIL;
      case "ABS":
        return MathOp.ABSOLUTE;
      case "NEG":
      case "NEGATE":
        return MathOp.NEGATION;
      case "SIG":
      case "SIGNUM":
        return MathOp.SIGNUM;
      default:
        return MathOp.UNKNOWN;
    }
  }

  static eval(op: MathOp, op1: BigNumber, op2: BigNumber): BigNumber | null {
    switch (op) {
      // 2 operands
      case MathOp.ADDITION:
        return op1.plus(op2);
      case MathOp.SUBTRACTION:
        return op1.minus(op2);
      case MathOp.MULTIPLICATION:
        return op1.multipliedBy(op2);
      case MathOp.DIVISION:
        return op1.dividedBy(op2);
      case MathOp.INTEGER_DIVISION:
        return op1.dividedToIntegerBy(op2);
      case MathOp.MODULU:
        return op1.mod(op2);
      case MathOp.POWER:
        return op1.pow(op2);
      case MathOp.MIN:
        return BigNumber.min(op1, op2);
      case MathOp.MAX:
        return BigNumber.max(op1, op2);
      // only one operand
      case MathOp.SQUARE_ROOT:
        return op1.sqrt();
      case MathOp.ROUND:
        return op1.decimalPlaces(op2.integerValue().toNumber(), BigNumber.ROUND_HALF_UP);
      case MathOp.FLOOR:
        return op1.decimalPlaces(op2.integerValue().toNumber(), BigNumber.ROUND_FLOOR);
      case MathOp.CEIL:
        return op1.decimalPlaces(op2.integerValue().toNumber(), BigNumber.ROUND_CEIL);
      case MathOp.ABSOLUTE:
        return op1.abs();
      case MathOp.NEGATION:
        return op1.negated();
      case MathOp.SIGNUM:
        return BigDecimal(op1.isZero() ? 0 : op1.isPositive() ? 1 : -1);
      // bitwise
      case MathOp.BITAND:
        return BigDecimal((toBigInt(op1) & toBigInt(op2)).toString());
      case MathOp.BITOR:
        return BigDecimal((toBigInt(op1) | toBigInt(op2)).toString());
      // special case where only 1 op (~x) acts as NOT (op2 acts like ~0)
      case MathOp.BITXOR:
        return isNullOrUndefined(op2)
          ? BigDecimal(~toBigInt(op1).toString())
          : BigDecimal((toBigInt(op1) ^ toBigInt(op2)).toString());
      case MathOp.SHIFT_LEFT:
        return BigDecimal((toBigInt(op1) << toBigInt(op2)).toString());
      case MathOp.SHIFT_RIGHT:
        return BigDecimal((toBigInt(op1) >> toBigInt(op2)).toString());
      default:
        return null;
    }
  }
}

export default TransformerFunctionMath;
