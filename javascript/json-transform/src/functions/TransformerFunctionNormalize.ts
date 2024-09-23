import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

type NormalizerForm = "NFC" | "NFD" | "NFKC" | "NFKD";

const Z_Separators = new RegExp("[ \u00A0\u1680\u2000-\u200A\u2028\u2029\u202F\u205F\u3000]", "g");
const InCombiningDiacriticalMarks = /[̀-ͯ]+/g;
const LetterModifier_Partial = /[·ʰ-ˁˆ-ˑˠ-ˤˬˮʹͺᴬ-ᵪⸯ々ꙿꚜ-ꚝꜗ-ꜟꝰꞈꟲ-ꟴꟸ-ꟹꭜ-ꭟꭩ]+/g;

class TransformerFunctionNormalize extends TransformerFunction {
  // * source strings (ends with _S) are after decomposition and removal of marks
  // ** if target character is more than one letter,
  //    use _ instead and add it as a separate mapping in the exceptions map (ends with _E)
  static readonly LatinExANonComposed_S = "ĐđĦħŁłŊŋŒœŦŧ";
  static readonly LatinExANonComposed_T = "DdHhLlNn__Tt";
  static readonly LatinExANonComposed_E = Object.fromEntries([
    ["Œ", "OE"],
    ["œ", "oe"],
  ]);
  static readonly LatinExBNonComposed_S = "ƀƁƄƅƆƇƈƉƊƑƒƓƕƗƘƙƚOoƤƥƦƧƨƫƬƭƳƴƵƶÆæǤǥǶǷØøȡȤȥȴȵȶȺȻȼȽȾȿɀɃɄɅɆɇɈɉɊɋɌɍɎɏ";
  static readonly LatinExBNonComposed_T = "bBbbCCcDDFfGhIKklOoPpRSstTtYyZz__GgHpOodZzlntACcLTszBUAEeJjQqRrYy";
  static readonly LatinExBNonComposed_E = Object.fromEntries([
    ["Æ", "AE"],
    ["æ", "ae"],
  ]);
  static readonly Latin1SNonComposed_S = "Ð";
  static readonly Latin1SNonComposed_T = "D";
  static readonly Latin1SNonComposed_E = {};

  static ReplacementsPattern: RegExp;
  static ReplacementsMap: Record<string, string>;

  static {
    const sthis = TransformerFunctionNormalize;
    sthis.ReplacementsPattern = new RegExp(
      `[${sthis.LatinExANonComposed_S}${sthis.LatinExBNonComposed_S}${sthis.Latin1SNonComposed_S}]`,
      "gu",
    );
    const entries: [string, string][] = [];
    sthis.addAllEntries(entries, sthis.LatinExANonComposed_S, sthis.LatinExANonComposed_T, sthis.LatinExANonComposed_E);
    sthis.addAllEntries(entries, sthis.LatinExBNonComposed_S, sthis.LatinExBNonComposed_T, sthis.LatinExBNonComposed_E);
    sthis.addAllEntries(entries, sthis.Latin1SNonComposed_S, sthis.Latin1SNonComposed_T, sthis.Latin1SNonComposed_E);

    sthis.ReplacementsMap = Object.fromEntries(entries);
  }

  static addAllEntries(
    entries: [string, string][],
    source: string,
    target: string,
    exceptions: Record<string, string>,
  ) {
    const sourceArray = Array.from(source);
    const targetArray = Array.from(target);
    for (let i = 0; i < sourceArray.length; i++) {
      const sourceChar = sourceArray[i];
      const targetChar = targetArray[i];
      if (targetChar !== "_") {
        entries.push([sourceChar, targetChar]);
      } else if (Object.prototype.hasOwnProperty.call(exceptions, sourceChar)) {
        entries.push([sourceChar, exceptions[sourceChar]]);
      }
    }
  }

  constructor() {
    super({
      arguments: {
        form: { type: ArgType.Enum, position: 0, defaultEnum: "NFKD" },
        post_operation: { type: ArgType.Enum, position: 1, defaultEnum: "ROBUST" },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    let str = await context.getString(null);
    if (str == null) {
      return null;
    }
    if (str.trim() === "") return str; // short-circuit for blank strings
    const normalizerForm = (await context.getEnum("form")) as NormalizerForm;
    str = str.normalize(normalizerForm);

    var postOperation = await context.getEnum("post_operation");
    if (postOperation?.toUpperCase() === "ROBUST") {
      // replace separators with space (and remove leading and trailing white spaces)
      str = str.replace(Z_Separators, " ").trim();
      // remove all diacritical marks
      str = str.replace(InCombiningDiacriticalMarks, "");
      str = str.replace(LetterModifier_Partial, ""); // solves decomposition of ŉ Ŀ ŀ
      // do other replacements
      str = str?.replace(
        TransformerFunctionNormalize.ReplacementsPattern,
        (match, offset, string, groups) => TransformerFunctionNormalize.ReplacementsMap[match],
      );
    }

    return str;
  }
}

export default TransformerFunctionNormalize;
