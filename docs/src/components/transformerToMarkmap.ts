import {IPureNode} from "markmap-common";
import { functionsParser } from "@nlighten/json-transform-core";

const valueStringify = (value: any) => {
  return `<b>${JSON.stringify(value)}</b>`;
}
const funcStringify = (value: any) => {
  return `<span style="color:var(--markmap-keyword-color)"><i><b>${value}</b></i></span>`;
}

const isPrimitive = (value: any) => {
  return (typeof value === "string" && !value.startsWith("$$")) ||
    typeof value === "boolean" ||
    typeof value === "number" ||
    value === null;
}

export default function transformerToMarkmap(value: any, key: string): IPureNode {
  const valueIsPrimitive = isPrimitive(value);
  const node : IPureNode = {
    content: key === "*" && !valueIsPrimitive
      ? "*"
      : valueIsPrimitive
        ? `${key} = ${valueStringify(value)}`
        : key.startsWith("$$")
          ? funcStringify(key)
          : key,
    children: []
  }
  if (valueIsPrimitive) {
    return node;
  }
  if (typeof value === "string") {
    if (value.startsWith("$$")) {
      let foundFunc: string, foundInlineFunctionValue: string, foundArgs: Record<string, any>;
      functionsParser.matchInline(value, (funcName, func, inlineFunctionValue, args) => {
        foundFunc = funcName;
        foundInlineFunctionValue = inlineFunctionValue;
        foundArgs = args;
      });
      if (foundFunc) {
        node.content = `${node.content} = ${funcStringify("$$" + foundFunc)}`;
        if (foundArgs) {
          for (const argKey in foundArgs) {
            node.children.push(transformerToMarkmap(foundArgs[argKey], argKey));
          }
        }
        if (foundInlineFunctionValue) {
          node.children.push(transformerToMarkmap(foundInlineFunctionValue, ""));
        }
      } else {
        node.children.push({content: JSON.stringify(value), children: []});
      }
    } else {
      node.children.push({content: JSON.stringify(value), children: []});
    }
  } else if (Array.isArray(value)) {
    node.children = value.map((item, index) => {
      return transformerToMarkmap(item, `[${index}]`);
    });
  } else if (typeof value === "object" && value !== null) {
    const func = functionsParser.matchObject(value);
    if (func) {
      for (const key in value) {
        if (key === "$$" + func.name) continue;
        node.children.push(transformerToMarkmap(value[key], key));
      }
      node.children.push(transformerToMarkmap(value["$$" + func.name], "$$" + func.name));
    } else {
      for (const key in value) {
        node.children.push(transformerToMarkmap(value[key], key));
      }
    }
  }
  return node;
}