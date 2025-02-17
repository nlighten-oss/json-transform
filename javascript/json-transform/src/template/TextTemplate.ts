import ParameterDefaultResolveOptions from "./ParameterDefaultResolveOptions";
import { isParameterResolver, ParameterResolver, parameterResolverFromMap } from "../ParameterResolver";
import TemplateParameter from "./TemplateParameter";

const STATE_TEXT = 0;
const STATE_PARAM_NAME = 1;
const STATE_PARAM_DEFAULT = 2;
const UNESCAPED_OPEN_CURLY_BRACKET = /(?<!\\)\{/;

export default class TextTemplate {
  private static readonly cached: Record<string, TextTemplate> = {};
  private readonly templateParameters: TemplateParameter[] = [];

  private readonly defaultResolver: ParameterDefaultResolveOptions;
  /**
   * internal list of values
   */
  private readonly values: any[] = [];

  /**
   * Creates a new memory template for a string
   *
   * @param template         The template text
   * @param defaultResolver defines how the template should resolve parameter default values
   */
  constructor(template: string, defaultResolver = ParameterDefaultResolveOptions.UNIQUE) {
    this.defaultResolver = defaultResolver;
    this.parse(template, defaultResolver);
  }

  /**
   * Exposes a list copy to inspect the list of parameters
   *
   * @return a list of parameters in the template
   */
  public getParameters() {
    return this.templateParameters.slice();
  }

  /**
   * gets or creates a template from the cache
   *
   * @param template         the command to parse
   * @param defaultResolver defines how the template should resolve parameter default values
   * @return a new text template
   */
  public static get(template: string, defaultResolver = ParameterDefaultResolveOptions.UNIQUE) {
    let tpl: TextTemplate;
    const key =
      ParameterDefaultResolveOptions.UNIQUE === defaultResolver
        ? template // the common case
        : defaultResolver.toString() + "{" + template;
    tpl = TextTemplate.cached[key];
    if (tpl == null) {
      tpl = new TextTemplate(template, defaultResolver);
      TextTemplate.cached[key] = tpl;
    }
    return tpl;
  }

  private parse(template: string, defaultResolver: ParameterDefaultResolveOptions) {
    let curleyOpen = 0; //, bracketOpen = 0;
    let buffer = "";
    let state = 0;
    //internal storage for found params
    const templateParams: Record<string, TemplateParameter> = {};
    let pName: string | null = null,
      pValue: any = null;

    for (let i = 0; i < template.length; i++) {
      let c = template.charAt(i);
      switch (c) {
        case "\\":
          if (i + 1 < template.length) {
            var nextChar = template.charAt(i + 1);
            if (nextChar == "{") {
              i++; // skip backslash
              buffer += "\\{";
              continue;
            }
          }
          break;
        case "{":
          curleyOpen++;
          if (state == STATE_TEXT) {
            //start of parameter flush old
            if (buffer) {
              this.values.push(buffer);
              buffer = "";
            }
            state = STATE_PARAM_NAME;
            //we don't need to add to buffer
            continue;
          }
          break;
        case "}":
          // if there is no open bracket, then treat it as a regular character
          if (curleyOpen > 0) {
            curleyOpen--;

            if (curleyOpen != 0) {
            } else {
              // close off
              switch (state) {
                case STATE_PARAM_NAME:
                  pName = buffer.replace("\\{", "{");
                  break;
                case STATE_PARAM_DEFAULT:
                  pValue = buffer;
                  break;
              }
              buffer = "";

              state = STATE_TEXT;
              if (pName) {
                let tParam: TemplateParameter;
                if (
                  defaultResolver != ParameterDefaultResolveOptions.UNIQUE &&
                  Object.prototype.hasOwnProperty.call(templateParams, pName)
                ) {
                  tParam = templateParams[pName];
                  if (pValue != null) {
                    if (tParam.getDefault() == null || defaultResolver == ParameterDefaultResolveOptions.LAST_VALUE) {
                      tParam.setDefault(pValue);
                    }
                  }
                } else {
                  tParam = new TemplateParameter(pName, pValue);
                  templateParams[pName] = tParam;
                }
                this.templateParameters.push(tParam);
                this.values.push(tParam);
              }
              pName = pValue = null;
              continue;
            }
          }
          break;
        case ",":
          if (state == STATE_PARAM_NAME) {
            state = STATE_PARAM_DEFAULT;
            pName = buffer;
            buffer = "";
            continue;
          }
      }
      // must be a valid char
      buffer += c;
    }
    if (buffer) this.values.push(buffer);
  }

  private async internalRender(resolver: ParameterResolver) {
    let sb = "";
    for (const value of this.values) {
      if (typeof value === "string") {
        sb += value;
      } else if (value instanceof TemplateParameter) {
        // escape param value
        sb += await value.getStringValue(resolver);
      }
    }
    return sb;
  }

  /**
   * Renders the template after inserting the parameters
   *
   * @param resolver A resolver to extract parameter values
   * @return a string with its parameters replaced
   */
  public async render(resolver: Record<string, string> | ParameterResolver) {
    const resl = isParameterResolver(resolver) ? resolver : parameterResolverFromMap(resolver);
    let res = await this.internalRender(resl);
    while (UNESCAPED_OPEN_CURLY_BRACKET.test(res)) {
      res = await TextTemplate.get(res, this.defaultResolver).internalRender(resl);
    }
    // unescape
    return res.replace("\\{", "{").replace("\\}", "}");
  }

  public static render(
    template: string,
    resolver: Record<string, string> | ParameterResolver,
    defaultResolver = ParameterDefaultResolveOptions.UNIQUE,
  ) {
    return TextTemplate.get(template, defaultResolver).render(resolver);
  }
}
