package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.PacketDirection;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.PacketHandshakingInSetProtocol;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.PacketLoginInEncryptionBegin;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.PacketLoginInStart;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.*;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.status.PacketStatusInPing;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.status.PacketStatusInStart;
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

    private static PacketMapping map(final int packetId) {
        return new PacketMapping(Version.values(), packetId);
    }

    private static PacketMapping map(final Version version, final int packetId) {
        return new PacketMapping(version, packetId);
    }

    private static PacketMapping map(final Version versionMin, final Version versionMax, final int packetId) {
        return new PacketMapping(versionMin, versionMax, packetId);
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
                map(Version.V1_9, Version.V1_12_2, 0x00)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInKeepAlive.class,
                map(Version.V1_8, 0x00), map(Version.V1_9, Version.V1_11_1, 0x0B), map(Version.V1_12, 0x0C), map(Version.V1_12_1, Version.V1_12_2, 0x0B)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInChat.class,
                map(Version.V1_8, 0x01), map(Version.V1_9, Version.V1_11_1, 0x02), map(Version.V1_12, 0x03), map(Version.V1_12_1, Version.V1_12_2, 0x02)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.class,
                map(Version.V1_8, 0x03), map(Version.V1_9, Version.V1_11_1, 0x0F), map(Version.V1_12, 0x0D), map(Version.V1_12_1, Version.V1_12_2, 0x0C)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInPosition.class,
                map(Version.V1_8, 0x04), map(Version.V1_9, Version.V1_11_1, 0x0C), map(Version.V1_12, 0x0E), map(Version.V1_12_1, Version.V1_12_2, 0x0D)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInLook.class,
                map(Version.V1_8, 0x05), map(Version.V1_9, Version.V1_11_1, 0x0E), map(Version.V1_12, 0x10), map(Version.V1_12_1, 0x0F), map(Version.V1_12_2, 0x0E)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInFlying.PacketPlayInPositionLook.class,
                map(Version.V1_8, 0x06), map(Version.V1_9, Version.V1_11_1, 0x0D), map(Version.V1_12, 0x0F), map(Version.V1_12_1, 0x0E), map(Version.V1_12_2, 0x0F)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInBlockDig.class,
                map(Version.V1_8, 0x07), map(Version.V1_9, Version.V1_11_1, 0x13), map(Version.V1_12, Version.V1_12_2, 0x14)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInBlockPlace.class,
                map(Version.V1_8, 0x08), map(Version.V1_9, Version.V1_11_1, 0x1D), map(Version.V1_12, Version.V1_12_2, 0x20)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInHeldItemSlot.class,
                map(Version.V1_8, 0x09), map(Version.V1_9, Version.V1_11_1, 0x17), map(Version.V1_12, Version.V1_12_2, 0x1A)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInArmAnimation.class,
                map(Version.V1_8, 0x0A), map(Version.V1_9, Version.V1_11_1, 0x1A), map(Version.V1_12, Version.V1_12_2, 0x1D)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInEntityAction.class,
                map(Version.V1_8, 0x0B), map(Version.V1_9, Version.V1_11_1, 0x14), map(Version.V1_12, Version.V1_12_2, 0x15)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInCloseWindow.class,
                map(Version.V1_8, 0x0D), map(Version.V1_9, Version.V1_11_1, 0x08), map(Version.V1_12, 0x09), map(Version.V1_12_1, Version.V1_12_2, 0x08)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInWindowClick.class,
                map(Version.V1_8, 0x0E), map(Version.V1_9, Version.V1_11_1, 0x07), map(Version.V1_12, 0x08), map(Version.V1_12_1, Version.V1_12_2, 0x07)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInTransaction.class,
                map(Version.V1_8, 0x0F), map(Version.V1_9, Version.V1_11_1, 0x05), map(Version.V1_12, 0x06), map(Version.V1_12_1, Version.V1_12_2, 0x05)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInSetCreativeSlot.class,
                map(Version.V1_8, 0x10), map(Version.V1_9, Version.V1_11_1, 0x18), map(Version.V1_12, Version.V1_12_2, 0x1B)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInTabComplete.class,
                map(Version.V1_8, 0x14), map(Version.V1_9, Version.V1_11_1, 0x01), map(Version.V1_12, 0x02), map(Version.V1_12_1, Version.V1_12_2, 0x01)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInSettings.class,
                map(Version.V1_8, 0x15), map(Version.V1_9, Version.V1_11_1, 0x04), map(Version.V1_12, 0x05), map(Version.V1_12_1, Version.V1_12_2, 0x04)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInClientCommand.class,
                map(Version.V1_8, 0x16), map(Version.V1_9, Version.V1_11_1, 0x03), map(Version.V1_12, 0x04), map(Version.V1_12_1, Version.V1_12_2, 0x03)
        );
        registerPacket(server, PacketDirection.SERVERBOUND, ConnectionState.PLAY, PacketPlayInCustomPayload.class,
                map(Version.V1_8, 0x17), map(Version.V1_9, Version.V1_11_1, 0x09), map(Version.V1_12, 0x0A), map(Version.V1_12_1, Version.V1_12_2, 0x09)
        );

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutKeepAlive.class,
                map(Version.V1_8, 0x00), map(Version.V1_9, Version.V1_12_2, 0x1F)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutLogin.class,
                map(Version.V1_8, 0x01), map(Version.V1_9, Version.V1_12_2, 0x23)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutChat.class,
                map(Version.V1_8, 0x02), map(Version.V1_9, Version.V1_12_2, 0x0F)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutPosition.class,
                map(Version.V1_8, 0x08), map(Version.V1_9, Version.V1_12, 0x2E), map(Version.V1_12_1, Version.V1_12_2, 0x2F)
        );

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutSetSlot.class,
                map(Version.V1_8, 0x2F), map(Version.V1_9, Version.V1_11_1, 0x16), map(Version.V1_12, Version.V1_12_2, 0x16)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutMap.class,
                map(Version.V1_8, 0x34), map(Version.V1_9, Version.V1_11_1, 0x24), map(Version.V1_12, Version.V1_12_2, 0x24)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutAbilities.class,
                map(Version.V1_8, 0x39), map(Version.V1_9, Version.V1_12, 0x2B), map(Version.V1_12_1, Version.V1_12_2, 0x2C)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutKickDisconnect.class,
                map(Version.V1_8, 0x40), map(Version.V1_9, Version.V1_12_2, 0x1A)
        );

        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutTitle.class,
                map(Version.V1_8, Version.V1_11_1, 0x45), map(Version.V1_12, 0x47), map(Version.V1_12_1, Version.V1_12_2, 0x48)
        );
        registerPacket(server, PacketDirection.CLIENTBOUND, ConnectionState.PLAY, PacketPlayOutPlayerListHeaderFooter.class,
                map(Version.V1_8, 0x47), map(Version.V1_9, Version.V1_9_2, 0x48), map(Version.V1_9_3, Version.V1_11_1, 0x47), map(Version.V1_12, 0x49), map(Version.V1_12_1, Version.V1_12_2, 0x4A)
        );
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
        for (final Map.Entry<ConnectionState, Map<Integer, RegisteredPacket>> it : packets.get(direction).get(version).entrySet()) {
            for (final Map.Entry<Integer, RegisteredPacket> its : it.getValue().entrySet()) {
                if (its.getValue().getPacketClass().equals(packetClass)) {
                    return its.getKey();
                }
            }
        }
        return -1;
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
