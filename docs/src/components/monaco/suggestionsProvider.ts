import { TypeSchema } from "@nlighten/json-schema-utils";

type SuggestionsResult = [string[], Record<string, TypeSchema>];

const suggestions: Record<string, SuggestionsResult> = {};

export const setSuggestions = (key: string, variables: string[], paths: Record<string, TypeSchema>) => {
  suggestions[key] = [variables, paths];
};

const EmptySuggestionsResult: SuggestionsResult = [[], {}];

export const getSuggestions = (key: string, ignoreErrors?: boolean): SuggestionsResult => {
  key = key.replace(/__\w+__/, "");
  if (!suggestions[key]) {
    !ignoreErrors && console.warn(`Could not find suggestions for key: ${key}`);
    //console.debug(`available keys are: ${Object.keys(suggestions)}`);
    return EmptySuggestionsResult;
  }
  return suggestions[key];
};

export const resolveSuggestions = (path: string) => {
  return getSuggestions("/" + path, true)[1];
};
