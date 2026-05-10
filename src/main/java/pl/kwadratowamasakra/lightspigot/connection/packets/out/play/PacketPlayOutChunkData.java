package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutChunkData extends PacketOut {

    private static final int SECTION_BLOCK_COUNT = 16 * 16 * 16;
    private static final int LIGHT_BYTES = 2048;
    private static final int BIOME_BYTES = 256;
    private static final int DIRECT_BITS_PER_BLOCK = 13;

    private int chunkX;
    private int chunkZ;
    private boolean groundUpContinuous;
    private int primaryBitMask;
    private byte[] data;

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeInt(chunkX);
        packetBuffer.writeInt(chunkZ);
        packetBuffer.writeBoolean(groundUpContinuous);
        if (connection.getVersion().isEqual(Version.V1_8)) {
            packetBuffer.writeShort(primaryBitMask);
            packetBuffer.writeVarInt(data.length);
            packetBuffer.writeBytes(data);
            return;
        }

        final byte[] modernData = convertLegacyDataToModernFormat(data);
        packetBuffer.writeVarInt(primaryBitMask);
        packetBuffer.writeVarInt(modernData.length);
        packetBuffer.writeBytes(modernData);

        if (connection.getVersion().isEqualOrHigher(Version.V1_9_3)) {
            packetBuffer.writeVarInt(0);
        }
    }

    private byte[] convertLegacyDataToModernFormat(final byte[] legacyData) {
        final PacketBuffer legacyBuffer = new PacketBuffer();
        legacyBuffer.writeBytes(legacyData);
        final PacketBuffer modernBuffer = new PacketBuffer();

        final int sections = Integer.bitCount(primaryBitMask);
        for (int sectionIndex = 0; sectionIndex < sections; sectionIndex++) {
            writeModernSection(legacyBuffer, modernBuffer);
        }

        if (groundUpContinuous && legacyBuffer.readableBytes() >= BIOME_BYTES) {
            final byte[] biomes = new byte[BIOME_BYTES];
            legacyBuffer.readBytes(biomes);
            modernBuffer.writeBytes(biomes);
        }

        final byte[] modernData = new byte[modernBuffer.readableBytes()];
        modernBuffer.readBytes(modernData);
        return modernData;
    }

    private void writeModernSection(final PacketBuffer legacyBuffer, final PacketBuffer modernBuffer) {
        final int[] blockStates = new int[SECTION_BLOCK_COUNT];
        for (int i = 0; i < SECTION_BLOCK_COUNT; i++) {
            final int legacyBlock = legacyBuffer.readUnsignedByte() | legacyBuffer.readUnsignedByte() << 8;
            blockStates[i] = legacyBlock;
        }

        final Map<Integer, Integer> paletteIndexByState = new LinkedHashMap<>();
        final int[] paletteIndices = new int[SECTION_BLOCK_COUNT];
        for (int blockIndex = 0; blockIndex < SECTION_BLOCK_COUNT; blockIndex++) {
            final int blockState = blockStates[blockIndex];
            Integer paletteIndex = paletteIndexByState.get(blockState);
            if (paletteIndex == null) {
                paletteIndex = paletteIndexByState.size();
                paletteIndexByState.put(blockState, paletteIndex);
            }
            paletteIndices[blockIndex] = paletteIndex;
        }

        final boolean useDirectPalette = paletteIndexByState.size() > 256;
        final int bitsPerBlock = useDirectPalette ? DIRECT_BITS_PER_BLOCK : Math.max(4, 32 - Integer.numberOfLeadingZeros(Math.max(1, paletteIndexByState.size() - 1)));
        modernBuffer.writeByte(bitsPerBlock);

        if (!useDirectPalette) {
            modernBuffer.writeVarInt(paletteIndexByState.size());
            for (final int blockState : paletteIndexByState.keySet()) {
                modernBuffer.writeVarInt(blockState);
            }
        }

        final int longArrayLength = SECTION_BLOCK_COUNT * bitsPerBlock / 64;
        modernBuffer.writeVarInt(longArrayLength);

        final long[] compacted = new long[longArrayLength];
        final long mask = (1L << bitsPerBlock) - 1L;
        for (int blockIndex = 0; blockIndex < SECTION_BLOCK_COUNT; blockIndex++) {
            final long value = (useDirectPalette ? blockStates[blockIndex] : paletteIndices[blockIndex]) & mask;
            final int bitIndex = blockIndex * bitsPerBlock;
            final int longIndex = bitIndex >>> 6;
            final int startBit = bitIndex & 63;

            compacted[longIndex] |= value << startBit;
            final int endBit = startBit + bitsPerBlock;
            if (endBit > 64) {
                compacted[longIndex + 1] |= value >>> (64 - startBit);
            }
        }

        Arrays.stream(compacted).forEach(modernBuffer::writeLong);

        final byte[] blockLight = new byte[LIGHT_BYTES];
        legacyBuffer.readBytes(blockLight);
        modernBuffer.writeBytes(blockLight);

        final byte[] skyLight = new byte[LIGHT_BYTES];
        legacyBuffer.readBytes(skyLight);
        modernBuffer.writeBytes(skyLight);
    }
}
