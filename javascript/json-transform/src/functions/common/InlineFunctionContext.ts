import FunctionContext from "./FunctionContext";

class InlineFunctionContext extends FunctionContext {
  private stringInput: string | null;
  private args: any[];

  private constructor(
    path: string,
    input: string | null,
    args: any[],
    functionKey: string,
    func: any,
    resolver: any,
    extractor: any,
  ) {
    super(path, functionKey, func, resolver, extractor);
    this.stringInput = input;
    this.args = args;
  }

  public static create(
    path: string,
    input: string | null,
    args: any[],
    functionKey: string,
    func: any,
    resolver: any,
    extractor: any,
  ) {
    return new InlineFunctionContext(path, input, args, functionKey, func, resolver, extractor);
  }

  override has(name: string): boolean {
    const argument = this.function.getArgument(name);
    return (
      name == null ||
      (argument != null &&
        (argument.position ?? -1) > -1 &&
        this.args != null &&
        this.args.length > (argument.position ?? -1))
    );
  }

  override async get(name: string, transform: boolean = true): Promise<any> {
    const argument = this.function.getArgument(name);
    if (
      name != null &&
      (argument == null ||
        (argument.position ?? -1) < 0 ||
        this.args == null ||
        this.args.length <= (argument.position ?? -1))
    ) {
      return this.function.getDefaultValue(name);
    }
    const argValue = name == null ? this.stringInput : this.args[argument?.position ?? -1];
    if (typeof argValue === "string" && transform) {
      return await this.extractor.transform(this.getPathFor(name), argValue, this.resolver, true);
    }
    return !transform ? argValue : await this.extractor.transform(this.getPathFor(name), argValue, this.resolver, true);
  }

  override getPathFor(key: number | string | null): string {
    return this.path + (key == null ? "" : `(${key})`);
  }
}

export default InlineFunctionContext;
