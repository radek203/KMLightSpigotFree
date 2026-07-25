import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.*;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.PacketPlayInBlockDig;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.PacketPlayInBlockPlace;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutBlockChange;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketManager;

class PacketManagerTest {

    @Test
    void critical18WorldPacketsShouldUseProtocolIds() {
        final PacketManager manager = registeredManager();

        Assertions.assertTrue(manager.containsPacketId(
                PacketDirection.SERVERBOUND, Version.V1_8, ConnectionState.PLAY, 0x07));
        Assertions.assertTrue(manager.containsPacketId(
                PacketDirection.SERVERBOUND, Version.V1_8, ConnectionState.PLAY, 0x08));
        Assertions.assertEquals(0x23, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_8, PacketPlayOutBlockChange.class));
    }

    @Test
    void packetIdsShouldFollowVersionSpecificMappings() {
        final PacketManager manager = registeredManager();

        Assertions.assertEquals(0x0B, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_12_2, PacketPlayOutBlockChange.class));
        Assertions.assertTrue(manager.containsPacketId(
                PacketDirection.SERVERBOUND, Version.V1_12_2, ConnectionState.PLAY, 0x14));
        Assertions.assertTrue(manager.containsPacketId(
                PacketDirection.SERVERBOUND, Version.V1_12_2, ConnectionState.PLAY, 0x1F));
        Assertions.assertFalse(manager.containsPacketId(
                PacketDirection.SERVERBOUND, Version.V1_8, ConnectionState.PLAY, 0x1F));
    }

    @Test
    void registeredPacketsShouldConstructFreshInstances() throws Exception {
        final PacketManager manager = registeredManager();

        final Object first = manager.constructPacket(
                PacketDirection.SERVERBOUND, Version.V1_8, ConnectionState.PLAY, 0x07);
        final Object second = manager.constructPacket(
                PacketDirection.SERVERBOUND, Version.V1_8, ConnectionState.PLAY, 0x07);

        Assertions.assertInstanceOf(PacketPlayInBlockDig.class, first);
        Assertions.assertNotSame(first, second);
        Assertions.assertInstanceOf(PacketPlayInBlockPlace.class, manager.constructPacket(
                PacketDirection.SERVERBOUND, Version.V1_8, ConnectionState.PLAY, 0x08));
    }

    @Test
    void unregisteredPacketClassShouldReturnNegativeId() {
        final PacketManager manager = registeredManager();
        Assertions.assertEquals(-1, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_8, PacketPlayInBlockPlace.class));
    }

    private static PacketManager registeredManager() {
        final PacketManager manager = new PacketManager();
        manager.registerPackets(null);
        return manager;
    }
}
