import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutSpawnPosition;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;

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
}
