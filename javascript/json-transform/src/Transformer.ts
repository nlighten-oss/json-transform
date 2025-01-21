export interface Transformer {
  transform(input: any, additionalContext: Record<string, any>) : any;
}

export const RAW: Transformer = {
  transform(input: any, additionalContext: Record<string, any>): any {
    return input;
  }
};