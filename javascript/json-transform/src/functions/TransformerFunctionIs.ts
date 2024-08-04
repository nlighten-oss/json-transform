import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { isEqual } from "../JsonHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["is"],
  description: "",
  inputType: ArgType.Any,
  arguments: {
    op: {
      type: ArgType.Enum,
      position: 0,
      defaultIsNull: true,
      enumValues: ["IN", "NIN", "EQ", "=", "==", "NEQ", "!=", "<>", "GT", ">", "GTE", ">=", "LT", "<", "LTE", "<="],
      description: "A type of check to do exclusively (goes together with `that`)",
    },
    that: {
      type: ArgType.Any,
      position: 1,
      defaultIsNull: true,
      description: "A transformer to extract a property to sum by (using ##current to refer to the current item)",
    },
    in: {
      type: ArgType.Array,
      defaultIsNull: true,
      description: "Array of values the input should be part of",
    },
    nin: {
      type: ArgType.Array,
      defaultIsNull: true,
      description: "Array of values the input should **NOT** be part of",
    },
    eq: {
      type: ArgType.Any,
      defaultIsNull: true,
      description: "A Value the input should be equal to",
    },
    neq: {
      type: ArgType.Any,
      defaultIsNull: true,
      description: "A Value the input should **NOT** be equal to",
    },
    gt: {
      type: ArgType.Any,
      defaultIsNull: true,
      description: "A Value the input should be greater than (input > value)",
    },
    gte: {
      type: ArgType.Any,
      defaultIsNull: true,
      description: "A Value the input should be greater than or equal (input >= value)",
    },
    lt: {
      type: ArgType.Any,
      defaultIsNull: true,
      description: "A Value the input should be lower than (input < value)",
    },
    lte: {
      type: ArgType.Any,
      defaultIsNull: true,
      description: "A Value the input should be lower than or equal (input <= value)",
    },
  },
  outputType: ArgType.Boolean,
};
class TransformerFunctionIs extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElement(null);
    if (context.has("op")) {
      const op = await context.getEnum("op");
      let that: any = null;
      // if operator is not in/nin then prepare the "that" argument which is a JsonElement
      if (op !== "IN" && op !== "NIN") {
        that = await (context.isJsonNumber(value) ? context.getBigDecimal("that") : context.getJsonElement("that"));
      }
      switch (op) {
        case "IN": {
          const _in = await context.getJsonElementStreamer("that");
          return _in != null && (await _in.stream().any(item => isEqual(item, value)));
        }
        case "NIN": {
          var nin = await context.getJsonElementStreamer("that");
          return nin != null && (await nin.stream().none(item => isEqual(item, value)));
        }
        case "GT":
        case ">": {
          const comparison = context.compareTo(value, that);
          return comparison != null && comparison > 0;
        }
        case "GTE":
        case ">=": {
          const comparison = context.compareTo(value, that);
          return comparison != null && comparison >= 0;
        }
        case "LT":
        case "<": {
          const comparison = context.compareTo(value, that);
          return comparison != null && comparison < 0;
        }
        case "LTE":
        case "<=": {
          const comparison = context.compareTo(value, that);
          return comparison != null && comparison <= 0;
        }
        case "EQ":
        case "=":
        case "==": {
          return value === that;
        }
        case "NEQ":
        case "!=":
        case "<>": {
          return value !== that;
        }
        default: {
          return false;
        }
      }
    }
    let result = true;
    if (context.has("in")) {
      const _in = await context.getJsonElementStreamer("in");
      result = _in != null && (await _in.stream().any(item => isEqual(item, value)));
    }
    if (result && context.has("nin")) {
      const nin = await context.getJsonElementStreamer("nin");
      result = nin != null && (await nin.stream().none(item => isEqual(item, value)));
    }
    if (result && context.has("gt")) {
      const gt = await context.getJsonElement("gt");
      const comparison = context.compareTo(value, gt);
      result = comparison != null && comparison > 0;
    }
    if (result && context.has("gte")) {
      const gte = await context.getJsonElement("gte");
      const comparison = context.compareTo(value, gte);
      result = comparison != null && comparison >= 0;
    }
    if (result && context.has("lt")) {
      const lt = await context.getJsonElement("lt");
      const comparison = context.compareTo(value, lt);
      result = comparison != null && comparison < 0;
    }
    if (result && context.has("lte")) {
      const lte = await context.getJsonElement("lte");
      const comparison = context.compareTo(value, lte);
      result = comparison != null && comparison <= 0;
    }
    if (result && context.has("eq")) {
      const eq = await context.getJsonElement("eq");
      result = value === eq;
    }
    if (result && context.has("neq")) {
      const neq = await context.getJsonElement("neq");
      result = value !== neq;
    }
    return result;
  }
}

export default TransformerFunctionIs;
