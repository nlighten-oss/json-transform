import {ArgType} from "./ArgType";

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
  defaultBigDecimal?: number; // default -1

  aliases?: string[]; // default {};
}