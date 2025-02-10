package co.nlighten.jsontransform.functions.common;


import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CompareBy {
    public List<Object> by;
    public Object value;
    public CompareBy(Object value) {
        this.value = value;
    }

    public static Comparator<CompareBy> createByComparator(
            JsonAdapter<?, ?, ?> adapter, int index, String type) {
        Comparator<CompareBy> comparator;
        if (type == null || "AUTO".equalsIgnoreCase(type)) {
            comparator = (CompareBy a, CompareBy b) -> {
                var compareResult = adapter.compareTo(a.by.get(index), b.by.get(index));
                return Objects.requireNonNullElse(compareResult, 0);
            };
        } else {
            comparator = switch (type.toUpperCase()) {
                case "NUMBER" -> Comparator.comparing((CompareBy tup) -> adapter.getNumberAsBigDecimal(tup.by.get(index)));
                case "BOOLEAN" -> Comparator.comparing((CompareBy tup) -> adapter.getBoolean(tup.by.get(index)));
                //case "STRING"
                default -> Comparator.comparing((CompareBy tup) -> {
                    var val = tup.by.get(index);
                    // null keys and empty string are treated the same
                    return adapter.isNull(val) ? "" : adapter.getAsString(val);
                });
            };
        }
        return comparator;
    }
}
