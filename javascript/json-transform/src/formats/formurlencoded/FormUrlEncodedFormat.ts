import { FormatSerializer } from "../FormatSerializer";
import { FormatDeserializer } from "../FormatDeserializer";
import { isNullOrUndefined } from "../../JsonHelpers";

class FormUrlEncodedFormat implements FormatSerializer, FormatDeserializer {
  serialize(payload: any): string | null {
    if (isNullOrUndefined(payload)) {
      return null;
    }
    const params = new URLSearchParams();
    for (const key in payload) {
      if (Object.prototype.hasOwnProperty.call(payload, key)) {
        const value = payload[key];
        if (Array.isArray(value)) {
          for (const item of value) {
            params.append(key, item);
          }
        } else {
          params.append(key, value);
        }
      }
    }
    return params.toString();
  }

  deserialize(input: string): any {
    if (isNullOrUndefined(input)) {
      throw new Error("Input is null");
    }
    const params = new URLSearchParams(input);
    const result: Record<string, any> = {};
    for (const key of params.keys()) {
      const value = params.getAll(key);
      result[key] = value[0] || "true";
      result[key + "$$"] = value.map(v => v || "true");
    }
    return result;
  }
}

export default FormUrlEncodedFormat;
