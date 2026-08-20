import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.*;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.PacketPlayInBlockDig;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.PacketPlayInBlockPlace;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.PacketPlayInKeepAlive;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.PacketPlayInTransaction;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.PacketLoginInAcknowledged;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutFeatureFlags;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutTags;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutBlockChange;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkBatchFinish;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkBatchStart;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkData;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutGameEvent;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutUpdateViewPosition;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutUpdateLight;
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

    @Test
    void protocols113Through1171ShouldUseTheirPlayPacketIds() {
        final PacketManager manager = registeredManager();
        Assertions.assertEquals(0x18, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_13, PacketPlayInBlockDig.class));
        Assertions.assertEquals(0x1A, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_14_4, PacketPlayInBlockDig.class));
        Assertions.assertEquals(0x1B, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_16_4, PacketPlayInBlockDig.class));
        Assertions.assertEquals(0x1A, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_17_1, PacketPlayInBlockDig.class));
        Assertions.assertEquals(0x0E, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_13_2, PacketPlayInKeepAlive.class));
        Assertions.assertEquals(0x0F, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_14, PacketPlayInKeepAlive.class));
        Assertions.assertEquals(0x06, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_13, PacketPlayInTransaction.class));
        Assertions.assertEquals(0x07, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_16_4, PacketPlayInTransaction.class));
        Assertions.assertEquals(-1, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_17, PacketPlayInTransaction.class));
        Assertions.assertEquals(0x22, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_13_2, PacketPlayOutChunkData.class));
        Assertions.assertEquals(0x21, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_14_2, PacketPlayOutChunkData.class));
        Assertions.assertEquals(0x20, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_16_4, PacketPlayOutChunkData.class));
        Assertions.assertEquals(0x22, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_17_1, PacketPlayOutChunkData.class));
        Assertions.assertEquals(0x25, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_17_1, PacketPlayOutUpdateLight.class));
    }

    @Test
    void protocols118Through262ShouldUseModernStateAndPlayIds() {
        final PacketManager manager = registeredManager();
        Assertions.assertEquals(0x22, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_18, PacketPlayOutChunkData.class));
        Assertions.assertEquals(0x25, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_20_2, PacketPlayOutChunkData.class));
        Assertions.assertEquals(0x2D, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V26_2, PacketPlayOutChunkData.class));
        Assertions.assertEquals(0x0C, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_20_3, PacketPlayOutChunkBatchFinish.class));
        Assertions.assertEquals(0x0D, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_20_3, PacketPlayOutChunkBatchStart.class));
        Assertions.assertEquals(0x0B, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_21_7, PacketPlayOutChunkBatchFinish.class));
        Assertions.assertEquals(0x0C, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_21_7, PacketPlayOutChunkBatchStart.class));
        Assertions.assertEquals(0x20, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_20_3, PacketPlayOutGameEvent.class));
        Assertions.assertEquals(0x22, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_21_7, PacketPlayOutGameEvent.class));
        Assertions.assertEquals(0x26, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V26_2, PacketPlayOutGameEvent.class));
        Assertions.assertEquals(0x57, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_21_7, PacketPlayOutUpdateViewPosition.class));
        Assertions.assertEquals(0x5E, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V26_2, PacketPlayOutUpdateViewPosition.class));
        Assertions.assertEquals(0x30, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V26_2, PacketPlayOutUpdateLight.class));
        Assertions.assertEquals(0x29, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V26_2, PacketPlayInBlockDig.class));
        Assertions.assertEquals(0x41, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V26_2, PacketPlayInBlockPlace.class));
        Assertions.assertEquals(0x03, manager.getPacketId(
                PacketDirection.SERVERBOUND, Version.V1_20_2, PacketLoginInAcknowledged.class));
        Assertions.assertTrue(manager.containsPacketId(
                PacketDirection.SERVERBOUND, Version.V1_20_2, ConnectionState.CONFIGURATION, 0x02));
        Assertions.assertTrue(manager.containsPacketId(
                PacketDirection.SERVERBOUND, Version.V26_2, ConnectionState.CONFIGURATION, 0x03));
        Assertions.assertEquals(0x08, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_20_3, PacketConfigurationOutFeatureFlags.class));
        Assertions.assertEquals(0x09, manager.getPacketId(
                PacketDirection.CLIENTBOUND, Version.V1_20_3, PacketConfigurationOutTags.class));
    }

    private static PacketManager registeredManager() {
        final PacketManager manager = new PacketManager();
        manager.registerPackets(null);
        return manager;
    }
}
