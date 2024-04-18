package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CompareBy<JE> {
    public List<JE> by;
    public JE value;
    public CompareBy(JE value) {
        this.value = value;
    }

    public static <JE, JA extends Iterable<JE>, JO extends JE> Comparator<CompareBy<JE>> createByComparator(
            JsonAdapter<JE, JA, JO> adapter, int index, String type) {
        Comparator<CompareBy<JE>> comparator;
        if (type == null || "AUTO".equalsIgnoreCase(type)) {
            comparator = (CompareBy<JE> a, CompareBy<JE> b) -> {
                var compareResult = adapter.compareTo(a.by.get(index), b.by.get(index));
                return Objects.requireNonNullElse(compareResult, 0);
            };
        } else {
            comparator = switch (type.toUpperCase()) {
                case "NUMBER" -> Comparator.comparing((CompareBy<JE> tup) -> adapter.getNumberAsBigDecimal(tup.by.get(index)));
                case "BOOLEAN" -> Comparator.comparing((CompareBy<JE> tup) -> adapter.getBoolean(tup.by.get(index)));
                //case "STRING"
                default -> Comparator.comparing((CompareBy<JE> tup) -> {
                    var val = tup.by.get(index);
                    // null keys and empty string are treated the same
                    return adapter.isNull(val) ? "" : adapter.getAsString(val);
                });
            };
        }
        return comparator;
    }
}
