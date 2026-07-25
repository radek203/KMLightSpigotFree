import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutAbilities;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;

class PacketPlayOutAbilitiesTest {

    @Test
    void flightFlagsShouldAllowRestartingFlightAfterLanding() {
        final PacketBuffer buffer = new PacketBuffer();
        new PacketPlayOutAbilities(
                PacketPlayOutAbilities.FLYING | PacketPlayOutAbilities.ALLOW_FLYING,
                PacketPlayOutAbilities.DEFAULT_FLYING_SPEED,
                PacketPlayOutAbilities.DEFAULT_FIELD_OF_VIEW
        ).write(null, buffer);

        Assertions.assertEquals(0x06, buffer.readUnsignedByte());
        Assertions.assertEquals(0.1F, buffer.readFloat());
        Assertions.assertEquals(0.1F, buffer.readFloat());
        Assertions.assertFalse(buffer.isReadable());
    }
}
