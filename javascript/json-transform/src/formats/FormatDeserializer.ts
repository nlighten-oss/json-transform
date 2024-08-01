export interface FormatDeserializer {
  deserialize(input: string): Record<string, any>;
}