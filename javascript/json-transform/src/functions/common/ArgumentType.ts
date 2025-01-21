import { ArgType } from "./ArgType";
import { BigDecimal } from "./FunctionHelpers";

export type ArgumentType = {
  type: ArgType; // ArgType
  position?: number; // default -1

  defaultIsNull?: boolean; // default false
  defaultBoolean?: boolean; // default false
  defaultString?: string; // default ""
  defaultEnum?: string; // default ""
  defaultInteger?: number; // default -1
  defaultLong?: number; // default -1L
  defaultBigDecimal?: number | string; // default -1

  aliases?: string[]; // default {};
};
