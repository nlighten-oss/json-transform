import { BigDecimal } from "../common/FunctionHelpers";
import BigNumber from "bignumber.js";

/**
 * Base36Or62 is a utility class that provides methods to encode and decode numbers using Base36 or Base62 encoding.
 */
class Base36Or62 {
  static readonly BASE_62 = BigDecimal(62);
  static readonly DIGITS_62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  /**
   * Encodes a number using Base36 encoding.
   *
   * @param number a positive integer
   * @param base62 whether to use Base62 or Base36
   * @return a Base36 string
   *
   * @throws IllegalArgumentException if <code>number</code> is a negative integer
   */
  public static encode(number: BigNumber, base62: boolean) {
    if (number.isNegative()) {
      throw new Error("number must not be negative");
    }
    if (!base62) {
      return number.toString(36);
    }

    const base = Base36Or62.BASE_62;
    const digits = Base36Or62.DIGITS_62;

    let result = "";
    let num = number;
    while (num.isPositive()) {
      const rem = num.modulo(base);
      num = num.dividedToIntegerBy(base);
      result = digits.charAt(rem.toNumber()) + result;
    }
    return result.length == 0 ? digits[0] : result;
  }

  /**
   * Decodes a string using Base36 encoding.
   *
   * @param str a Base36 string
   * @param base62 whether to use Base62 or Base36
   * @return a positive integer
   *
   * @throws IllegalArgumentException if <code>string</code> is empty
   */
  public static decode(str: string, base62: boolean) {
    if (!str) {
      throw new Error("String must not be empty/null");
    }

    if (!base62) {
      return BigDecimal(str, 36).integerValue(BigNumber.ROUND_FLOOR);
    }

    const base = Base36Or62.BASE_62;
    const digits = Base36Or62.DIGITS_62;

    return Array.from({ length: str.length })
      .map((_, i) => {
        return BigDecimal(digits[str.charCodeAt(str.length - i - 1)]).multipliedBy(base.pow(i));
      })
      .reduce((acc, value) => acc.plus(value), BigDecimal(0));
  }
}

export default Base36Or62;
