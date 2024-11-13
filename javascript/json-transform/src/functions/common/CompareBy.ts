import { Comparator, ComparatorFactory } from "@wortise/sequency";
import { compareTo, getAsString, isNullOrUndefined, isNumberType } from "../../JsonHelpers";
import { BigDecimal } from "./FunctionHelpers";

const factory = new ComparatorFactory<CompareBy>();

class CompareBy {
  public by: any[];
  public value: any;

  constructor(value: any) {
    this.value = value;
    this.by = [];
  }

  public static createByComparator(index: number, type: string | null): Comparator<CompareBy> {
    let comparator: Comparator<CompareBy>;
    if (isNullOrUndefined(type) || "AUTO" === type.toUpperCase()) {
      comparator = factory.compare((a, b) => {
        const compareResult = compareTo(a.by?.[index], b.by?.[index]);
        return compareResult ?? 0;
      });
    } else {
      switch (type.toUpperCase()) {
        case "NUMBER": {
          comparator = factory.compare((_a, _b) => {
            const a = _a.by?.[index],
              b = _b.by?.[index];
            if ((isNumberType(a) || typeof a === "string") && (isNumberType(b) || typeof b === "string")) {
              return BigDecimal(a).comparedTo(BigDecimal(b));
            } else if (isNullOrUndefined(a) && !isNullOrUndefined(b)) {
              return -1;
            } else if (!isNullOrUndefined(a) && isNullOrUndefined(b)) {
              return 1;
            }
            return 0;
          });
          break;
        }
        case "BOOLEAN": {
          comparator = factory.compare((_a, _b) => {
            const a = _a.by?.[index],
              b = _b.by?.[index];
            if (typeof a === "boolean" && typeof b === "boolean") {
              return a === b ? 0 : a ? 1 : -1;
            } else if (isNullOrUndefined(a) && !isNullOrUndefined(b)) {
              return -1;
            } else if (!isNullOrUndefined(a) && isNullOrUndefined(b)) {
              return 1;
            }
            return 0;
          });
          break;
        }
        //case "STRING"
        default: {
          comparator = factory.compareBy(tup => getAsString(tup.by?.[index]) ?? "");
          break;
        }
      }
    }
    return comparator;
  }
}

export default CompareBy;
