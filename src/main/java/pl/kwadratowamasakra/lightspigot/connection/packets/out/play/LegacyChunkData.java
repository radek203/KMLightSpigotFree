package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;

final class LegacyChunkData {

    static final int BLOCKS_PER_SECTION = 4096;
    static final int LIGHT_BYTES = 2048;
    static final int BIOME_BYTES = 256;

    final int[][] blocks;
    final byte[][] blockLight;
    final byte[][] skyLight;
    final byte[] biomes;
    final int[] sectionYs;

    private LegacyChunkData(final int[][] blocks, final byte[][] blockLight,
                            final byte[][] skyLight, final byte[] biomes, final int[] sectionYs) {
        this.blocks = blocks;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.biomes = biomes;
        this.sectionYs = sectionYs;
    }

    static LegacyChunkData decode(final int primaryBitMask, final byte[] data) {
        final PacketBuffer buffer = new PacketBuffer();
        buffer.writeBytes(data);
        final int sections = Integer.bitCount(primaryBitMask);
        final int[][] blocks = new int[sections][BLOCKS_PER_SECTION];
        final int[] sectionYs = new int[sections];
        int sectionIndex = 0;
        for (int y = 0; y < 16; y++) if ((primaryBitMask & 1 << y) != 0) sectionYs[sectionIndex++] = y;
        for (int section = 0; section < sections; section++) {
            for (int block = 0; block < BLOCKS_PER_SECTION; block++) {
                blocks[section][block] = buffer.readUnsignedByte() | buffer.readUnsignedByte() << 8;
            }
        }
        final byte[][] blockLight = readLights(buffer, sections);
        final byte[][] skyLight = readLights(buffer, sections);
        final byte[] biomes = new byte[BIOME_BYTES];
        if (buffer.readableBytes() >= BIOME_BYTES) {
            buffer.readBytes(biomes);
        }
        return new LegacyChunkData(blocks, blockLight, skyLight, biomes, sectionYs);
    }

    private static byte[][] readLights(final PacketBuffer buffer, final int sections) {
        final byte[][] result = new byte[sections][LIGHT_BYTES];
        for (int section = 0; section < sections; section++) {
            buffer.readBytes(result[section]);
        }
        return result;
    }

    int[] blocksAt(final int sectionY) {
        for (int i = 0; i < sectionYs.length; i++) if (sectionYs[i] == sectionY) return blocks[i];
        return new int[BLOCKS_PER_SECTION];
    }

    int blockAt(final int x, final int y, final int z) {
        if (x < 0 || x >= 16 || z < 0 || z >= 16 || y < 0 || y >= 256) return 0;
        final int sectionY = y >>> 4;
        for (int i = 0; i < sectionYs.length; i++) {
            if (sectionYs[i] == sectionY) {
                return blocks[i][(y & 0x0F) * 256 + z * 16 + x];
            }
        }
        return 0;
    }
}
