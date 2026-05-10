package pl.kwadratowamasakra.lightspigot.world.utils;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.world.ChunkCoordinate;
import pl.kwadratowamasakra.lightspigot.world.ChunkSnapshot;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static pl.kwadratowamasakra.lightspigot.world.World.CHUNK_SIZE;
import static pl.kwadratowamasakra.lightspigot.world.World.WORLD_HEIGHT;

public final class ChunkUtils {

    private static final int LIGHT_BYTES = 2048;
    private static final int BIOME_BYTES = 256;
    private static final int VOID_BIOME = 127;

    private ChunkUtils() {
    }

    public static int[][][] createEmptyChunkBlocks() {
        return new int[WORLD_HEIGHT][CHUNK_SIZE][CHUNK_SIZE];
    }

    public static byte[] createChunkData(final int[][][] blocks, final int primaryBitMask) {
        final PacketBuffer buffer = new PacketBuffer();
        writeBlocks(buffer, blocks, primaryBitMask);
        writeBlockLight(buffer, primaryBitMask);
        writeSkyLight(buffer, primaryBitMask);
        writeBiomes(buffer);
        final byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        return data;
    }

    private static void writeBlocks(final PacketBuffer buffer, final int[][][] blocks, final int primaryBitMask) {
        for (int section = 0; section < WORLD_HEIGHT / CHUNK_SIZE; section++) {
            if ((primaryBitMask & 1 << section) == 0) {
                continue;
            }
            for (int localY = 0; localY < CHUNK_SIZE; localY++) {
                final int y = section * CHUNK_SIZE + localY;
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    for (int x = 0; x < CHUNK_SIZE; x++) {
                        final int block = blocks[y][z][x];
                        buffer.writeByte(block & 0xFF);
                        buffer.writeByte(block >> 8 & 0xFF);
                    }
                }
            }
        }
    }

    private static void writeBlockLight(final PacketBuffer buffer, final int primaryBitMask) {
        final byte[] blockLight = new byte[LIGHT_BYTES];
        Arrays.fill(blockLight, (byte) 0x00);
        for (int section = 0; section < Integer.bitCount(primaryBitMask); section++) {
            buffer.writeBytes(blockLight);
        }
    }

    private static void writeSkyLight(final PacketBuffer buffer, final int primaryBitMask) {
        final byte[] skyLight = new byte[LIGHT_BYTES];
        Arrays.fill(skyLight, (byte) 0xFF);
        for (int section = 0; section < Integer.bitCount(primaryBitMask); section++) {
            buffer.writeBytes(skyLight);
        }
    }

    private static void writeBiomes(final PacketBuffer buffer) {
        final byte[] biomes = new byte[BIOME_BYTES];
        Arrays.fill(biomes, (byte) VOID_BIOME);
        buffer.writeBytes(biomes);
    }

    public static List<ChunkSnapshot> createChunkSnapshots(final Map<ChunkCoordinate, int[][][]> chunkBlocks, final int fullChunkMask) {
        return chunkBlocks.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<ChunkCoordinate, int[][][]> entry) -> entry.getKey().chunkX())
                        .thenComparingInt(entry -> entry.getKey().chunkZ()))
                .map(entry -> createChunkSnapshot(entry.getKey(), entry.getValue(), fullChunkMask))
                .toList();
    }

    public static ChunkSnapshot createChunkSnapshot(final ChunkCoordinate coordinate, final int[][][] blocks, final int fullChunkMask) {
        int primaryBitMask = fullChunkMask;
        for (int y = 0; y < WORLD_HEIGHT; y++) {
            if (containsBlock(blocks[y])) {
                primaryBitMask |= 1 << y / CHUNK_SIZE;
            }
        }
        return new ChunkSnapshot(coordinate.chunkX(), coordinate.chunkZ(), primaryBitMask, createChunkData(blocks, primaryBitMask));
    }

    private static boolean containsBlock(final int[][] layer) {
        for (final int[] row : layer) {
            for (final int block : row) {
                if (block != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
