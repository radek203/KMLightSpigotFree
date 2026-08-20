package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.PacketDirection;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.PacketHandshakingInSetProtocol;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.configuration.PacketConfigurationInFinish;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.configuration.PacketConfigurationInIgnored;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.PacketLoginInAcknowledged;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.PacketLoginInEncryptionBegin;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.PacketLoginInStart;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.*;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.status.PacketStatusInPing;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.status.PacketStatusInStart;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutFeatureFlags;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutFinish;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutRegistryData;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutTags;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketLoginOutDisconnect;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketLoginOutEncryptionBegin;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketLoginOutSetCompression;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketLoginOutSuccess;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.*;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.status.PacketStatusOutPong;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.status.PacketStatusOutServerInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * The PacketManager class is responsible for managing the packets in the network communication.
 * It includes methods for registering packets, getting packet IDs, constructing packets, and checking if a packet ID exists.
 */
public class PacketManager {

    private final Map<PacketDirection, Map<Version, Map<ConnectionState, Map<Integer, RegisteredPacket>>>> packets = new EnumMap<>(PacketDirection.class);
    private final Map<PacketDirection, Map<Version, Map<Class<? extends Packet>, Integer>>> packetClassToId = new EnumMap<>(PacketDirection.class);

    private static PacketMapping map(final int packetId) {
        return new PacketMapping(Version.values(), packetId);
    }

    private static PacketMapping map(final Version version, final int packetId) {
        return new PacketMapping(version, packetId);
    }

    private static PacketMapping map(final Version versionMin, final Version versionMax, final int packetId) {
        return new PacketMapping(versionMin, versionMax, packetId);
    }

    private static PacketMapping[] modern(final int... packetIds) {
        final Version[] versions = Version.getVersionsRange(Version.V1_18, Version.V26_2);
        if (packetIds.length != versions.length) {
            throw new IllegalArgumentException("Expected " + versions.length + " modern packet ids, got " + packetIds.length);
        }
        final PacketMapping[] mappings = new PacketMapping[versions.length];
        for (int i = 0; i < versions.length; i++) mappings[i] = map(versions[i], packetIds[i]);
        return mappings;
    }

    /**
     * Registers all the packets that will be used in the network communication.
     *
     * @param server The server that will be using the packets.
     */
    public final void registerPackets(final LightSpigotServer server) {
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.HANDSHAKING, PacketHandshakingInSetProtocol.class, map(0x00));

        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.STATUS, PacketStatusInStart.class, map(0x00));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.STATUS, PacketStatusInPing.class, map(0x01));

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.STATUS, PacketStatusOutServerInfo.class, map(0x00));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.STATUS, PacketStatusOutPong.class, map(0x01));

        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.LOGIN, PacketLoginInStart.class, map(0x00));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.LOGIN, PacketLoginInEncryptionBegin.class, map(0x01));

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.LOGIN, PacketLoginOutDisconnect.class, map(0x00));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.LOGIN, PacketLoginOutEncryptionBegin.class, map(0x01));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.LOGIN, PacketLoginOutSuccess.class, map(0x02));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.LOGIN, PacketLoginOutSetCompression.class, map(0x03));

        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInTeleportAccept.class,
                map(Version.V1_9, Version.V1_17_1, 0x00)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInKeepAlive.class,
                map(Version.V1_8, 0x00), map(Version.V1_9, Version.V1_11_1, 0x0B), map(Version.V1_12, 0x0C), map(Version.V1_12_1, Version.V1_12_2, 0x0B),
                map(Version.V1_13, Version.V1_13_2, 0x0E), map(Version.V1_14, Version.V1_15_2, 0x0F),
                map(Version.V1_16, Version.V1_16_4, 0x10), map(Version.V1_17, Version.V1_17_1, 0x0F)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInChat.class,
                map(Version.V1_8, 0x01), map(Version.V1_9, Version.V1_11_1, 0x02), map(Version.V1_12, 0x03), map(Version.V1_12_1, Version.V1_12_2, 0x02),
                map(Version.V1_13, Version.V1_13_2, 0x02), map(Version.V1_14, Version.V1_17_1, 0x03)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.class,
                map(Version.V1_8, 0x03), map(Version.V1_9, Version.V1_11_1, 0x0F), map(Version.V1_12, 0x0D), map(Version.V1_12_1, Version.V1_12_2, 0x0C),
                map(Version.V1_13, Version.V1_13_2, 0x0F), map(Version.V1_14, Version.V1_15_2, 0x14), map(Version.V1_16, Version.V1_16_4, 0x15), map(Version.V1_17, Version.V1_17_1, 0x14)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInPosition.class,
                map(Version.V1_8, 0x04), map(Version.V1_9, Version.V1_11_1, 0x0C), map(Version.V1_12, 0x0E), map(Version.V1_12_1, Version.V1_12_2, 0x0D),
                map(Version.V1_13, Version.V1_13_2, 0x10), map(Version.V1_14, Version.V1_15_2, 0x11), map(Version.V1_16, Version.V1_16_4, 0x12), map(Version.V1_17, Version.V1_17_1, 0x11)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInLook.class,
                map(Version.V1_8, 0x05), map(Version.V1_9, Version.V1_11_1, 0x0E), map(Version.V1_12, 0x10), map(Version.V1_12_1, Version.V1_12_2, 0x0F),
                map(Version.V1_13, Version.V1_13_2, 0x12), map(Version.V1_14, Version.V1_15_2, 0x13), map(Version.V1_16, Version.V1_16_4, 0x14), map(Version.V1_17, Version.V1_17_1, 0x13)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInPositionLook.class,
                map(Version.V1_8, 0x06), map(Version.V1_9, Version.V1_11_1, 0x0D), map(Version.V1_12, 0x0F), map(Version.V1_12_1, Version.V1_12_2, 0x0E),
                map(Version.V1_13, Version.V1_13_2, 0x11), map(Version.V1_14, Version.V1_15_2, 0x12), map(Version.V1_16, Version.V1_16_4, 0x13), map(Version.V1_17, Version.V1_17_1, 0x12)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInBlockDig.class,
                map(Version.V1_8, 0x07), map(Version.V1_9, Version.V1_11_1, 0x13), map(Version.V1_12, Version.V1_12_2, 0x14),
                map(Version.V1_13, Version.V1_13_2, 0x18), map(Version.V1_14, Version.V1_15_2, 0x1A), map(Version.V1_16, Version.V1_16_4, 0x1B), map(Version.V1_17, Version.V1_17_1, 0x1A)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInUseItem.class,
                map(Version.V1_9, Version.V1_11_1, 0x1D), map(Version.V1_12, Version.V1_12_2, 0x20),
                map(Version.V1_13, Version.V1_13_2, 0x2A), map(Version.V1_14, Version.V1_15_2, 0x2D), map(Version.V1_16, Version.V1_16_1, 0x2E), map(Version.V1_16_2, Version.V1_17_1, 0x2F)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInBlockPlace.class,
                map(Version.V1_8, 0x08), map(Version.V1_9, Version.V1_11_1, 0x1C), map(Version.V1_12, Version.V1_12_2, 0x1F),
                map(Version.V1_13, Version.V1_13_2, 0x29), map(Version.V1_14, Version.V1_15_2, 0x2C), map(Version.V1_16, Version.V1_16_1, 0x2D), map(Version.V1_16_2, Version.V1_17_1, 0x2E)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInHeldItemSlot.class,
                map(Version.V1_8, 0x09), map(Version.V1_9, Version.V1_11_1, 0x17), map(Version.V1_12, Version.V1_12_2, 0x1A),
                map(Version.V1_13, Version.V1_13_2, 0x21), map(Version.V1_14, Version.V1_15_2, 0x23), map(Version.V1_16, Version.V1_16_1, 0x24), map(Version.V1_16_2, Version.V1_17_1, 0x25)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInArmAnimation.class,
                map(Version.V1_8, 0x0A), map(Version.V1_9, Version.V1_11_1, 0x1A), map(Version.V1_12, Version.V1_12_2, 0x1D),
                map(Version.V1_13, Version.V1_13_2, 0x27), map(Version.V1_14, Version.V1_15_2, 0x2A), map(Version.V1_16, Version.V1_16_1, 0x2B), map(Version.V1_16_2, Version.V1_17_1, 0x2C)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInEntityAction.class,
                map(Version.V1_8, 0x0B), map(Version.V1_9, Version.V1_11_1, 0x14), map(Version.V1_12, Version.V1_12_2, 0x15),
                map(Version.V1_13, Version.V1_13_2, 0x19), map(Version.V1_14, Version.V1_15_2, 0x1B), map(Version.V1_16, Version.V1_16_4, 0x1C), map(Version.V1_17, Version.V1_17_1, 0x1B)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInAbilities.class,
                map(Version.V1_8, 0x13), map(Version.V1_9, Version.V1_11_1, 0x12), map(Version.V1_12, Version.V1_12_2, 0x13),
                map(Version.V1_13, Version.V1_13_2, 0x17), map(Version.V1_14, Version.V1_15_2, 0x19), map(Version.V1_16, Version.V1_16_4, 0x1A), map(Version.V1_17, Version.V1_17_1, 0x19)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInCloseWindow.class,
                map(Version.V1_8, 0x0D), map(Version.V1_9, Version.V1_11_1, 0x08), map(Version.V1_12, 0x09), map(Version.V1_12_1, Version.V1_12_2, 0x08),
                map(Version.V1_13, Version.V1_13_2, 0x09), map(Version.V1_14, Version.V1_16_4, 0x0A), map(Version.V1_17, Version.V1_17_1, 0x09)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInWindowClick.class,
                map(Version.V1_8, 0x0E), map(Version.V1_9, Version.V1_11_1, 0x07), map(Version.V1_12, 0x08), map(Version.V1_12_1, Version.V1_12_2, 0x07),
                map(Version.V1_13, Version.V1_13_2, 0x08), map(Version.V1_14, Version.V1_16_4, 0x09), map(Version.V1_17, Version.V1_17_1, 0x08)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInTransaction.class,
                map(Version.V1_8, 0x0F), map(Version.V1_9, Version.V1_11_1, 0x05), map(Version.V1_12, 0x06), map(Version.V1_12_1, Version.V1_12_2, 0x05),
                map(Version.V1_13, Version.V1_13_2, 0x06), map(Version.V1_14, Version.V1_16_4, 0x07)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInSetCreativeSlot.class,
                map(Version.V1_8, 0x10), map(Version.V1_9, Version.V1_11_1, 0x18), map(Version.V1_12, Version.V1_12_2, 0x1B),
                map(Version.V1_13, Version.V1_13_2, 0x24), map(Version.V1_14, Version.V1_15_2, 0x26), map(Version.V1_16, Version.V1_16_1, 0x27), map(Version.V1_16_2, Version.V1_17_1, 0x28)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInTabComplete.class,
                map(Version.V1_8, 0x14), map(Version.V1_9, Version.V1_11_1, 0x01), map(Version.V1_12, 0x02), map(Version.V1_12_1, Version.V1_12_2, 0x01),
                map(Version.V1_13, Version.V1_13_2, 0x05), map(Version.V1_14, Version.V1_17_1, 0x06)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInSettings.class,
                map(Version.V1_8, 0x15), map(Version.V1_9, Version.V1_11_1, 0x04), map(Version.V1_12, 0x05), map(Version.V1_12_1, Version.V1_12_2, 0x04),
                map(Version.V1_13, Version.V1_13_2, 0x04), map(Version.V1_14, Version.V1_17_1, 0x05)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInClientCommand.class,
                map(Version.V1_8, 0x16), map(Version.V1_9, Version.V1_11_1, 0x03), map(Version.V1_12, 0x04), map(Version.V1_12_1, Version.V1_12_2, 0x03),
                map(Version.V1_13, Version.V1_13_2, 0x03), map(Version.V1_14, Version.V1_17_1, 0x04)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInCustomPayload.class,
                map(Version.V1_8, 0x17), map(Version.V1_9, Version.V1_11_1, 0x09), map(Version.V1_12, 0x0A), map(Version.V1_12_1, Version.V1_12_2, 0x09),
                map(Version.V1_13, Version.V1_13_2, 0x0A), map(Version.V1_14, Version.V1_16_4, 0x0B), map(Version.V1_17, Version.V1_17_1, 0x0A)
        );

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutKeepAlive.class,
                map(Version.V1_8, 0x00), map(Version.V1_9, Version.V1_12_2, 0x1F), map(Version.V1_13, Version.V1_13_2, 0x21),
                map(Version.V1_14, Version.V1_14_4, 0x20), map(Version.V1_15, Version.V1_15_2, 0x21), map(Version.V1_16, Version.V1_16_1, 0x20),
                map(Version.V1_16_2, Version.V1_16_4, 0x1F), map(Version.V1_17, Version.V1_17_1, 0x21)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutLogin.class,
                map(Version.V1_8, 0x01), map(Version.V1_9, Version.V1_12_2, 0x23), map(Version.V1_13, Version.V1_13_2, 0x25),
                map(Version.V1_14, Version.V1_14_4, 0x25), map(Version.V1_15, Version.V1_15_2, 0x26), map(Version.V1_16, Version.V1_16_1, 0x25),
                map(Version.V1_16_2, Version.V1_16_4, 0x24), map(Version.V1_17, Version.V1_17_1, 0x26)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutChat.class,
                map(Version.V1_8, 0x02), map(Version.V1_9, Version.V1_12_2, 0x0F), map(Version.V1_13, Version.V1_13_2, 0x0E),
                map(Version.V1_14, Version.V1_14_4, 0x0E), map(Version.V1_15, Version.V1_15_2, 0x0F), map(Version.V1_16, Version.V1_16_4, 0x0E),
                map(Version.V1_17, Version.V1_17_1, 0x0F)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutSpawnPosition.class,
                map(Version.V1_8, 0x05), map(Version.V1_9, Version.V1_11_1, 0x43), map(Version.V1_12, 0x45), map(Version.V1_12_1, Version.V1_12_2, 0x46),
                map(Version.V1_13, Version.V1_13_2, 0x49), map(Version.V1_14, Version.V1_14_4, 0x4D), map(Version.V1_15, Version.V1_15_2, 0x4E),
                map(Version.V1_16, Version.V1_16_4, 0x42), map(Version.V1_17, Version.V1_17_1, 0x4B)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutPosition.class,
                map(Version.V1_8, 0x08), map(Version.V1_9, Version.V1_12, 0x2E), map(Version.V1_12_1, Version.V1_12_2, 0x2F),
                map(Version.V1_13, Version.V1_13_2, 0x32), map(Version.V1_14, Version.V1_14_4, 0x35), map(Version.V1_15, Version.V1_15_2, 0x36),
                map(Version.V1_16, Version.V1_16_1, 0x35), map(Version.V1_16_2, Version.V1_16_4, 0x34), map(Version.V1_17, Version.V1_17_1, 0x38)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutBlockChange.class,
                map(Version.V1_8, 0x23), map(Version.V1_9, Version.V1_14_4, 0x0B), map(Version.V1_15, Version.V1_15_2, 0x0C),
                map(Version.V1_16, Version.V1_16_4, 0x0B), map(Version.V1_17, Version.V1_17_1, 0x0C)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutChunkData.class,
                map(Version.V1_8, 0x21), map(Version.V1_9, Version.V1_12_2, 0x20), map(Version.V1_13, Version.V1_13_2, 0x22),
                map(Version.V1_14, Version.V1_14_4, 0x21), map(Version.V1_15, Version.V1_15_2, 0x22), map(Version.V1_16, Version.V1_16_1, 0x21),
                map(Version.V1_16_2, Version.V1_16_4, 0x20), map(Version.V1_17, Version.V1_17_1, 0x22)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutUpdateLight.class,
                map(Version.V1_14, Version.V1_14_4, 0x24), map(Version.V1_15, Version.V1_15_2, 0x25),
                map(Version.V1_16, Version.V1_16_1, 0x24), map(Version.V1_16_2, Version.V1_16_4, 0x23), map(Version.V1_17, Version.V1_17_1, 0x25)
        );

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutSetSlot.class,
                map(Version.V1_8, 0x2F), map(Version.V1_9, Version.V1_12_2, 0x16), map(Version.V1_13, Version.V1_13_2, 0x17),
                map(Version.V1_14, Version.V1_14_4, 0x16), map(Version.V1_15, Version.V1_15_2, 0x17), map(Version.V1_16, Version.V1_16_1, 0x16),
                map(Version.V1_16_2, Version.V1_16_4, 0x15), map(Version.V1_17, Version.V1_17_1, 0x16)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutMap.class,
                map(Version.V1_8, 0x34), map(Version.V1_9, Version.V1_12_2, 0x24), map(Version.V1_13, Version.V1_13_2, 0x26),
                map(Version.V1_14, Version.V1_14_4, 0x26), map(Version.V1_15, Version.V1_15_2, 0x27), map(Version.V1_16, Version.V1_16_1, 0x26),
                map(Version.V1_16_2, Version.V1_16_4, 0x25), map(Version.V1_17, Version.V1_17_1, 0x27)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutAbilities.class,
                map(Version.V1_8, 0x39), map(Version.V1_9, Version.V1_12, 0x2B), map(Version.V1_12_1, Version.V1_12_2, 0x2C),
                map(Version.V1_13, Version.V1_13_2, 0x2E), map(Version.V1_14, Version.V1_14_4, 0x31), map(Version.V1_15, Version.V1_15_2, 0x32),
                map(Version.V1_16, Version.V1_16_1, 0x31), map(Version.V1_16_2, Version.V1_16_4, 0x30), map(Version.V1_17, Version.V1_17_1, 0x32)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutKickDisconnect.class,
                map(Version.V1_8, 0x40), map(Version.V1_9, Version.V1_12_2, 0x1A), map(Version.V1_13, Version.V1_13_2, 0x1B),
                map(Version.V1_14, Version.V1_14_4, 0x1A), map(Version.V1_15, Version.V1_15_2, 0x1B), map(Version.V1_16, Version.V1_16_1, 0x1A),
                map(Version.V1_16_2, Version.V1_16_4, 0x19), map(Version.V1_17, Version.V1_17_1, 0x1A)
        );

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutTitle.class,
                map(Version.V1_8, Version.V1_11_1, 0x45), map(Version.V1_12, 0x47), map(Version.V1_12_1, Version.V1_12_2, 0x48),
                map(Version.V1_13, Version.V1_13_2, 0x4B), map(Version.V1_14, Version.V1_14_4, 0x4F), map(Version.V1_15, Version.V1_15_2, 0x50),
                map(Version.V1_16, Version.V1_16_4, 0x4F), map(Version.V1_17, Version.V1_17_1, 0x59)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutPlayerListHeaderFooter.class,
                map(Version.V1_8, 0x47), map(Version.V1_9, Version.V1_9_2, 0x48), map(Version.V1_9_3, Version.V1_11_1, 0x47), map(Version.V1_12, 0x49), map(Version.V1_12_1, Version.V1_12_2, 0x4A),
                map(Version.V1_13, Version.V1_13_2, 0x4E), map(Version.V1_14, Version.V1_14_4, 0x53), map(Version.V1_15, Version.V1_15_2, 0x54),
                map(Version.V1_16, Version.V1_16_4, 0x53), map(Version.V1_17, Version.V1_17_1, 0x5E)
        );

        registerModernPackets(server);
    }

    private void registerModernPackets(final LightSpigotServer server) {
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.LOGIN, PacketLoginInAcknowledged.class,
                map(Version.V1_20_2, Version.V26_2, 0x03));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.CONFIGURATION, PacketConfigurationInIgnored.class,
                map(Version.V1_20_2, Version.V1_20_3, 0x00), map(Version.V1_20_2, Version.V1_20_3, 0x01),
                map(Version.V1_20_5, Version.V26_2, 0x00), map(Version.V1_20_5, Version.V26_2, 0x02));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.CONFIGURATION, PacketConfigurationInFinish.class,
                map(Version.V1_20_2, Version.V1_20_3, 0x02), map(Version.V1_20_5, Version.V26_2, 0x03));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.CONFIGURATION, PacketConfigurationOutRegistryData.class,
                map(Version.V1_20_2, Version.V1_20_3, 0x05), map(Version.V1_20_5, Version.V26_2, 0x07));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.CONFIGURATION, PacketConfigurationOutFeatureFlags.class,
                map(Version.V1_20_2, 0x07), map(Version.V1_20_3, 0x08),
                map(Version.V1_20_5, Version.V26_2, 0x0C));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.CONFIGURATION, PacketConfigurationOutTags.class,
                map(Version.V1_20_2, 0x08), map(Version.V1_20_3, 0x09),
                map(Version.V1_20_5, Version.V26_2, 0x0D));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.CONFIGURATION, PacketConfigurationOutFinish.class,
                map(Version.V1_20_2, Version.V1_20_3, 0x02), map(Version.V1_20_5, Version.V26_2, 0x03));

        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInTeleportAccept.class, modern(
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInKeepAlive.class, modern(
                0x0F, 0x0F, 0x11, 0x12, 0x11, 0x12, 0x12, 0x14, 0x15, 0x18, 0x18, 0x1A, 0x1A, 0x1A, 0x1B, 0x1B, 0x1B, 0x1B, 0x1C, 0x1C));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInChat.class, modern(
                0x03, 0x03, 0x04, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x06, 0x06, 0x07, 0x07, 0x07, 0x08, 0x08, 0x08, 0x08, 0x09, 0x09));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInCommand.class,
                map(Version.V1_19, 0x03), map(Version.V1_19_1, Version.V1_20_3, 0x04),
                map(Version.V1_20_5, Version.V1_21, 0x04), map(Version.V1_21_2, Version.V1_21_5, 0x05),
                map(Version.V1_21_6, Version.V1_21_11, 0x06), map(Version.V26_1, Version.V26_2, 0x07));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInPosition.class, modern(
                0x11, 0x11, 0x13, 0x14, 0x13, 0x14, 0x14, 0x16, 0x17, 0x1A, 0x1A, 0x1C, 0x1C, 0x1C, 0x1D, 0x1D, 0x1D, 0x1D, 0x1E, 0x1E));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInPositionLook.class, modern(
                0x12, 0x12, 0x14, 0x15, 0x14, 0x15, 0x15, 0x17, 0x18, 0x1B, 0x1B, 0x1D, 0x1D, 0x1D, 0x1E, 0x1E, 0x1E, 0x1E, 0x1F, 0x1F));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInLook.class, modern(
                0x13, 0x13, 0x15, 0x16, 0x15, 0x16, 0x16, 0x18, 0x19, 0x1C, 0x1C, 0x1E, 0x1E, 0x1E, 0x1F, 0x1F, 0x1F, 0x1F, 0x20, 0x20));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.class, modern(
                0x14, 0x14, 0x16, 0x17, 0x16, 0x17, 0x17, 0x19, 0x1A, 0x1D, 0x1D, 0x1F, 0x1F, 0x1F, 0x20, 0x20, 0x20, 0x20, 0x21, 0x21));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInAbilities.class, modern(
                0x19, 0x19, 0x1B, 0x1C, 0x1B, 0x1C, 0x1C, 0x1F, 0x20, 0x23, 0x23, 0x25, 0x26, 0x26, 0x27, 0x27, 0x27, 0x27, 0x28, 0x28));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInBlockDig.class, modern(
                0x1A, 0x1A, 0x1C, 0x1D, 0x1C, 0x1D, 0x1D, 0x20, 0x21, 0x24, 0x24, 0x26, 0x27, 0x27, 0x28, 0x28, 0x28, 0x28, 0x29, 0x29));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInEntityAction.class, modern(
                0x1B, 0x1B, 0x1D, 0x1E, 0x1D, 0x1E, 0x1E, 0x21, 0x22, 0x25, 0x25, 0x27, 0x28, 0x28, 0x29, 0x29, 0x29, 0x29, 0x2A, 0x2A));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInHeldItemSlot.class, modern(
                0x25, 0x25, 0x27, 0x28, 0x28, 0x28, 0x28, 0x2B, 0x2C, 0x2F, 0x2F, 0x31, 0x33, 0x33, 0x34, 0x34, 0x34, 0x34, 0x35, 0x35));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInSetCreativeSlot.class, modern(
                0x28, 0x28, 0x2A, 0x2B, 0x2B, 0x2B, 0x2B, 0x2E, 0x2F, 0x32, 0x32, 0x34, 0x36, 0x36, 0x37, 0x37, 0x37, 0x37, 0x38, 0x38));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInArmAnimation.class, modern(
                0x2C, 0x2C, 0x2E, 0x2F, 0x2F, 0x2F, 0x2F, 0x32, 0x33, 0x36, 0x36, 0x38, 0x3A, 0x3B, 0x3C, 0x3C, 0x3C, 0x3C, 0x3F, 0x3E));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInBlockPlace.class, modern(
                0x2E, 0x2E, 0x30, 0x31, 0x31, 0x31, 0x31, 0x34, 0x35, 0x38, 0x38, 0x3A, 0x3C, 0x3E, 0x3F, 0x3F, 0x3F, 0x3F, 0x42, 0x41));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInUseItem.class, modern(
                0x2F, 0x2F, 0x31, 0x32, 0x32, 0x32, 0x32, 0x35, 0x36, 0x39, 0x39, 0x3B, 0x3D, 0x3F, 0x40, 0x40, 0x40, 0x40, 0x43, 0x42));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInWindowClick.class, modern(
                0x08, 0x08, 0x0A, 0x0B, 0x0A, 0x0B, 0x0B, 0x0D, 0x0D, 0x0E, 0x0E, 0x10, 0x10, 0x10, 0x11, 0x11, 0x11, 0x11, 0x12, 0x12));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInCloseWindow.class, modern(
                0x09, 0x09, 0x0B, 0x0C, 0x0B, 0x0C, 0x0C, 0x0E, 0x0E, 0x0F, 0x0F, 0x11, 0x11, 0x11, 0x12, 0x12, 0x12, 0x12, 0x13, 0x13));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInCustomPayload.class, modern(
                0x0A, 0x0A, 0x0C, 0x0D, 0x0C, 0x0D, 0x0D, 0x0F, 0x10, 0x12, 0x12, 0x14, 0x14, 0x14, 0x15, 0x15, 0x15, 0x15, 0x16, 0x16));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInClientCommand.class, modern(
                0x04, 0x04, 0x06, 0x07, 0x06, 0x07, 0x07, 0x08, 0x08, 0x09, 0x09, 0x0A, 0x0A, 0x0A, 0x0B, 0x0B, 0x0B, 0x0B, 0x0C, 0x0C));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInSettings.class, modern(
                0x05, 0x05, 0x07, 0x08, 0x07, 0x08, 0x08, 0x09, 0x09, 0x0A, 0x0A, 0x0C, 0x0C, 0x0C, 0x0D, 0x0D, 0x0D, 0x0D, 0x0E, 0x0E));
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInTabComplete.class, modern(
                0x06, 0x06, 0x08, 0x09, 0x08, 0x09, 0x09, 0x0A, 0x0A, 0x0B, 0x0B, 0x0D, 0x0D, 0x0D, 0x0E, 0x0E, 0x0E, 0x0E, 0x0F, 0x0F));

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutBlockChange.class, modern(
                0x0C, 0x0C, 0x09, 0x09, 0x09, 0x0A, 0x0A, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutChunkBatchFinish.class,
                map(Version.V1_20_2, Version.V1_21_4, 0x0C),
                map(Version.V1_21_5, Version.V26_2, 0x0B));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutChunkBatchStart.class,
                map(Version.V1_20_2, Version.V1_21_4, 0x0D),
                map(Version.V1_21_5, Version.V26_2, 0x0C));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutGameEvent.class,
                map(Version.V1_20_2, Version.V1_20_3, 0x20),
                map(Version.V1_20_5, Version.V1_21, 0x22),
                map(Version.V1_21_2, Version.V1_21_4, 0x23),
                map(Version.V1_21_5, Version.V1_21_7, 0x22),
                map(Version.V1_21_9, Version.V26_2, 0x26));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutUpdateViewPosition.class, modern(
                0x49, 0x49, 0x48, 0x4B, 0x4A, 0x4E, 0x4E, 0x50, 0x52, 0x54, 0x54, 0x58, 0x58, 0x57, 0x57, 0x57, 0x5C, 0x5C, 0x5E, 0x5E));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutChat.class, modern(
                0x0F, 0x0F, 0x5F, 0x62, 0x60, 0x64, 0x64, 0x67, 0x69, 0x6C, 0x6C, 0x73, 0x73, 0x72, 0x72, 0x72, 0x77, 0x77, 0x79, 0x79));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutSetSlot.class, modern(
                0x16, 0x16, 0x13, 0x13, 0x12, 0x14, 0x14, 0x15, 0x15, 0x15, 0x15, 0x15, 0x15, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutKickDisconnect.class, modern(
                0x1A, 0x1A, 0x17, 0x19, 0x17, 0x1A, 0x1A, 0x1B, 0x1B, 0x1D, 0x1D, 0x1D, 0x1D, 0x1C, 0x1C, 0x1C, 0x20, 0x20, 0x20, 0x20));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutKeepAlive.class, modern(
                0x21, 0x21, 0x1E, 0x20, 0x1F, 0x23, 0x23, 0x24, 0x24, 0x26, 0x26, 0x27, 0x27, 0x26, 0x26, 0x26, 0x2B, 0x2B, 0x2C, 0x2C));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutChunkData.class, modern(
                0x22, 0x22, 0x1F, 0x21, 0x20, 0x24, 0x24, 0x25, 0x25, 0x27, 0x27, 0x28, 0x28, 0x27, 0x27, 0x27, 0x2C, 0x2C, 0x2D, 0x2D));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutUpdateLight.class, modern(
                0x25, 0x25, 0x22, 0x24, 0x23, 0x27, 0x27, 0x28, 0x28, 0x2A, 0x2A, 0x2B, 0x2B, 0x2A, 0x2A, 0x2A, 0x2F, 0x2F, 0x30, 0x30));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutLogin.class, modern(
                0x26, 0x26, 0x23, 0x25, 0x24, 0x28, 0x28, 0x29, 0x29, 0x2B, 0x2B, 0x2C, 0x2C, 0x2B, 0x2B, 0x2B, 0x30, 0x30, 0x31, 0x31));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutMap.class, modern(
                0x27, 0x27, 0x24, 0x26, 0x25, 0x29, 0x29, 0x2A, 0x2A, 0x2C, 0x2C, 0x2D, 0x2D, 0x2C, 0x2C, 0x2C, 0x31, 0x31, 0x33, 0x33));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutAbilities.class, modern(
                0x32, 0x32, 0x2F, 0x31, 0x30, 0x34, 0x34, 0x36, 0x36, 0x38, 0x38, 0x3A, 0x3A, 0x39, 0x39, 0x39, 0x3E, 0x3E, 0x40, 0x40));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutPosition.class, modern(
                0x38, 0x38, 0x36, 0x39, 0x38, 0x3C, 0x3C, 0x3E, 0x3E, 0x40, 0x40, 0x42, 0x42, 0x41, 0x41, 0x41, 0x46, 0x46, 0x48, 0x48));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutSpawnPosition.class, modern(
                0x4B, 0x4B, 0x4A, 0x4D, 0x4C, 0x50, 0x50, 0x52, 0x54, 0x56, 0x56, 0x5B, 0x5B, 0x5A, 0x5A, 0x5A, 0x5F, 0x5F, 0x61, 0x61));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutTitle.class, modern(
                0x5A, 0x5A, 0x5A, 0x5D, 0x5B, 0x5F, 0x5F, 0x61, 0x63, 0x65, 0x65, 0x6C, 0x6C, 0x6B, 0x6B, 0x6B, 0x70, 0x70, 0x72, 0x72));
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutPlayerListHeaderFooter.class, modern(
                0x5F, 0x5F, 0x60, 0x63, 0x61, 0x65, 0x65, 0x68, 0x6A, 0x6D, 0x6D, 0x74, 0x74, 0x73, 0x73, 0x73, 0x78, 0x78, 0x7A, 0x7A));

        final Version[] modernVersions = Version.getVersionsRange(Version.V1_18, Version.V26_2);
        final int[] maximumServerboundIds = {
                0x2F, 0x2F, 0x31, 0x32, 0x32, 0x32, 0x32, 0x35, 0x36, 0x39,
                0x39, 0x3B, 0x3D, 0x3F, 0x41, 0x41, 0x41, 0x41, 0x44, 0x43
        };
        for (int versionIndex = 0; versionIndex < modernVersions.length; versionIndex++) {
            final Version version = modernVersions[versionIndex];
            for (int packetId = 0; packetId <= maximumServerboundIds[versionIndex]; packetId++) {
                if (!containsPacketId(PacketDirection.SERVERBOUND, version, ConnectionState.PLAY, packetId)) {
                    registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY,
                            version, packetId, PacketPlayInIgnored.class);
                }
            }
        }
    }

    private void registerPacket(final LightSpigotServer server, final PacketDirection direction, final ConnectionState connectionState, final Class<? extends Packet> packet, final PacketMapping... mappings) {
        for (PacketMapping packetMapping : mappings) {
            for (Version version : packetMapping.versions()) {
                registerPacket(server, direction, connectionState, version, packetMapping.packetId(), packet);
            }
        }
    }

    /**
     * Registers a new packet with the specified direction, connection state, packet ID, and packet class.
     *
     * @param server          The server that will be using the packet.
     * @param direction       The direction of the packet (IN or OUT).
     * @param connectionState The connection state when the packet is used.
     * @param packetId        The ID of the packet.
     * @param packet          The class of the packet.
     */
    private void registerPacket(final LightSpigotServer server, final PacketDirection direction, final ConnectionState connectionState, final Version version, final int packetId, final Class<? extends Packet> packet) {
        if (containsPacketId(direction, version, connectionState, packetId)) {
            server.getLogger().error("PacketManager.registerPacket(...) error", "PacketID is already in use (" + packetId + ", " + packet.getSimpleName() + ", " + version + ")");
            return;
        }
        try {
            final RegisteredPacket registeredPacket = new RegisteredPacket(packet);
            packets.computeIfAbsent(direction, k -> new EnumMap<>(Version.class))
                    .computeIfAbsent(version, k -> new EnumMap<>(ConnectionState.class))
                    .computeIfAbsent(connectionState, k -> new HashMap<>())
                    .put(packetId, registeredPacket);

            packetClassToId
                    .computeIfAbsent(direction, k -> new EnumMap<>(Version.class))
                    .computeIfAbsent(version, k -> new HashMap<>())
                    .put(packet, packetId);
        } catch (final NoSuchMethodException e) {
            server.getLogger().error("PacketManager.registerPacket(...) error", "Failed to register packet (" + packetId + ", " + packet.getSimpleName() + ", " + version + ")\n" + e.getMessage());
        }
    }

    /**
     * Returns the ID of the specified packet.
     *
     * @param direction   The direction of the packet (IN or OUT).
     * @param packetClass The class of the packet.
     * @return The ID of the packet.
     */
    public final int getPacketId(final PacketDirection direction, final Version version, final Class<? extends Packet> packetClass) {
        return packetClassToId.getOrDefault(direction, Map.of()).getOrDefault(version, Map.of()).getOrDefault(packetClass, -1);
    }

    /**
     * Constructs a new instance of the specified packet.
     *
     * @param direction       The direction of the packet (IN or OUT).
     * @param connectionState The connection state when the packet is used.
     * @param packetId        The ID of the packet.
     * @return The new instance of the packet.
     * @throws InvocationTargetException If the underlying constructor throws an exception.
     * @throws InstantiationException    If the class that declares the underlying constructor represents an abstract class.
     * @throws IllegalAccessException    If the underlying constructor is inaccessible.
     */
    public final Packet constructPacket(final PacketDirection direction, final Version version, final ConnectionState connectionState, final int packetId) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return packets.get(direction).get(version).get(connectionState).get(packetId).getConstructor().newInstance();
    }

    /**
     * Checks if a packet ID exists for the specified direction and connection state.
     *
     * @param direction       The direction of the packet (IN or OUT).
     * @param connectionState The connection state when the packet is used.
     * @param id              The ID of the packet.
     * @return True if the packet ID exists, false otherwise.
     */
    public final boolean containsPacketId(final PacketDirection direction, final Version version, final ConnectionState connectionState, final int id) {
        if (packets.containsKey(direction)) {
            if (packets.get(direction).containsKey(version)) {
                if (packets.get(direction).get(version).containsKey(connectionState)) {
                    return packets.get(direction).get(version).get(connectionState).containsKey(id);
                }
            }
        }
        return false;
    }

    record PacketMapping(Version[] versions, int packetId) {

        private PacketMapping(final Version versionMin, final Version versionMax, final int packetId) {
            this(Version.getVersionsRange(versionMin, versionMax), packetId);
        }

        private PacketMapping(final Version version, final int packetId) {
            this(version, version, packetId);
        }

    }

}
