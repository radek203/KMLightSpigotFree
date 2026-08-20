package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolMappings;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolNbt;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.World;
import pl.kwadratowamasakra.lightspigot.world.utils.LegacyStairShape;

import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
public class PacketPlayOutChunkData extends PacketOut {

    private int chunkX;
    private int chunkZ;
    private boolean groundUpContinuous;
    private int primaryBitMask;
    private byte[] data;
    private World world;

    public PacketPlayOutChunkData(final int chunkX, final int chunkZ, final boolean groundUpContinuous,
                                  final int primaryBitMask, final byte[] data) {
        this(chunkX, chunkZ, groundUpContinuous, primaryBitMask, data, null);
    }

    public PacketPlayOutChunkData(final int chunkX, final int chunkZ, final boolean groundUpContinuous,
                                  final int primaryBitMask, final byte[] data, final World world) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.groundUpContinuous = groundUpContinuous;
        this.primaryBitMask = primaryBitMask;
        this.data = data;
        this.world = world;
    }

    private static long[] createHeightmap(final LegacyChunkData legacy) {
        final int[] heights = new int[256];
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                heightSearch:
                for (int sectionIndex = legacy.sectionYs.length - 1; sectionIndex >= 0; sectionIndex--) {
                    final int sectionY = legacy.sectionYs[sectionIndex];
                    final int[] blocks = legacy.blocks[sectionIndex];
                    for (int localY = 15; localY >= 0; localY--) {
                        if (blocks[localY * 256 + z * 16 + x] != 0) {
                            // Modern overworld chunks start at Y=-64, hence +64 plus the heightmap's +1.
                            heights[z * 16 + x] = sectionY * 16 + localY + 65;
                            break heightSearch;
                        }
                    }
                }
            }
        }
        final int bits = 9;
        final int valuesPerLong = Long.SIZE / bits;
        final long[] packed = new long[(heights.length + valuesPerLong - 1) / valuesPerLong];
        for (int index = 0; index < heights.length; index++) {
            packed[index / valuesPerLong] |= (long) heights[index] << index % valuesPerLong * bits;
        }
        return packed;
    }

    private static void writeSingleValuedPalette(final PacketBuffer buffer, final int value,
                                                 final Version version) {
        buffer.writeByte(0);
        buffer.writeVarInt(value);
        if (version.isLessThan(Version.V1_21_5)) buffer.writeVarInt(0);
    }

    private static int biomeId(final Version version) {
        if (version.isLessThan(Version.V1_19_3)) return 1;
        if (version.isLessThan(Version.V1_20)) return 38;
        if (version.isLessThan(Version.V1_21_2)) return 39;
        return 40;
    }

    static void writeModernLight(final PacketBuffer buffer, final Version version, final LegacyChunkData data) {
        long lightMask = 0L;
        for (final int sectionY : data.sectionYs) lightMask |= 1L << (sectionY + 5);
        final long allMask = (1L << 26) - 1L;
        final long emptyMask = allMask & ~lightMask;
        if (version.isLessThan(Version.V1_20)) buffer.writeBoolean(true);
        writeLongArray(buffer, lightMask);
        writeLongArray(buffer, lightMask);
        writeLongArray(buffer, emptyMask);
        writeLongArray(buffer, emptyMask);
        buffer.writeVarInt(data.skyLight.length);
        for (final byte[] light : data.skyLight) buffer.writeBytesArray(light);
        buffer.writeVarInt(data.blockLight.length);
        for (final byte[] light : data.blockLight) buffer.writeBytesArray(light);
    }

    private static void writeLongArray(final PacketBuffer buffer, final long value) {
        buffer.writeVarInt(1);
        buffer.writeLong(value);
    }

    private static int bitsRequired(final int[] values) {
        int maximum = 0;
        for (final int value : values) {
            maximum = Math.max(maximum, value);
        }
        return Math.max(1, 32 - Integer.numberOfLeadingZeros(maximum));
    }

    private static int directBits(final Version version) {
        if (version.isEqualOrHigher(Version.V1_16)) {
            return 15;
        }
        if (version.isEqualOrHigher(Version.V1_13)) {
            return 14;
        }
        return 13;
    }

    private static void writePacked(final PacketBuffer output, final int[] values,
                                    final int bits, final boolean padded, final boolean omitLength) {
        final long mask = (1L << bits) - 1L;
        if (padded) {
            final int valuesPerLong = 64 / bits;
            final int length = (values.length + valuesPerLong - 1) / valuesPerLong;
            if (!omitLength) output.writeVarInt(length);
            for (int longIndex = 0; longIndex < length; longIndex++) {
                long packed = 0;
                for (int local = 0; local < valuesPerLong; local++) {
                    final int index = longIndex * valuesPerLong + local;
                    if (index == values.length) break;
                    packed |= ((long) values[index] & mask) << local * bits;
                }
                output.writeLong(packed);
            }
            return;
        }
        final int length = (values.length * bits + 63) / 64;
        final long[] packed = new long[length];
        for (int index = 0; index < values.length; index++) {
            final int bitIndex = index * bits;
            final int longIndex = bitIndex >>> 6;
            final int startBit = bitIndex & 63;
            final long value = (long) values[index] & mask;
            packed[longIndex] |= value << startBit;
            if (startBit + bits > 64) {
                packed[longIndex + 1] |= value >>> (64 - startBit);
            }
        }
        if (!omitLength) output.writeVarInt(length);
        for (final long value : packed) output.writeLong(value);
    }

    private static void writeBiomes(final PacketBuffer buffer, final Version version) {
        if (version.isEqualOrHigher(Version.V1_16_2)) {
            buffer.writeVarInt(1024);
            for (int i = 0; i < 1024; i++) buffer.writeVarInt(1);
        } else {
            for (int i = 0; i < 1024; i++) buffer.writeInt(1);
        }
    }

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer buffer) {
        final Version version = connection.getVersion();
        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
        if (version.isEqual(Version.V1_8)) {
            buffer.writeBoolean(groundUpContinuous);
            buffer.writeShort(primaryBitMask);
            buffer.writeVarInt(data.length);
            buffer.writeBytes(data);
            return;
        }

        final LegacyChunkData legacy = LegacyChunkData.decode(primaryBitMask, data);
        if (version.isEqualOrHigher(Version.V1_18)) {
            writeModernChunk(buffer, version, legacy);
            return;
        }
        if (version.isLessThan(Version.V1_14)) {
            buffer.writeBoolean(groundUpContinuous);
            buffer.writeVarInt(primaryBitMask);
            final byte[] encoded = encodeSections(version, legacy, true, true);
            buffer.writeBytesArray(encoded);
            if (version.isEqualOrHigher(Version.V1_9_3)) {
                buffer.writeVarInt(0);
            }
            return;
        }

        if (version.isLessThan(Version.V1_17)) {
            buffer.writeBoolean(groundUpContinuous);
            if (version.isInRange(Version.V1_16, Version.V1_16_1)) {
                buffer.writeBoolean(true); // ignore old heightmaps
            }
            buffer.writeVarInt(primaryBitMask);
        } else {
            buffer.writeVarInt(1);
            buffer.writeLong(primaryBitMask & 0xFFFFL);
        }
        ProtocolNbt.writeEmptyHeightmaps(buffer);

        if (version.isInRange(Version.V1_14, Version.V1_14_4)) {
            final byte[] encoded = encodeSections(version, legacy, false, true);
            buffer.writeBytesArray(encoded);
        } else {
            writeBiomes(buffer, version);
            final byte[] encoded = encodeSections(version, legacy, false, false);
            buffer.writeBytesArray(encoded);
        }
        buffer.writeVarInt(0); // block entities
    }

    private void writeModernChunk(final PacketBuffer buffer, final Version version, final LegacyChunkData legacy) {
        ProtocolNbt.writeHeightmaps(buffer, version, createHeightmap(legacy));
        final PacketBuffer sections = new PacketBuffer();
        for (int sectionY = -4; sectionY < 20; sectionY++) {
            writeSection(sections, version, legacy, sectionY);
            writeSingleValuedPalette(sections, biomeId(version), version);
        }
        final byte[] encoded = new byte[sections.readableBytes()];
        sections.readBytes(encoded);
        buffer.writeBytesArray(encoded);
        buffer.writeVarInt(0); // block entities
        writeModernLight(buffer, version, legacy);
    }

    private byte[] encodeSections(final Version version, final LegacyChunkData legacy,
                                  final boolean includeLight, final boolean includeBiomes) {
        final PacketBuffer output = new PacketBuffer();
        for (int section = 0; section < legacy.blocks.length; section++) {
            writeSection(output, version, legacy, legacy.sectionYs[section]);
            if (includeLight) {
                output.writeBytes(legacy.blockLight[section]);
                output.writeBytes(legacy.skyLight[section]);
            }
        }
        if (includeBiomes) {
            if (version.isLessThan(Version.V1_13)) {
                output.writeBytes(legacy.biomes);
            } else {
                for (int i = 0; i < 256; i++) {
                    output.writeInt(version.isLessThan(Version.V1_14)
                            ? Byte.toUnsignedInt(legacy.biomes[i]) : 1);
                }
            }
        }
        final byte[] encoded = new byte[output.readableBytes()];
        output.readBytes(encoded);
        return encoded;
    }

    private void writeSection(final PacketBuffer output, final Version version,
                              final LegacyChunkData legacy, final int sectionY) {
        final int[] legacyStates = legacy.blocksAt(sectionY);
        final int[] states = new int[legacyStates.length];
        int nonAirBlocks = 0;
        int fluidBlocks = 0;
        final Map<Integer, Integer> palette = new LinkedHashMap<>();
        final int[] paletteIndexes = new int[states.length];
        for (int index = 0; index < states.length; index++) {
            final int legacyState = legacyStates[index];
            final int state;
            if (version.isEqualOrHigher(Version.V1_13)) {
                final int localX = index & 0x0F;
                final int localZ = index >>> 4 & 0x0F;
                final int blockY = sectionY * 16 + (index >>> 8);
                final int blockX = chunkX * 16 + localX;
                final int blockZ = chunkZ * 16 + localZ;
                final LegacyStairShape.Shape shape = LegacyStairShape.isStairs(legacyState)
                        ? LegacyStairShape.resolve(
                        (x, y, z) -> blockValueAt(legacy, x, y, z), blockX, blockY, blockZ)
                        : LegacyStairShape.Shape.STRAIGHT;
                state = ProtocolMappings.blockState(version, legacyState >>> 4,
                        legacyState & 0x0F, shape);
            } else {
                state = legacyState;
            }
            states[index] = state;
            if (state != 0) {
                nonAirBlocks++;
            }
            final int legacyBlockId = legacyState >>> 4;
            if (legacyBlockId >= 8 && legacyBlockId <= 11) fluidBlocks++;
            paletteIndexes[index] = palette.computeIfAbsent(state, ignored -> palette.size());
        }
        if (version.isEqualOrHigher(Version.V1_14)) {
            output.writeShort(nonAirBlocks);
            if (version.isEqualOrHigher(Version.V26_1)) {
                output.writeShort(fluidBlocks);
            }
        }
        final boolean direct = palette.size() > 256;
        final int bitsPerBlock = direct ? directBits(version) : Math.max(4, bitsRequired(paletteIndexes));
        output.writeByte(bitsPerBlock);
        if (!direct) {
            output.writeVarInt(palette.size());
            palette.keySet().forEach(output::writeVarInt);
        }
        final int[] values = direct ? states : paletteIndexes;
        writePacked(output, values, bitsPerBlock, version.isEqualOrHigher(Version.V1_16),
                version.isEqualOrHigher(Version.V1_21_5));
    }

    private int blockValueAt(final LegacyChunkData legacy, final int blockX, final int blockY, final int blockZ) {
        if (world != null) return world.getBlockValue(blockX, blockY, blockZ);
        if (Math.floorDiv(blockX, 16) != chunkX || Math.floorDiv(blockZ, 16) != chunkZ) return 0;
        return legacy.blockAt(Math.floorMod(blockX, 16), blockY, Math.floorMod(blockZ, 16));
    }
}
