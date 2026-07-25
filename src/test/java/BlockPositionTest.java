import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;

class BlockPositionTest {

    @Test
    void yCoordinateShouldOccupyMiddleTwelveBitsInProtocol18() {
        final BlockPosition position = new BlockPosition(0, 64, 0);

        Assertions.assertEquals(4_294_967_296L, position.toLong());
        Assertions.assertEquals(position, BlockPosition.fromLong(4_294_967_296L));
    }

    @Test
    void packedPositionShouldPreservePositiveAndNegativeCoordinates() {
        assertRoundTrip(12, 64, -31);
        assertRoundTrip(-33_554_432, -2_048, 33_554_431);
        assertRoundTrip(33_554_431, 2_047, -33_554_432);
    }

    @Test
    void relativeShouldApplyEveryProtocolFace() {
        final BlockPosition origin = new BlockPosition(10, 70, -4);

        Assertions.assertEquals(new BlockPosition(10, 69, -4), origin.relative(0));
        Assertions.assertEquals(new BlockPosition(10, 71, -4), origin.relative(1));
        Assertions.assertEquals(new BlockPosition(10, 70, -5), origin.relative(2));
        Assertions.assertEquals(new BlockPosition(10, 70, -3), origin.relative(3));
        Assertions.assertEquals(new BlockPosition(9, 70, -4), origin.relative(4));
        Assertions.assertEquals(new BlockPosition(11, 70, -4), origin.relative(5));
        Assertions.assertThrows(IllegalArgumentException.class, () -> origin.relative(6));
    }

    private static void assertRoundTrip(final int x, final int y, final int z) {
        final long protocol18Position = ((x & 0x3FFFFFFL) << 38) | ((y & 0xFFFL) << 26) | (z & 0x3FFFFFFL);
        final BlockPosition position = new BlockPosition(x, y, z);
        Assertions.assertEquals(protocol18Position, position.toLong());
        Assertions.assertEquals(position, BlockPosition.fromLong(protocol18Position));
    }
}
