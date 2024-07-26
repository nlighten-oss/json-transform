import {ArgumentType} from "./ArgumentType";
import {ArgType} from "./ArgType";

export type FunctionDescription = {
  alias: string;
  deprecatedAlias?: string;
  description?: string;
  notes?: string;
  inputType?: ArgType;
  arguments?: Record<string, ArgumentType>;
  outputType?: ArgType;
  pipedType?: boolean;
  // should be internally set to `true` if registered by client
  custom?: boolean;
}