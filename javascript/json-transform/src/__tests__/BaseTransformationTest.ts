import JsonTransformer from "../JsonTransformer";
import {expect} from "vitest";

export const assertTransformation = (payload: any, definition: any, output: any, additionalContext: any = {}) => {
  const x = new JsonTransformer(definition)
  expect(x.transform(payload, additionalContext)).toEqual(output);
}

export const assertFailTransformation = (payload: any, definition: any, output: any, additionalContext: any = {}) => {
  const x = new JsonTransformer(definition)
  expect(x.transform(payload, additionalContext)).not.toEqual(output);
}