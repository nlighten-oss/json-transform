package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionNormalizeTest extends BaseTest {

    @Test
    void testInlineFunctionNormalize() {
        var strVal = "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ ａｂｃＡＢＣ…";
        assertTransformation(strVal, "$$normalize:$", "This is a funky String abcABC...");
        assertTransformation(strVal, "$$normalize(NFKD):$", "This is a funky String abcABC...");
        assertTransformation(strVal, "$$normalize(NFKD,NONE):$", "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ abcABC...");
        assertTransformation(strVal, "$$normalize(NFD):$", "This is a funky String ａｂｃＡＢＣ…");
        assertTransformation(strVal, "$$normalize(NFD,NONE):$", "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ ａｂｃＡＢＣ…");

        // removeMarks on NFC/NFKC does not affect because marks are composed (and gone) before replacement
        assertTransformation(strVal, "$$normalize(NFKC):$", "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ abcABC...");
        assertTransformation(strVal, "$$normalize(NFKC,NONE):$", "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ abcABC...");
        assertTransformation(strVal, "$$normalize(NFC):$", strVal);
        assertTransformation(strVal, "$$normalize(NFC,NONE):$", strVal);
    }

    @Test
    void unicodeTableBasicLatin() {
        var strVal = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        assertTransformation(strVal, "$$normalize:$", strVal);
    }
    @Test
    void unicodeTableLatinExtendedA() {
        var strVal = "ĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſ";
        var expect = "AaAaAaCcCcCcCcDdDdEeEeEeEeEeGgGgGgGgHhHhIiIiIiIiIıIJijJjKkĸLlLlLlLlLlNnNnNnnNnOoOoOoOEoeRrRrRrSsSsSsSsTtTtTtUuUuUuUuUuUuWwYyYZzZzZzs";
        assertTransformation(strVal, "$$normalize:$", expect);
    }

    @Test
    void unicodeTableLatinExtendedB() {
        var strVal = "ƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿǀǁǂǃǄǅǆǇǈǉǊǋǌǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȠȡȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳȴȵȶȷȸȹȺȻȼȽȾȿɀɁɂɃɄɅɆɇɈɉɊɋɌɍɎɏ";
        var expect = "bBƂƃbbCCcDDƋƌƍƎƏƐFfGƔhƖIKklƛƜƝƞƟOoƢƣPpRSsƩƪtTtƮUuƱƲYyZzƷƸƹƺƻƼƽƾƿǀǁǂǃDZDzdzLJLjljNJNjnjAaIiOoUuUuUuUuUuǝAaAaAEaeGgGgKkOoOoƷʒjDZDzdzGgHpNnAaAEaeOoAaAaEeEeIiIiOoOoRrRrUuUuSsTtȜȝHhȠdȢȣZzAaEeOoOoOoOoYylntȷȸȹACcLTszɁɂBUAEeJjQqRrYy";
        assertTransformation(strVal, "$$normalize:$", expect);
    }

    @Test
    void unicodeTableLatin1Supplement() {
        var strVal = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ";
        var expect = "AAAAAAAECEEEEIIIIDNOOOOO×OUUUUYÞßaaaaaaaeceeeeiiiiðnooooo÷ouuuuyþy";
        assertTransformation(strVal, "$$normalize:$", expect);
    }
    @Test
    void exceptionsAeOe() {
        var strVal = "ŒœÆæǢǣǼǽ";
        var expect = "OEoeAEaeAEaeAEae";
        assertTransformation(strVal, "$$normalize:$", expect);
    }
}
