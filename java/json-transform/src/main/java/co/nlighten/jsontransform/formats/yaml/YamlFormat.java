package co.nlighten.jsontransform.formats.yaml;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.formats.FormatDeserializer;
import co.nlighten.jsontransform.formats.FormatSerializer;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;

public class YamlFormat implements FormatSerializer, FormatDeserializer {

    private final LoadSettings loadSettings;
    private final DumpSettings dumpSettings;
    private final JsonAdapter<?, ?, ?> adapter;


    public YamlFormat(JsonAdapter<?, ?, ?> adapter) {
        this.adapter = adapter;
        this.loadSettings = LoadSettings.builder().build();
        this.dumpSettings = DumpSettings.builder()
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .setMultiLineFlow(true)
                .build();
    }

    @Override
    public Object deserialize(String input) {
        var load = new Load(loadSettings);
        var yamlOutput = load.loadFromString(input);
        return adapter.wrap(yamlOutput);
    }

    @Override
    public String serialize(Object body) {
        var dump = new Dump(dumpSettings);
        return dump.dumpToString(adapter.is(body) ? adapter.unwrap(body, true) : body);
    }
}
