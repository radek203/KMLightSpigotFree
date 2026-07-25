import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.config.Configuration;
import pl.kwadratowamasakra.lightspigot.config.YamlConfiguration;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

class ConfigurationTest {

    @Test
    void nestedPathsShouldBeCreatedReadAndRemoved() {
        final Configuration configuration = new Configuration();

        configuration.set("network.proxy.enabled", true);
        configuration.set("network.port", 25565);
        Assertions.assertTrue(configuration.getBoolean("network.proxy.enabled"));
        Assertions.assertEquals(25565, configuration.getInt("network.port"));
        Assertions.assertTrue(configuration.contains("network.proxy.enabled"));

        configuration.set("network.proxy.enabled", null);
        Assertions.assertFalse(configuration.contains("network.proxy.enabled"));
    }

    @Test
    void defaultsShouldBeVisibleAndOverridableWithoutMutation() {
        final Configuration defaults = new Configuration();
        defaults.set("network.port", 25565);
        defaults.set("motd.text", "default");
        final Configuration configuration = new Configuration(defaults);

        Assertions.assertEquals(25565, configuration.getInt("network.port"));
        Assertions.assertEquals("default", configuration.getString("motd.text"));
        configuration.set("network.port", 25566);
        Assertions.assertEquals(25566, configuration.getInt("network.port"));
        Assertions.assertEquals(25565, defaults.getInt("network.port"));
    }

    @Test
    void typedListsShouldFilterValuesOfOtherTypes() {
        final Configuration configuration = new Configuration();
        configuration.set("mixed", List.of(1, 2L, "three", true));

        Assertions.assertEquals(List.of(1, 2), configuration.getIntList("mixed"));
        Assertions.assertEquals(List.of("three"), configuration.getStringList("mixed"));
        Assertions.assertEquals(List.of(true), configuration.getBooleanList("mixed"));
    }

    @Test
    void mapsShouldBecomeNestedConfigurationSections() {
        final Configuration configuration = new Configuration();
        configuration.set("world", Map.of("difficulty", 2, "type", "flat"));

        Assertions.assertEquals(2, configuration.getInt("world.difficulty"));
        Assertions.assertEquals("flat", configuration.getString("world.type"));
        Assertions.assertEquals(List.of("difficulty", "type"), configuration.getSection("world").getKeys().stream().sorted().toList());
    }

    @Test
    void yamlShouldRoundTripNestedValuesAndUnicode() {
        final Configuration original = new Configuration();
        original.set("server.name", "Żółty serwer");
        original.set("server.port", 25565);
        original.set("server.ops", List.of("radek203", "Tester"));
        final YamlConfiguration yaml = new YamlConfiguration();
        final StringWriter output = new StringWriter();

        yaml.save(original, output);
        final Configuration restored = yaml.load(output.toString());

        Assertions.assertEquals("Żółty serwer", restored.getString("server.name"));
        Assertions.assertEquals(25565, restored.getInt("server.port"));
        Assertions.assertEquals(List.of("radek203", "Tester"), restored.getStringList("server.ops"));
    }

    @Test
    void emptyYamlShouldProduceEmptyConfiguration() {
        final Configuration configuration = new YamlConfiguration().load("");

        Assertions.assertTrue(configuration.getKeys().isEmpty());
        Assertions.assertEquals(7, configuration.getInt("missing", 7));
    }
}
