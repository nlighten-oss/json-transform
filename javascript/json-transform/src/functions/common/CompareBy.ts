import { Comparator, ComparatorFactory } from "@wortise/sequency";
import { compareTo, getAsString } from "../../JsonHelpers";
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
    if (type == null || "AUTO" === type.toUpperCase()) {
      comparator = factory.compare((a: CompareBy, b: CompareBy) => {
        const compareResult = compareTo(a.by?.[index], b.by?.[index]);
        return compareResult ?? 0;
      });
    } else {
      switch (type.toUpperCase()) {
        case "NUMBER": {
          comparator = factory.compareBy(tup => BigDecimal(tup.by?.[index]));
          break;
        }
        case "BOOLEAN": {
          comparator = factory.compareBy(tup => Boolean(tup.by?.[index]));
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
