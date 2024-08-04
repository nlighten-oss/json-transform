import JsonTransformer from "../JsonTransformer";
import { expect } from "vitest";

export const assertTransformation = async (payload: any, definition: any, output: any, additionalContext: any = {}) => {
  const x = new JsonTransformer(definition);
  expect(await x.transform(payload, additionalContext)).toEqual(output);
};

export const assertFailTransformation = async (
  payload: any,
  definition: any,
  output: any,
  additionalContext: any = {},
) => {
  const x = new JsonTransformer(definition);
  expect(await x.transform(payload, additionalContext)).not.toEqual(output);
};
