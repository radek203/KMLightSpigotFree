import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketLoginOutSuccess;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkData;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkBatchFinish;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkBatchStart;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutGameEvent;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutLogin;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutUpdateLight;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutUpdateViewPosition;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolConfiguration;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolResources;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.ChunkCoordinate;
import pl.kwadratowamasakra.lightspigot.world.ChunkSnapshot;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.ChunkUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

class ModernProtocolPacketTest {

    @Test
    void protocolResourcesShouldPreloadIdempotently() {
        Assertions.assertDoesNotThrow(ProtocolResources::preload);
        Assertions.assertDoesNotThrow(ProtocolResources::preload);
    }

    @Test
    void chunkBatchPacketsShouldUseAnEmptyStartAndChunkCountFinish() {
        final PacketBuffer start = new PacketBuffer();
        new PacketPlayOutChunkBatchStart().write(null, start);
        Assertions.assertFalse(start.isReadable());

        final PacketBuffer finish = new PacketBuffer();
        new PacketPlayOutChunkBatchFinish(4).write(null, finish);
        Assertions.assertEquals(4, finish.readVarInt());
        Assertions.assertFalse(finish.isReadable());
    }

    @Test
    void levelChunksLoadEventShouldReleaseTheModernClientLoadingState() {
        final PacketBuffer output = new PacketBuffer();
        new PacketPlayOutGameEvent(PacketPlayOutGameEvent.LEVEL_CHUNKS_LOAD_START, 0.0F)
                .write(null, output);

        Assertions.assertEquals(13, output.readUnsignedByte());
        Assertions.assertEquals(0.0F, output.readFloat());
        Assertions.assertFalse(output.isReadable());
    }

    @Test
    void updateViewPositionShouldUseSignedChunkVarInts() {
        final PacketBuffer output = new PacketBuffer();
        new PacketPlayOutUpdateViewPosition(-37, -12).write(null, output);
        Assertions.assertEquals(-37, output.readVarInt());
        Assertions.assertEquals(-12, output.readVarInt());
        Assertions.assertFalse(output.isReadable());
    }

    @Test
    void loginSuccessShouldSwitchToBinaryUuidIn116() {
        final UUID uuid = UUID.fromString("12345678-1234-5678-9abc-def012345678");
        final PacketBuffer output = new PacketBuffer();
        final PlayerConnection connection = connection(Version.V1_16);
        new PacketLoginOutSuccess(uuid, "Player").write(connection, output);
        Assertions.assertEquals(uuid, output.readUUID());
        Assertions.assertEquals("Player", output.readString());
        Assertions.assertFalse(output.isReadable());
        connection.getChannel().close();
    }

    @Test
    void login117ShouldContainDimensionRegistryAndAllTrailingFlags() {
        final PacketBuffer output = new PacketBuffer();
        final PlayerConnection connection = connection(Version.V1_17_1);
        new PacketPlayOutLogin(42, 3, 0, 0, 20, "flat", 2, true).write(connection, output);
        Assertions.assertEquals(42, output.readInt());
        Assertions.assertFalse(output.readBoolean());
        Assertions.assertEquals(3, output.readUnsignedByte());
        Assertions.assertEquals(-1, output.readByte());
        Assertions.assertEquals(1, output.readVarInt());
        Assertions.assertEquals("minecraft:overworld", output.readString());
        output.skipOptionalNbt();
        output.skipOptionalNbt();
        Assertions.assertEquals("minecraft:overworld", output.readString());
        Assertions.assertEquals(0L, output.readLong());
        Assertions.assertEquals(20, output.readVarInt());
        Assertions.assertEquals(2, output.readVarInt());
        Assertions.assertTrue(output.readBoolean());
        Assertions.assertTrue(output.readBoolean());
        Assertions.assertFalse(output.readBoolean());
        Assertions.assertTrue(output.readBoolean());
        Assertions.assertFalse(output.isReadable());
        connection.getChannel().close();
    }

    @Test
    void chunkAndLight117ShouldUseLongMasksAndSeparatedLightArrays() {
        final int[][][] blocks = ChunkUtils.createEmptyChunkBlocks();
        blocks[64][0][0] = BlockUtils.createLegacyBlockValue(1, 0);
        final ChunkSnapshot chunk = ChunkUtils.createChunkSnapshot(
                new ChunkCoordinate(-2, 3), blocks, 1 << 4);
        final PlayerConnection connection = connection(Version.V1_17_1);

        final PacketBuffer chunkOutput = new PacketBuffer();
        new PacketPlayOutChunkData(chunk.chunkX(), chunk.chunkZ(), true,
                chunk.primaryBitMask(), chunk.chunkData()).write(connection, chunkOutput);
        Assertions.assertEquals(-2, chunkOutput.readInt());
        Assertions.assertEquals(3, chunkOutput.readInt());
        Assertions.assertEquals(1, chunkOutput.readVarInt());
        Assertions.assertEquals(chunk.primaryBitMask(), chunkOutput.readLong());
        chunkOutput.skipOptionalNbt();
        Assertions.assertEquals(1024, chunkOutput.readVarInt());
        for (int i = 0; i < 1024; i++) Assertions.assertEquals(1, chunkOutput.readVarInt());
        chunkOutput.skipBytes(chunkOutput.readVarInt());
        Assertions.assertEquals(0, chunkOutput.readVarInt());
        Assertions.assertFalse(chunkOutput.isReadable());

        final PacketBuffer lightOutput = new PacketBuffer();
        new PacketPlayOutUpdateLight(chunk.chunkX(), chunk.chunkZ(),
                chunk.primaryBitMask(), chunk.chunkData()).write(connection, lightOutput);
        Assertions.assertEquals(-2, lightOutput.readVarInt());
        Assertions.assertEquals(3, lightOutput.readVarInt());
        Assertions.assertTrue(lightOutput.readBoolean());
        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(1, lightOutput.readVarInt());
            lightOutput.readLong();
        }
        for (int kind = 0; kind < 2; kind++) {
            Assertions.assertEquals(Integer.bitCount(chunk.primaryBitMask()), lightOutput.readVarInt());
            for (int section = 0; section < Integer.bitCount(chunk.primaryBitMask()); section++) {
                Assertions.assertEquals(2048, lightOutput.readVarInt());
                lightOutput.skipBytes(2048);
            }
        }
        Assertions.assertFalse(lightOutput.isReadable());
        connection.getChannel().close();
    }

    @Test
    void chunk118ShouldContainTwentyFourSectionsAndInlineLight() {
        final int[][][] blocks = ChunkUtils.createEmptyChunkBlocks();
        blocks[64][0][0] = BlockUtils.createLegacyBlockValue(1, 0);
        final ChunkSnapshot chunk = ChunkUtils.createChunkSnapshot(new ChunkCoordinate(1, -1), blocks, 1 << 4);
        final PlayerConnection connection = connection(Version.V1_18);
        final PacketBuffer output = new PacketBuffer();
        new PacketPlayOutChunkData(1, -1, true, chunk.primaryBitMask(), chunk.chunkData()).write(connection, output);
        Assertions.assertEquals(1, output.readInt());
        Assertions.assertEquals(-1, output.readInt());
        output.skipOptionalNbt();
        final int sectionBytes = output.readVarInt();
        Assertions.assertTrue(sectionBytes > 24 * 5);
        output.skipBytes(sectionBytes);
        Assertions.assertEquals(0, output.readVarInt());
        Assertions.assertTrue(output.readBoolean());
        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(1, output.readVarInt());
            output.readLong();
        }
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(1, output.readVarInt());
            Assertions.assertEquals(2048, output.readVarInt());
            output.skipBytes(2048);
        }
        Assertions.assertFalse(output.isReadable());
        connection.getChannel().close();
    }

    @Test
    void chunk1217ShouldContainClientHeightmaps() {
        final int[][][] blocks = ChunkUtils.createEmptyChunkBlocks();
        blocks[64][0][0] = BlockUtils.createLegacyBlockValue(1, 0);
        blocks[80][1][1] = BlockUtils.createLegacyBlockValue(1, 0);
        final ChunkSnapshot chunk = ChunkUtils.createChunkSnapshot(new ChunkCoordinate(0, 0), blocks, 1 << 4);
        final PlayerConnection connection = connection(Version.V1_21_7);
        final PacketBuffer output = new PacketBuffer();
        new PacketPlayOutChunkData(0, 0, true, chunk.primaryBitMask(), chunk.chunkData()).write(connection, output);

        Assertions.assertEquals(0, output.readInt());
        Assertions.assertEquals(0, output.readInt());
        skipNetworkHeightmaps(output);
        final byte[] sectionData = new byte[output.readVarInt()];
        output.readBytes(sectionData);
        Assertions.assertEquals(0, parseNoLengthPrefixSections(sectionData, false));
        connection.getChannel().close();
    }

    @Test
    void chunk261ShouldIncludeFluidCountsAndNoPaletteLengths() {
        final int[][][] blocks = ChunkUtils.createEmptyChunkBlocks();
        blocks[64][0][0] = BlockUtils.createLegacyBlockValue(8, 0);
        final ChunkSnapshot chunk = ChunkUtils.createChunkSnapshot(new ChunkCoordinate(0, 0), blocks, 1 << 4);
        final PlayerConnection connection = connection(Version.V26_1);
        final PacketBuffer output = new PacketBuffer();
        new PacketPlayOutChunkData(0, 0, true, chunk.primaryBitMask(), chunk.chunkData()).write(connection, output);

        output.skipBytes(8);
        skipNetworkHeightmaps(output);
        final byte[] sectionData = new byte[output.readVarInt()];
        output.readBytes(sectionData);
        Assertions.assertEquals(1, parseNoLengthPrefixSections(sectionData, true));
        connection.getChannel().close();
    }

    private static void skipNetworkHeightmaps(final PacketBuffer output) {
        Assertions.assertEquals(3, output.readVarInt());
        for (final int expectedType : new int[]{5, 1, 4}) {
            Assertions.assertEquals(expectedType, output.readVarInt());
            Assertions.assertEquals(37, output.readVarInt());
            final long first = output.readLong();
            Assertions.assertEquals(129L, first & 0x1FFL);
            for (int value = 1; value < 37; value++) output.readLong();
        }
    }

    private static int parseNoLengthPrefixSections(final byte[] data, final boolean hasFluidCount) {
        final PacketBuffer sections = new PacketBuffer();
        sections.writeBytes(data);
        int fluids = 0;
        for (int section = 0; section < 24; section++) {
            sections.readUnsignedShort();
            if (hasFluidCount) fluids += sections.readUnsignedShort();
            final int blockBits = sections.readUnsignedByte();
            final int paletteSize = sections.readVarInt();
            for (int palette = 0; palette < paletteSize; palette++) sections.readVarInt();
            final int blockLongs = (4096 + 64 / blockBits - 1) / (64 / blockBits);
            sections.skipBytes(blockLongs * Long.BYTES);
            Assertions.assertEquals(0, sections.readUnsignedByte());
            sections.readVarInt(); // single biome value; there is no trailing data-array length
        }
        Assertions.assertFalse(sections.isReadable());
        return fluids;
    }

    @Test
    void everyConfigurationProtocolShouldHaveRegistryPayloads() {
        for (final Version version : Version.getVersionsRange(Version.V1_20_2, Version.V26_2)) {
            Assertions.assertFalse(ProtocolConfiguration.registries(version).isEmpty(), version.name());
        }
    }

    @Test
    void configuration262ShouldUseTheCurrentEntityVariantSchema() {
        final List<byte[]> registries = ProtocolConfiguration.registries(Version.V26_2);
        Assertions.assertEquals(29, registries.size());
        final String payload = registries.stream()
                .map(bytes -> new String(bytes, StandardCharsets.ISO_8859_1))
                .reduce("", String::concat);

        Assertions.assertTrue(payload.contains("minecraft:pig_sound_variant"));
        Assertions.assertTrue(payload.contains("minecraft:cat_sound_variant"));
        Assertions.assertTrue(payload.contains("baby_asset_id"));
        Assertions.assertTrue(payload.contains("minecraft:entity/cat/cat_all_black"));
        Assertions.assertFalse(payload.contains("minecraft:entity/cat/all_black"));
    }

    @Test
    void configuration1217ShouldContainRequiredVariantRegistries() {
        final Map<String, Integer> sizes = new HashMap<>();
        final List<byte[]> registries = ProtocolConfiguration.registries(Version.V1_21_7);
        Assertions.assertEquals(21, registries.size());
        for (final byte[] registry : registries) {
            final PacketBuffer packet = new PacketBuffer();
            packet.writeBytes(registry);
            sizes.put(packet.readString(), packet.readVarInt());
        }

        for (final String registry : List.of("minecraft:cat_variant", "minecraft:chicken_variant",
                "minecraft:cow_variant", "minecraft:frog_variant", "minecraft:pig_variant",
                "minecraft:wolf_sound_variant", "minecraft:wolf_variant")) {
            Assertions.assertTrue(sizes.getOrDefault(registry, 0) > 0, registry);
        }
    }

    @Test
    void modernTagsPacketShouldIncludeRegistryAndClientRequiredTags() {
        final PacketBuffer output = new PacketBuffer();
        ProtocolConfiguration.writeRequiredTags(Version.V1_21_2, output);
        final Map<String, Set<String>> tagsByRegistry = new HashMap<>();
        final int registryCount = output.readVarInt();
        for (int registry = 0; registry < registryCount; registry++) {
            final String registryName = output.readString();
            final Set<String> tags = new HashSet<>();
            final int tagCount = output.readVarInt();
            for (int tag = 0; tag < tagCount; tag++) {
                tags.add(output.readString());
                final int entryCount = output.readVarInt();
                for (int entry = 0; entry < entryCount; entry++) output.readVarInt();
            }
            tagsByRegistry.put(registryName, tags);
        }
        Assertions.assertTrue(tagsByRegistry.get("minecraft:item")
                .contains("minecraft:enchantable/head_armor"));
        Assertions.assertTrue(tagsByRegistry.get("minecraft:enchantment")
                .contains("minecraft:exclusive_set/armor"));
        Assertions.assertFalse(output.isReadable());

        final PacketBuffer output262 = new PacketBuffer();
        ProtocolConfiguration.writeRequiredTags(Version.V26_2, output262);
        boolean foundFireDamage = false;
        final int registryCount262 = output262.readVarInt();
        for (int registry = 0; registry < registryCount262; registry++) {
            final String registryName = output262.readString();
            final int tagCount = output262.readVarInt();
            for (int tag = 0; tag < tagCount; tag++) {
                final String tagName = output262.readString();
                foundFireDamage |= registryName.equals("minecraft:damage_type")
                        && tagName.equals("minecraft:is_fire");
                final int entryCount = output262.readVarInt();
                for (int entry = 0; entry < entryCount; entry++) output262.readVarInt();
            }
        }
        Assertions.assertTrue(foundFireDamage);
        Assertions.assertFalse(output262.isReadable());
    }

    private static PlayerConnection connection(final Version version) {
        final PlayerConnection connection = new PlayerConnection(new EmbeddedChannel(), null);
        connection.setVersion(version);
        return connection;
    }
}
