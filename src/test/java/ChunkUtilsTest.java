import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.world.ChunkCoordinate;
import pl.kwadratowamasakra.lightspigot.world.ChunkSnapshot;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.ChunkUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ChunkUtilsTest {

    @Test
    void snapshotShouldIncludeEverySectionContainingBlocks() {
        final int[][][] blocks = ChunkUtils.createEmptyChunkBlocks();
        blocks[80][0][0] = BlockUtils.createLegacyBlockValue(57, 3);

        final ChunkSnapshot snapshot = ChunkUtils.createChunkSnapshot(new ChunkCoordinate(0, 0), blocks, 1 << 4);

        Assertions.assertEquals((1 << 4) | (1 << 5), snapshot.primaryBitMask());
        Assertions.assertEquals(24_832, snapshot.chunkData().length);
        Assertions.assertEquals(57 << 4 | 3,
                Byte.toUnsignedInt(snapshot.chunkData()[8_192]) | Byte.toUnsignedInt(snapshot.chunkData()[8_193]) << 8);
    }

    @Test
    void emptyChunkShouldRetainDefaultSpawnSection() {
        final ChunkSnapshot snapshot = ChunkUtils.createChunkSnapshot(
                new ChunkCoordinate(0, 0), ChunkUtils.createEmptyChunkBlocks(), 1 << 4);

        Assertions.assertEquals(1 << 4, snapshot.primaryBitMask());
        Assertions.assertEquals(12_544, snapshot.chunkData().length);
    }

    @Test
    void blocksAtWorldHeightBoundariesShouldSelectFirstAndLastSections() {
        final int[][][] blocks = ChunkUtils.createEmptyChunkBlocks();
        blocks[0][0][0] = BlockUtils.createLegacyBlockValue(1, 0);
        blocks[255][15][15] = BlockUtils.createLegacyBlockValue(2, 1);

        final ChunkSnapshot snapshot = ChunkUtils.createChunkSnapshot(new ChunkCoordinate(-1, 2), blocks, 0);

        Assertions.assertEquals(1 | 1 << 15, snapshot.primaryBitMask());
        Assertions.assertEquals(24_832, snapshot.chunkData().length);
    }

    @Test
    void chunkSnapshotsShouldBeSortedByXThenZ() {
        final Map<ChunkCoordinate, int[][][]> chunks = new HashMap<>();
        chunks.put(new ChunkCoordinate(2, -1), ChunkUtils.createEmptyChunkBlocks());
        chunks.put(new ChunkCoordinate(-1, 5), ChunkUtils.createEmptyChunkBlocks());
        chunks.put(new ChunkCoordinate(-1, -2), ChunkUtils.createEmptyChunkBlocks());

        final List<ChunkSnapshot> snapshots = ChunkUtils.createChunkSnapshots(chunks, 1 << 4);

        Assertions.assertEquals(List.of(
                new ChunkCoordinate(-1, -2),
                new ChunkCoordinate(-1, 5),
                new ChunkCoordinate(2, -1)
        ), snapshots.stream().map(chunk -> new ChunkCoordinate(chunk.chunkX(), chunk.chunkZ())).toList());
    }
}
