import FunctionContext from "./FunctionContext";

class InlineFunctionContext extends FunctionContext {

  private stringInput: string | null;
  private args: any[];

  constructor(input: string | null, args: any[], functionKey: string, func: any, resolver: any, extractor: any) {
    super(functionKey, func, resolver, extractor, null);
    this.stringInput = input;
    this.args = args;
  }

  override has(name: string): boolean {
    const argument = this.function.getArgument(name);
    return name == null || (argument != null && (argument.position ?? -1) > -1 && this.args != null && this.args.length > (argument.position ?? -1));
  }

  override get(name: string, transform: boolean = true): any {
    const argument = this.function.getArgument(name);
    if (name != null && (argument == null || (argument.position ?? -1) < 0 || this.args == null || this.args.length <= (argument.position ?? -1))) {
      return this.function.getDefaultValue(name);
    }
    var argValue = name == null ? this.stringInput : this.args[argument?.position ?? -1];
    if (typeof argValue === 'string' && transform) {
      return this.extractor.transform(argValue, this.resolver, true);
    }
    return transform
      ? this.extractor.transform(argValue, this.resolver, true)
      : argValue;
  }
}

export default InlineFunctionContext;