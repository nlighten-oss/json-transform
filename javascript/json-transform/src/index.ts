import { JsonTransformer } from "./JsonTransformer";
import DocumentContext from "./DocumentContext";
import JsonElementStreamer from "./JsonElementStreamer";
import { JsonTransformerFunction } from "./JsonTransformerFunction";
import { ParameterResolver, isParameterResolver, parameterResolverFromMap } from "./ParameterResolver";
import { Transformer, RAW as RawTransformer } from "./Transformer";
import * as JsonHelpers from "./JsonHelpers";
import type { JsonMergeOptions } from "./JsonHelpers";

// Functions API
import { TransformerFunctions } from "./TransformerFunctions";
import DebuggableTransformerFunctions from "./DebuggableTransformerFunctions";
import { ArgType } from "./functions/common/ArgType";
import { ArgumentType } from "./functions/common/ArgumentType";
import CompareBy from "./functions/common/CompareBy";
import FunctionContext from "./functions/common/FunctionContext";
import { FunctionDescription } from "./functions/common/FunctionDescription";
import * as FunctionHelpers from "./functions/common/FunctionHelpers";
import InlineFunctionContext from "./functions/common/InlineFunctionContext";
import ObjectFunctionContext from "./functions/common/ObjectFunctionContext";
import TextEncoding from "./functions/common/TextEncoding";
import TransformerFunction from "./functions/common/TransformerFunction";
// Functions Utils
import Base36Or62 from "./functions/utils/Base36Or62";
import Base64 from "./functions/utils/Base64";
import javaSplit from "./functions/utils/javaSplit";
import md5 from "./functions/utils/md5";
import ShortUuid from "./functions/utils/ShortUuid";
// Formats
import CsvFormat from "./formats/csv/CsvFormat";
import FormUrlEncodedFormat from "./formats/formurlencoded/FormUrlEncodedFormat";
import { FormatDeserializer } from "./formats/FormatDeserializer";
import { FormatSerializer } from "./formats/FormatSerializer";
// TextTemplate
import TextTemplate from "./template/TextTemplate";
import { TextTemplateJsonTransformFunctionInstance } from "./template/TextTemplateJsonTransformer";
import { setCurrentJsonTransformFunction } from "./template/TextTemplateJsonTransformFunction";
import ParameterDefaultResolveOptions from "./template/ParameterDefaultResolveOptions";
import TemplateParameter from "./template/TemplateParameter";

// Dependency Injection
setCurrentJsonTransformFunction(TextTemplateJsonTransformFunctionInstance);

export {
  JsonTransformer,
  DocumentContext,
  JsonElementStreamer,
  JsonTransformerFunction,
  ParameterResolver,
  isParameterResolver,
  parameterResolverFromMap,
  Transformer,
  RawTransformer,
  JsonHelpers,
  JsonMergeOptions,
  TransformerFunctions,
  DebuggableTransformerFunctions,
  ArgType,
  ArgumentType,
  CompareBy,
  FunctionContext,
  FunctionDescription,
  FunctionHelpers,
  InlineFunctionContext,
  ObjectFunctionContext,
  TextEncoding,
  TransformerFunction,
  Base36Or62,
  Base64,
  javaSplit,
  md5,
  ShortUuid,
  CsvFormat,
  FormUrlEncodedFormat,
  FormatDeserializer,
  FormatSerializer,
  TextTemplate,
  ParameterDefaultResolveOptions,
  TemplateParameter,
};
