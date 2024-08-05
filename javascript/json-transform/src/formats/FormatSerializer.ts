export interface FormatSerializer {
  serialize(payload: any): string | null;
}
