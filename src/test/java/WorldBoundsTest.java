import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.world.WorldBounds;

class WorldBoundsTest {

    @Test
    void twoByTwoWorldShouldContainExactlyFourCentralChunks() {
        final WorldBounds bounds = WorldBounds.centered(2, 2);

        Assertions.assertEquals(-1, bounds.firstChunkX());
        Assertions.assertEquals(-1, bounds.firstChunkZ());
        Assertions.assertTrue(bounds.containsChunk(-1, -1));
        Assertions.assertTrue(bounds.containsChunk(-1, 0));
        Assertions.assertTrue(bounds.containsChunk(0, -1));
        Assertions.assertTrue(bounds.containsChunk(0, 0));
        Assertions.assertFalse(bounds.containsChunk(-2, 0));
        Assertions.assertFalse(bounds.containsChunk(1, 0));
        Assertions.assertFalse(bounds.containsChunk(0, -2));
        Assertions.assertFalse(bounds.containsChunk(0, 1));
    }

    @Test
    void twoByTwoWorldShouldUseExactBlockEdges() {
        final WorldBounds bounds = WorldBounds.centered(2, 2);

        Assertions.assertTrue(bounds.containsBlock(-16, -16));
        Assertions.assertTrue(bounds.containsBlock(15, 15));
        Assertions.assertTrue(bounds.containsBlock(0, 0));
        Assertions.assertFalse(bounds.containsBlock(-17, 0));
        Assertions.assertFalse(bounds.containsBlock(16, 0));
        Assertions.assertFalse(bounds.containsBlock(0, -17));
        Assertions.assertFalse(bounds.containsBlock(0, 16));
    }

    @Test
    void oddWorldDimensionsShouldRemainCenteredOnChunkZero() {
        final WorldBounds bounds = WorldBounds.centered(3, 5);

        Assertions.assertTrue(bounds.containsChunk(-1, -2));
        Assertions.assertTrue(bounds.containsChunk(1, 2));
        Assertions.assertFalse(bounds.containsChunk(-2, 0));
        Assertions.assertFalse(bounds.containsChunk(2, 0));
        Assertions.assertFalse(bounds.containsChunk(0, -3));
        Assertions.assertFalse(bounds.containsChunk(0, 3));
    }

    @Test
    void nonPositiveDimensionsShouldBeRejected() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> WorldBounds.centered(0, 2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> WorldBounds.centered(2, -1));
    }
}
