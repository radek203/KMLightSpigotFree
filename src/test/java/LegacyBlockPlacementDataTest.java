import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.world.utils.LegacyBlockPlacementData;

class LegacyBlockPlacementDataTest {

    @Test
    void stairsShouldFaceThePlayersHorizontalDirection() {
        Assertions.assertEquals(2, stairs(0.0F, 1, 0.5F));
        Assertions.assertEquals(1, stairs(90.0F, 1, 0.5F));
        Assertions.assertEquals(3, stairs(180.0F, 1, 0.5F));
        Assertions.assertEquals(0, stairs(-90.0F, 1, 0.5F));
        Assertions.assertEquals(0, stairs(270.0F, 1, 0.5F));
    }

    @Test
    void stairsShouldUseTopHalfWhenPlacedFromBelowOrOnUpperSide() {
        Assertions.assertEquals(2, stairs(0.0F, 1, 0.9F));
        Assertions.assertEquals(2 | 0x04, stairs(0.0F, 0, 0.1F));
        Assertions.assertEquals(2 | 0x04, stairs(0.0F, 3, 0.75F));
        Assertions.assertEquals(2, stairs(0.0F, 3, 0.25F));
    }

    @Test
    void slabsAndLogsShouldPreserveVariantAndApplyPlacementOrientation() {
        Assertions.assertEquals(0x0B, LegacyBlockPlacementData.resolve(44, 3, 0, 0.1F, 0.0F));
        Assertions.assertEquals(0x03, LegacyBlockPlacementData.resolve(44, 3, 1, 0.9F, 0.0F));
        Assertions.assertEquals(0x06, LegacyBlockPlacementData.resolve(17, 2, 5, 0.5F, 0.0F));
        Assertions.assertEquals(0x0A, LegacyBlockPlacementData.resolve(17, 2, 2, 0.5F, 0.0F));
        Assertions.assertEquals(0x02, LegacyBlockPlacementData.resolve(17, 2, 1, 0.5F, 0.0F));
    }

    private static int stairs(final float yaw, final int face, final float hitY) {
        return LegacyBlockPlacementData.resolve(53, 0, face, hitY, yaw);
    }
}
