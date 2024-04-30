import { Dispatch, SetStateAction, useMemo, useState } from "react";

type JSONStringHookReturnType = [string | undefined, Dispatch<SetStateAction<string | undefined>>, any, any];

const useJSONString = (defaultValue: string = "{}"): JSONStringHookReturnType => {
  const [codeString, setCodeString] = useState<string | undefined>(defaultValue);

  return useMemo(() => {
    let parsedObject: any, error: any;
    try {
      parsedObject = codeString ? JSON.parse(codeString) : undefined;
    } catch (e) {
      error = e;
    }
    return [codeString, setCodeString, parsedObject, error];
  }, [codeString]);
};

export default useJSONString;
