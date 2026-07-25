import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.world.utils.MapUtils;

import java.util.Map;

class MapUtilsTest {

    @Test
    void numericValuesShouldConvertWithoutDependingOnConcreteNumberType() {
        final Map<String, Object> values = Map.of("integer", 4L, "decimal", 1.25F);

        Assertions.assertEquals(4, MapUtils.readInt(values, "integer", -1));
        Assertions.assertEquals(1, MapUtils.readInt(values, "decimal", -1));
    }

    @Test
    void nestedNumbersShouldBeReadAndDefaultedSafely() {
        final Map<String, Object> values = Map.of("spawn", Map.of("x", -12L, "yaw", 22.5D));

        Assertions.assertEquals(-12, MapUtils.readInt(values, "spawn", "x", 0));
        Assertions.assertEquals(22.5D, MapUtils.readDouble(values, "spawn", "yaw", 0.0D));
        Assertions.assertEquals(70, MapUtils.readInt(values, "missing", "y", 70));
        Assertions.assertEquals(0.0D, MapUtils.readDouble(values, "spawn", "missing", 0.0D));
    }

    @Test
    void stringsShouldRejectBlankAndNonStringValues() {
        Assertions.assertEquals("default", MapUtils.readString(Map.of("type", "  "), "type", "default"));
        Assertions.assertEquals("default", MapUtils.readString(Map.of("type", 1), "type", "default"));
        Assertions.assertEquals("flat", MapUtils.readString(Map.of("type", "flat"), "type", "default"));
    }
}
