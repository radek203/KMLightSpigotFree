import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutBlockChange;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;

class PacketPlayOutBlockChangeTest {

    @Test
    void packetShouldWritePackedPositionAsLongAndLegacyBlockStateAsVarInt() {
        final int x = -12;
        final int y = 70;
        final int z = 34;
        final PacketBuffer buffer = new PacketBuffer();

        new PacketPlayOutBlockChange(x, y, z, 57, 3).write(null, buffer);

        final long expectedPosition = ((x & 0x3FFFFFFL) << 38) | ((y & 0xFFFL) << 26) | (z & 0x3FFFFFFL);
        Assertions.assertEquals(expectedPosition, buffer.readLong());
        Assertions.assertEquals(57 << 4 | 3, buffer.readVarInt());
        Assertions.assertFalse(buffer.isReadable());
    }
}
