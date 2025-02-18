import { ParameterResolver } from "../ParameterResolver";
import { getCurrentJsonTransformFunction } from "./TextTemplateJsonTransformFunction";

export default class TemplateParameter {
  private readonly name: string;
  private mDefault: string;

  getDefault() {
    return this.mDefault;
  }

  setDefault(value: string) {
    this.mDefault = value;
  }

  constructor(name: string, defaultValue: string) {
    this.name = name;
    this.mDefault = defaultValue == null ? "" : defaultValue;
  }

  async getStringValue(resolver: ParameterResolver) {
    if (resolver == null) {
      return null;
    }
    let val = resolver.get(this.name);

    if (this.name.startsWith("$$") && (this.name === val || val == null)) {
      val = await getCurrentJsonTransformFunction().call(null, this.name, resolver);
    }

    return val == null ? this.mDefault : val.replace(/\{/g, "\\{");
  }
}
