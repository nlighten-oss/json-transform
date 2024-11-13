import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionNormalize", () => {
  test("inline", async () => {
    var strVal = "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ ａｂｃＡＢＣ…";
    await assertTransformation(strVal, "$$normalize:$", "This is a funky String abcABC...");
    await assertTransformation(strVal, "$$normalize(NFKD):$", "This is a funky String abcABC...");
    await assertTransformation(strVal, "$$normalize(NFKD,NONE):$", "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ abcABC...");
    await assertTransformation(strVal, "$$normalize(NFD):$", "This is a funky String ａｂｃＡＢＣ…");
    await assertTransformation(strVal, "$$normalize(NFD,NONE):$", "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ ａｂｃＡＢＣ…");

    // removeMarks on NFC/NFKC does not affect because marks are composed (and gone) before replacement
    await assertTransformation(strVal, "$$normalize(NFKC):$", "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ abcABC...");
    await assertTransformation(strVal, "$$normalize(NFKC,NONE):$", "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ abcABC...");
    await assertTransformation(strVal, "$$normalize(NFC):$", strVal);
    await assertTransformation(strVal, "$$normalize(NFC,NONE):$", strVal);
  });

  test("unicodeTableBasicLatin", async () => {
    var strVal = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    await assertTransformation(strVal, "$$normalize:$", strVal);
  });
  test("unicodeTableLatinExtendedA", async () => {
    var strVal =
      "ĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſ";
    var expect =
      "AaAaAaCcCcCcCcDdDdEeEeEeEeEeGgGgGgGgHhHhIiIiIiIiIıIJijJjKkĸLlLlLlLlLlNnNnNnnNnOoOoOoOEoeRrRrRrSsSsSsSsTtTtTtUuUuUuUuUuUuWwYyYZzZzZzs";
    await assertTransformation(strVal, "$$normalize:$", expect);
  });

  test("unicodeTableLatinExtendedB", async () => {
    var strVal =
      "ƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿǀǁǂǃǄǅǆǇǈǉǊǋǌǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȠȡȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳȴȵȶȷȸȹȺȻȼȽȾȿɀɁɂɃɄɅɆɇɈɉɊɋɌɍɎɏ";
    var expect =
      "bBƂƃbbCCcDDƋƌƍƎƏƐFfGƔhƖIKklƛƜƝƞƟOoƢƣPpRSsƩƪtTtƮUuƱƲYyZzƷƸƹƺƻƼƽƾƿǀǁǂǃDZDzdzLJLjljNJNjnjAaIiOoUuUuUuUuUuǝAaAaAEaeGgGgKkOoOoƷʒjDZDzdzGgHpNnAaAEaeOoAaAaEeEeIiIiOoOoRrRrUuUuSsTtȜȝHhȠdȢȣZzAaEeOoOoOoOoYylntȷȸȹACcLTszɁɂBUAEeJjQqRrYy";
    await assertTransformation(strVal, "$$normalize:$", expect);
  });

  test("unicodeTableLatin1Supplement", async () => {
    var strVal = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ";
    var expect = "AAAAAAAECEEEEIIIIDNOOOOO×OUUUUYÞßaaaaaaaeceeeeiiiiðnooooo÷ouuuuyþy";
    await assertTransformation(strVal, "$$normalize:$", expect);
  });
  test("exceptionsAeOe", async () => {
    var strVal = "ŒœÆæǢǣǼǽ";
    var expect = "OEoeAEaeAEaeAEae";
    await assertTransformation(strVal, "$$normalize:$", expect);
  });
});
