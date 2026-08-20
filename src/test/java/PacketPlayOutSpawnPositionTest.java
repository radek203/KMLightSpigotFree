import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.netty.channel.embedded.EmbeddedChannel;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutSpawnPosition;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;

class PacketPlayOutSpawnPositionTest {

    @Test
    void packetShouldUsePre114PositionLayout() {
        final int x = 17;
        final int y = 64;
        final int z = -9;
        final PacketBuffer buffer = new PacketBuffer();

        new PacketPlayOutSpawnPosition(x, y, z).write(null, buffer);

        final long protocol18Position = ((x & 0x3FFFFFFL) << 38) | ((y & 0xFFFL) << 26) | (z & 0x3FFFFFFL);
        Assertions.assertEquals(protocol18Position, buffer.readLong());
        Assertions.assertFalse(buffer.isReadable());
    }

    @Test
    void packetShouldIncludeDimensionAndPitchSince12111() {
        final PlayerConnection connection = new PlayerConnection(new EmbeddedChannel(), null);
        connection.setVersion(Version.V1_21_11);
        final PacketBuffer buffer = new PacketBuffer();

        new PacketPlayOutSpawnPosition(1, 64, 2).write(connection, buffer);

        Assertions.assertEquals("minecraft:overworld", buffer.readString());
        Assertions.assertEquals(new BlockPosition(1, 64, 2).toLong(Version.V1_21_11), buffer.readLong());
        Assertions.assertEquals(0.0F, buffer.readFloat());
        Assertions.assertEquals(0.0F, buffer.readFloat());
        Assertions.assertFalse(buffer.isReadable());
        connection.getChannel().close();
    }
}
