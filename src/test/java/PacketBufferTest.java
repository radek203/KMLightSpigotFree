import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;

import java.util.UUID;

class PacketBufferTest {

    @Test
    void varIntShouldRoundTripProtocolBoundaryValues() {
        final int[] values = {0, 1, 127, 128, 255, 16_383, 16_384, 2_097_151, Integer.MAX_VALUE, -1, Integer.MIN_VALUE};
        final PacketBuffer buffer = new PacketBuffer();
        for (final int value : values) {
            buffer.writeVarInt(value);
        }
        for (final int value : values) {
            Assertions.assertEquals(value, buffer.readVarInt());
        }
        Assertions.assertFalse(buffer.isReadable());
    }

    @Test
    void malformedVarIntsShouldFailWithProtocolException() {
        final PacketBuffer incomplete = new PacketBuffer();
        incomplete.writeByte(0x80);
        Assertions.assertThrows(IllegalArgumentException.class, incomplete::readVarInt);

        final PacketBuffer tooLong = new PacketBuffer();
        tooLong.writeBytes(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80});
        Assertions.assertThrows(IllegalArgumentException.class, tooLong::readVarInt);

        final PacketBuffer overflow = new PacketBuffer();
        overflow.writeBytes(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x10});
        Assertions.assertThrows(IllegalArgumentException.class, overflow::readVarInt);
    }

    @Test
    void stringsByteArraysAndUuidsShouldRoundTrip() {
        final PacketBuffer buffer = new PacketBuffer();
        final String text = "Zażółć gęślą 🎮";
        final byte[] bytes = {0, 1, -1, 127};
        final UUID uuid = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");

        buffer.writeString(text);
        buffer.writeBytesArray(bytes);
        buffer.writeUUID(uuid);

        Assertions.assertEquals(text, buffer.readString());
        Assertions.assertArrayEquals(bytes, buffer.readBytesArray());
        Assertions.assertEquals(uuid, buffer.readUUID());
        Assertions.assertFalse(buffer.isReadable());
    }

    @Test
    void declaredLengthsMustNotExceedAvailablePayload() {
        final PacketBuffer string = new PacketBuffer();
        string.writeVarInt(4);
        string.writeByte('a');
        Assertions.assertThrows(IllegalArgumentException.class, string::readString);

        final PacketBuffer array = new PacketBuffer();
        array.writeVarInt(3);
        array.writeByte(1);
        Assertions.assertThrows(IllegalArgumentException.class, array::readBytesArray);
    }

    @Test
    void itemStacksShouldPreserveLegacyIdCountAndData() {
        final PacketBuffer buffer = new PacketBuffer();
        buffer.writeItemStack(new ItemStack(53, 12, 3));
        buffer.writeItemStack(new ItemStack());
        buffer.writeItemStack(null);

        final ItemStack item = buffer.readItemStack();
        Assertions.assertTrue(item.isItem());
        Assertions.assertEquals(53, item.getItemId());
        Assertions.assertEquals(12, item.getCount());
        Assertions.assertEquals(3, item.getData());
        Assertions.assertFalse(buffer.readItemStack().isItem());
        Assertions.assertFalse(buffer.readItemStack().isItem());
        Assertions.assertFalse(buffer.isReadable());
    }

    @Test
    void enumOrdinalsShouldBeValidated() {
        final PacketBuffer valid = new PacketBuffer();
        valid.writeEnumValue(TestValue.SECOND);
        Assertions.assertEquals(TestValue.SECOND, valid.readEnumValue(TestValue.class));

        final PacketBuffer invalid = new PacketBuffer();
        invalid.writeVarInt(2);
        Assertions.assertThrows(IllegalArgumentException.class, () -> invalid.readEnumValue(TestValue.class));
    }

    private enum TestValue {
        FIRST,
        SECOND
    }
}
