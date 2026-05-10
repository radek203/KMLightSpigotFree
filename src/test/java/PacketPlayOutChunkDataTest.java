import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkData;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.ChunkCoordinate;
import pl.kwadratowamasakra.lightspigot.world.ChunkSnapshot;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.ChunkUtils;

class PacketPlayOutChunkDataTest {

    @Test
    void protocol18ShouldTransmitLegacyChunkDataWithoutConversion() {
        final ChunkSnapshot chunk = oneSectionChunk();
        final PacketBuffer output = new PacketBuffer();
        final EmbeddedChannel channel = new EmbeddedChannel();
        final PlayerConnection connection = new PlayerConnection(channel, null);
        connection.setVersion(Version.V1_8);

        packet(chunk).write(connection, output);

        assertHeader(output, chunk);
        Assertions.assertEquals(chunk.primaryBitMask(), output.readUnsignedShort());
        Assertions.assertEquals(chunk.chunkData().length, output.readVarInt());
        final byte[] payload = new byte[output.readableBytes()];
        output.readBytes(payload);
        Assertions.assertArrayEquals(chunk.chunkData(), payload);
        channel.finishAndReleaseAll();
    }

    @Test
    void protocol19ShouldConvertLegacySectionsToPaletteFormat() {
        final ChunkSnapshot chunk = twoSectionChunk();
        final PacketBuffer output = new PacketBuffer();
        final EmbeddedChannel channel = new EmbeddedChannel();
        final PlayerConnection connection = new PlayerConnection(channel, null);
        connection.setVersion(Version.V1_9);

        packet(chunk).write(connection, output);

        assertHeader(output, chunk);
        Assertions.assertEquals(chunk.primaryBitMask(), output.readVarInt());
        final int payloadLength = output.readVarInt();
        Assertions.assertTrue(payloadLength > 256);
        Assertions.assertEquals(payloadLength, output.readableBytes());
        channel.finishAndReleaseAll();
    }

    @Test
    void protocol193ShouldAppendEmptyBlockEntityList() {
        final ChunkSnapshot chunk = oneSectionChunk();
        final PacketBuffer output = new PacketBuffer();
        final EmbeddedChannel channel = new EmbeddedChannel();
        final PlayerConnection connection = new PlayerConnection(channel, null);
        connection.setVersion(Version.V1_9_3);

        packet(chunk).write(connection, output);

        assertHeader(output, chunk);
        Assertions.assertEquals(chunk.primaryBitMask(), output.readVarInt());
        final int payloadLength = output.readVarInt();
        output.skipBytes(payloadLength);
        Assertions.assertEquals(0, output.readVarInt());
        Assertions.assertFalse(output.isReadable());
        channel.finishAndReleaseAll();
    }

    private static PacketPlayOutChunkData packet(final ChunkSnapshot chunk) {
        return new PacketPlayOutChunkData(
                chunk.chunkX(), chunk.chunkZ(), true, chunk.primaryBitMask(), chunk.chunkData());
    }

    private static ChunkSnapshot oneSectionChunk() {
        final int[][][] blocks = ChunkUtils.createEmptyChunkBlocks();
        blocks[64][0][0] = BlockUtils.createLegacyBlockValue(1, 0);
        return ChunkUtils.createChunkSnapshot(new ChunkCoordinate(-2, 3), blocks, 1 << 4);
    }

    private static ChunkSnapshot twoSectionChunk() {
        final int[][][] blocks = ChunkUtils.createEmptyChunkBlocks();
        blocks[64][0][0] = BlockUtils.createLegacyBlockValue(1, 0);
        blocks[80][1][1] = BlockUtils.createLegacyBlockValue(53, 2);
        return ChunkUtils.createChunkSnapshot(new ChunkCoordinate(-2, 3), blocks, 1 << 4);
    }

    private static void assertHeader(final PacketBuffer output, final ChunkSnapshot chunk) {
        Assertions.assertEquals(chunk.chunkX(), output.readInt());
        Assertions.assertEquals(chunk.chunkZ(), output.readInt());
        Assertions.assertTrue(output.readBoolean());
    }
}
