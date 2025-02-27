import yaml, { DumpOptions } from "js-yaml";
import { FormatSerializer } from "../FormatSerializer";
import { FormatDeserializer } from "../FormatDeserializer";

const replacer = (key: string, value: any) => {
  if (value._isBigNumber) {
    const asNumber = value.toNumber();
    if (asNumber.toString() === value.toString()) {
      return asNumber;
    }
    return value.toJSON();
  }
  return value;
};

export default class YamlFormat implements FormatSerializer, FormatDeserializer {
  static readonly dumpOptions: DumpOptions = { replacer };

  static readonly INSTANCE = new YamlFormat();

  deserialize(input: string | null) {
    if (input === null) {
      return null;
    }
    return yaml.load(input);
  }
  serialize(payload: any): string | null {
    return yaml.dump(payload, YamlFormat.dumpOptions);
  }
}
