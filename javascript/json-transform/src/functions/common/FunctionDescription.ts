import { ArgumentType } from "./ArgumentType";
import { ArgType } from "./ArgType";

export type FunctionDescription = {
  arguments?: Record<string, ArgumentType>;
  // should be internally set to `true` if registered by client
  custom?: boolean;
};
