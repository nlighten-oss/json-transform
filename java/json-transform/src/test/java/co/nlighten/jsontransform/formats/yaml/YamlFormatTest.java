package co.nlighten.jsontransform.formats.yaml;

import co.nlighten.jsontransform.MultiAdapterBaseTest;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class YamlFormatTest extends MultiAdapterBaseTest {

        public final static String YAML = """
title: Mr.
firstName: Sam
lastName: Anton
address:
  flatNumber: BV-1025
  buildingName: Shivaji
  plotNumber: '1093'
  sector: Sector 19
  city: Bengaluru
  state: Karnataka
  country: India
  nodeName: South Bengaluru
items:
- itemName: Television
  itemBoughtYear: '2014'
- itemName: Washing Machine
  itemBoughtYear: '2020'
- itemName: Refrigerator
  itemBoughtYear: '2011'
- itemName: Grinder
  itemBoughtYear: '2012'
- itemName: Computer
  itemBoughtYear: '2010'
""";
    public final static String JSON = """
{
  "title" : "Mr.",
  "firstName" : "Sam",
  "lastName" : "Anton",
  "address" : {
    "flatNumber" : "BV-1025",
    "buildingName" : "Shivaji",
    "plotNumber" : "1093",
    "sector" : "Sector 19",
    "city" : "Bengaluru",
    "state" : "Karnataka",
    "country" : "India",
    "nodeName" : "South Bengaluru"
  },
  "items" : [ {
    "itemName" : "Television",
    "itemBoughtYear" : "2014"
  }, {
    "itemName" : "Washing Machine",
    "itemBoughtYear" : "2020"
  }, {
    "itemName" : "Refrigerator",
    "itemBoughtYear" : "2011"
  }, {
    "itemName" : "Grinder",
    "itemBoughtYear" : "2012"
  }, {
    "itemName" : "Computer",
    "itemBoughtYear" : "2010"
  } ]
}""";

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testDeserialize(JsonAdapter<?,?,?> adapter) {
        var result = new YamlFormat(adapter).deserialize(YAML);
        assertEquals(adapter, adapter.parse(JSON), result);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testSerializeAndDeserializeViaJsonElement(JsonAdapter<?,?,?> adapter) {
        // we check back and forth serialization because the output yaml is not guaranteed to be in the same order
        // so, we convert back to JsonElement to do deep comparison

        // get JSON as JsonElement
        var inputJsonElement = adapter.parse(JSON);
        // convert to YAML and back to JsonElement
        var outputYaml = new YamlFormat(adapter).serialize(inputJsonElement);
        var result = new YamlFormat(adapter).deserialize(outputYaml);
        // check that reconstructed correctly
        assertEquals(adapter, inputJsonElement, result);
    }
}
