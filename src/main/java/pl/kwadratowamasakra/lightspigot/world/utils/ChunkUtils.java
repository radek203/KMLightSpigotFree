package pl.kwadratowamasakra.lightspigot.world.utils;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.world.ChunkCoordinate;
import pl.kwadratowamasakra.lightspigot.world.ChunkSnapshot;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static pl.kwadratowamasakra.lightspigot.world.World.CHUNK_SIZE;

public final class ChunkUtils {

    private static final int LIGHT_BYTES = 2048;
    private static final int BIOME_BYTES = 256;
    private static final int VOID_BIOME = 127;

    private ChunkUtils() {
    }

    public static int[][][] createEmptyChunkBlocks() {
        return new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    }

    public static byte[] createChunkData(final int[][][] blocks) {
        final PacketBuffer buffer = new PacketBuffer();
        writeBlocks(buffer, blocks);
        writeBlockLight(buffer);
        writeSkyLight(buffer);
        writeBiomes(buffer);
        final byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        return data;
    }

    private static void writeBlocks(final PacketBuffer buffer, final int[][][] blocks) {
        for (int y = 0; y < CHUNK_SIZE; y++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int x = 0; x < CHUNK_SIZE; x++) {
                    final int block = blocks[y][z][x];
                    buffer.writeByte(block & 0xFF);
                    buffer.writeByte(block >> 8 & 0xFF);
                }
            }
        }
    }

    private static void writeBlockLight(final PacketBuffer buffer) {
        final byte[] blockLight = new byte[LIGHT_BYTES];
        Arrays.fill(blockLight, (byte) 0x00);
        buffer.writeBytes(blockLight);
    }

    private static void writeSkyLight(final PacketBuffer buffer) {
        final byte[] skyLight = new byte[LIGHT_BYTES];
        Arrays.fill(skyLight, (byte) 0xFF);
        buffer.writeBytes(skyLight);
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
                .map(entry -> new ChunkSnapshot(entry.getKey().chunkX(), entry.getKey().chunkZ(), fullChunkMask, createChunkData(entry.getValue())))
                .toList();
    }
}
