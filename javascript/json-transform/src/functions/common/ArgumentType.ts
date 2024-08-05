import {ArgType} from "./ArgType";
import {BigDecimal} from "./FunctionHelpers";

export type ArgumentType = {
  description: string;
  enumValues?: string[]; // default []
  type: ArgType; // ArgType
  position?: number; // default -1
  required?: boolean; // default false

  defaultIsNull?: boolean // default false
  defaultBoolean?: boolean // default false
  defaultString?: string // default ""
  defaultEnum?: string // default ""
  defaultInteger?: number; // default -1
  defaultLong?: number; // default -1L
  defaultBigDecimal?: number | string; // default -1

  aliases?: string[]; // default {};
}