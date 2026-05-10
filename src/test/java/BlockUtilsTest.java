import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;

import java.util.Map;

class BlockUtilsTest {

    @Test
    void legacyBlockValueShouldCombineIdAndFourBitMetadata() {
        Assertions.assertEquals(57 << 4 | 3, BlockUtils.createLegacyBlockValue(57, 3));
        Assertions.assertEquals(1 << 4 | 15, BlockUtils.createLegacyBlockValue(1, 31));
    }

    @Test
    void oldLocalYCoordinatesShouldMapToSpawnSection() {
        Assertions.assertEquals(64, BlockUtils.normalizeY(0));
        Assertions.assertEquals(79, BlockUtils.normalizeY(15));
        Assertions.assertEquals(16, BlockUtils.normalizeY(16));
        Assertions.assertEquals(80, BlockUtils.normalizeY(80));
    }

    @Test
    void worldHeightShouldUseLegacyZeroTo255Range() {
        Assertions.assertTrue(BlockUtils.isInsideWorld(0));
        Assertions.assertTrue(BlockUtils.isInsideWorld(255));
        Assertions.assertFalse(BlockUtils.isInsideWorld(-1));
        Assertions.assertFalse(BlockUtils.isInsideWorld(256));
    }

    @Test
    void blockEntriesShouldSupportBothIdFieldNamesAndDefaultMetadata() {
        Assertions.assertEquals(5 << 4 | 2, BlockUtils.resolveLegacyBlockValue(Map.of("block", 5, "data", 2)));
        Assertions.assertEquals(7 << 4 | 1, BlockUtils.resolveLegacyBlockValue(Map.of("id", 7, "data", 1)));
        Assertions.assertEquals(3 << 4, BlockUtils.resolveLegacyBlockValue(Map.of("block", 3)));
    }
}
