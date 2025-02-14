package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class TransformerFunctionNormalize extends TransformerFunction {
    // * source strings (ends with _S) are after decomposition and removal of marks
    // ** if target character is more than one letter,
    //    use _ instead and add it as a separate mapping in the exceptions map (ends with _E)
    static final String LatinExANonComposed_S = "ĐđĦħŁłŊŋŒœŦŧ";
    static final String LatinExANonComposed_T = "DdHhLlNn__Tt";
    static final Map<String, String> LatinExANonComposed_E = Map.ofEntries(
            Map.entry("Œ", "OE"), Map.entry("œ", "oe")
    );
    static final String LatinExBNonComposed_S = "ƀƁƄƅƆƇƈƉƊƑƒƓƕƗƘƙƚOoƤƥƦƧƨƫƬƭƳƴƵƶÆæǤǥǶǷØøȡȤȥȴȵȶȺȻȼȽȾȿɀɃɄɅɆɇɈɉɊɋɌɍɎɏ";
    static final String LatinExBNonComposed_T = "bBbbCCcDDFfGhIKklOoPpRSstTtYyZz__GgHpOodZzlntACcLTszBUAEeJjQqRrYy";
    static final Map<String, String> LatinExBNonComposed_E = Map.ofEntries(
            Map.entry("Æ", "AE"), Map.entry("æ", "ae")
    );
    static final String Latin1SNonComposed_S = "Ð";
    static final String Latin1SNonComposed_T = "D";
    static final Map<String, String> Latin1SNonComposed_E = Map.ofEntries(
    );
    static final Pattern ReplacementsPattern;
    static final Map<String, String> ReplacementsMap;

    static {
        ReplacementsPattern = Pattern.compile(
                String.format("[%s]", String.join("", LatinExANonComposed_S, LatinExBNonComposed_S, Latin1SNonComposed_S)));
        var entries = new ArrayList<Map.Entry<String, String>>();
        addAllEntries(entries, LatinExANonComposed_S, LatinExANonComposed_T, LatinExANonComposed_E);
        addAllEntries(entries, LatinExBNonComposed_S, LatinExBNonComposed_T, LatinExBNonComposed_E);
        addAllEntries(entries, Latin1SNonComposed_S, Latin1SNonComposed_T, Latin1SNonComposed_E);

        ReplacementsMap = Map.ofEntries(entries.toArray(Map.Entry[]::new));
    }

    private static void addAllEntries(ArrayList<Map.Entry<String, String>> entries, String source, String target, Map<String, String> exceptions) {
        var sourceArray = source.codePoints().toArray();
        for (var i = 0; i < sourceArray.length; i++) {
            var sourceChar = Character.toString(sourceArray[i]);
            var targetChar = Character.toString(target.codePointAt(i));
            if (!targetChar.equals("_")) {
                entries.add(Map.entry(sourceChar, targetChar));
            } else if (exceptions.containsKey(sourceChar)) {
                entries.add(Map.entry(sourceChar, exceptions.get(sourceChar)));
            }
        }
    }

    public TransformerFunctionNormalize() {
        super(FunctionDescription.of(
                Map.of(
                        "form", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("NFKD"),
                        "post_operation", ArgumentType.of(ArgType.Enum).position(1).defaultEnum("ROBUST")
                )
        ));
    }

    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        if (str.isBlank()) return str; // short-circuit for blank strings
        var normalizerForm = Normalizer.Form.valueOf(context.getEnum("form"));
        str = Normalizer.normalize(str, normalizerForm);

        var postOperation = context.getEnum("post_operation");
        if (postOperation.equalsIgnoreCase("ROBUST")) {
            // replace separators with space (and remove leading and trailing white spaces)
            str = str.replaceAll("\\p{Z}", " ").strip();
            // remove all diacritical marks
            str = str.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            str = str.replaceAll("[·\\p{Lm}]+", ""); // solves decomposition of ŉ Ŀ ŀ
            // do other replacements
            str = ReplacementsPattern.matcher(str).replaceAll(result -> ReplacementsMap.get(result.group()));
        }

        return str;
    }
}
