import { v4 as uuidv4, parse } from "uuid";
import Base36Or62 from "./Base36Or62";

const UUID_REGEX = /^(.{8})(.{4})(.{4})(.{4})(.{12})$/;

/**
 * ShortUuid is a class that can be used to encode and decode UUIDs to a shorter string representation.
 */
class ShortUuid {
  static readonly UUID_BITS = 128;

  /**
   * Create a ShortUuid
   *
   * @param caseSensitive whether the id can be case-sensitive (in this case use a larger base to encode the id)
   * @return ShortUuid encoded UUID
   */
  public static random(caseSensitive: boolean) {
    return ShortUuid.toShortUuid(uuidv4(), caseSensitive);
  }

  /**
   * Encode UUID to ShortUuid
   *
   * @param uuid UUID to be encoded
   * @param caseSensitive whether the id can be case-sensitive (in this case use a larger base to encode the id)
   * @return ShortUuid encoded UUID
   */
  public static toShortUuid(uuid: string, caseSensitive: boolean) {
    const num = BigInt("0x" + uuid.replace(/-/g, ""));
    return Base36Or62.encode(num, caseSensitive);
  }

  /**
   * Decode ShortUuid to UUID
   *
   * @param shortId encoded UUID
   * @param caseSensitive whether the id can be case-sensitive (in this case use a larger base to encode the id)
   * @return decoded UUID
   */
  public static toUuid(shortId: string, caseSensitive: boolean) {
    const decoded = Base36Or62.decode(shortId, caseSensitive);
    const paddedBigIntAsHex = decoded.toString(16).padStart(32, "0");
    return paddedBigIntAsHex.replace(UUID_REGEX, "$1-$2-$3-$4-$5");
  }

  /**
   * Try to shorten an identifier, if it is a UUID it will be converted to Base36.
   * Otherwise, all non-alphanumeric characters will be removed
   *
   * @param id An identifier to short
   * @param caseSensitive whether the id can be case-sensitive (in this case use a larger base to encode the id)
   * @return The shortened string
   */
  public static tryShorteningId(id: string, caseSensitive: boolean) {
    try {
      parse(id); // throws if not valid
      return ShortUuid.toShortUuid(id, caseSensitive);
    } catch (ignored: any) {
      return id.replace(/[^A-Za-z0-9]+/g, "");
    }
  }
}

export default ShortUuid;
