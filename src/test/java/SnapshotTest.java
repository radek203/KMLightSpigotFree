import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.event.Location;
import pl.kwadratowamasakra.lightspigot.world.ChunkSnapshot;
import pl.kwadratowamasakra.lightspigot.world.WorldSnapshot;

import java.util.List;

class SnapshotTest {

    @Test
    void chunkSnapshotShouldDefensivelyCopyInputAndOutputArrays() {
        final byte[] source = {1, 2, 3};
        final ChunkSnapshot snapshot = new ChunkSnapshot(0, 0, 1, source);
        source[0] = 9;

        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, snapshot.chunkData());
        final byte[] exposed = snapshot.chunkData();
        exposed[1] = 9;
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, snapshot.chunkData());
    }

    @Test
    void copiedWorldChunksShouldNotMutateOriginalSnapshot() {
        final WorldSnapshot world = new WorldSnapshot(
                new Location(0, 65, 0), 0, 64, 0, 0, 0, "default",
                List.of(new ChunkSnapshot(0, 0, 1, new byte[]{4, 5, 6})));

        final List<ChunkSnapshot> copy = world.copyChunks();
        final byte[] copiedData = copy.getFirst().chunkData();
        copiedData[0] = 99;

        Assertions.assertArrayEquals(new byte[]{4, 5, 6}, world.chunks().getFirst().chunkData());
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> copy.add(new ChunkSnapshot(1, 0, 1, new byte[0])));
    }
}
