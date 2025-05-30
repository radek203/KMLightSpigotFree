package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.PacketDirection;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.PacketHandshake;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.PacketEncryptionResponse;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.PacketLoginStart;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.play.*;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.status.PacketPing;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.status.PacketServerQuery;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketDisconnect;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketEnableCompression;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketEncryptionRequest;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketLoginSuccess;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.*;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.status.PacketPong;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.status.PacketServerInfo;

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
     * clientBound - OUT
     * serverBound - IN
     *
     * @param server The server that will be using the packets.
     */
    public final void registerPackets(final LightSpigotServer server) {
        registerPacket(server, PacketDirection.IN, ConnectionState.HANDSHAKING, PacketHandshake.class, map(0x00));

        registerPacket(server, PacketDirection.IN, ConnectionState.STATUS, PacketServerQuery.class, map(0x00));
        registerPacket(server, PacketDirection.IN, ConnectionState.STATUS, PacketPing.class, map(0x01));

        registerPacket(server, PacketDirection.OUT, ConnectionState.STATUS, PacketServerInfo.class, map(0x00));
        registerPacket(server, PacketDirection.OUT, ConnectionState.STATUS, PacketPong.class, map(0x01));

        registerPacket(server, PacketDirection.IN, ConnectionState.LOGIN, PacketLoginStart.class, map(0x00));
        registerPacket(server, PacketDirection.IN, ConnectionState.LOGIN, PacketEncryptionResponse.class, map(0x01));

        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, PacketDisconnect.class, map(0x00));
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, PacketEncryptionRequest.class, map(0x01));
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, PacketLoginSuccess.class, map(0x02));
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, PacketEnableCompression.class, map(0x03));

        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketTeleportAccept.class, map(Version.V1_12_2, 0x00));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketKeepAliveIn.class, map(Version.V1_8, 0x00), map(Version.V1_12_2, 0x0B));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketChatMessage.class, map(Version.V1_8, 0x01), map(Version.V1_12_2, 0x02));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketPlayer.class, map(Version.V1_8, 0x03), map(Version.V1_12_2, 0x0C));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketPlayer.PacketPlayerPosition.class, map(Version.V1_8, 0x04), map(Version.V1_12_2, 0x0D));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketPlayer.PacketPlayerLook.class, map(Version.V1_8, 0x05), map(Version.V1_12_2, 0x0E));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketPlayer.PacketPlayerPosLook.class, map(Version.V1_8, 0x06), map(Version.V1_12_2, 0x0F));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketPlayerDigging.class, map(Version.V1_8, 0x07), map(Version.V1_12_2, 0x14));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketPlayerBlockPlacement.class, map(Version.V1_8, 0x08), map(Version.V1_12_2, 0x20));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketHeldItemChange.class, map(Version.V1_8, 0x09), map(Version.V1_12_2, 0x1A));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketAnimation.class, map(Version.V1_8, 0x0A), map(Version.V1_12_2, 0x1D));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketEntityAction.class, map(Version.V1_8, 0x0B), map(Version.V1_12_2, 0x15));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketCloseWindow.class, map(Version.V1_8, 0x0D), map(Version.V1_12_2, 0x08));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketClickWindow.class, map(Version.V1_8, 0x0E), map(Version.V1_12_2, 0x07));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketConfirmTransaction.class, map(Version.V1_8, 0x0F), map(Version.V1_12_2, 0x05));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketCreativeInventoryAction.class, map(Version.V1_8, 0x10), map(Version.V1_12_2, 0x1B));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketTabComplete.class, map(Version.V1_8, 0x14), map(Version.V1_12_2, 0x01));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketClientSettings.class, map(Version.V1_8, 0x15), map(Version.V1_12_2, 0x04));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketClientStatus.class, map(Version.V1_8, 0x16), map(Version.V1_12_2, 0x03));
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, PacketCustomPayload.class, map(Version.V1_8, 0x17), map(Version.V1_12_2, 0x09));

        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketKeepAliveOut.class, map(Version.V1_8, 0x00), map(Version.V1_12_2, 0x1F));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketJoinGame.class, map(Version.V1_8, 0x01), map(Version.V1_12_2, 0x23));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketChat.class, map(Version.V1_8, 0x02), map(Version.V1_12_2, 0x0F));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketPlayerPosLook.class, map(Version.V1_8, 0x08), map(Version.V1_12_2, 0x2F));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketPlaySetSlot.class, map(Version.V1_8, 0x2F), map(Version.V1_12_2, 0x16));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketPlayMap.class, map(Version.V1_8, 0x34), map(Version.V1_12_2, 0x24));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketPlayerAbilitiesOut.class, map(Version.V1_8, 0x39), map(Version.V1_12_2, 0x2C));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketKickDisconnect.class, map(Version.V1_8, 0x40), map(Version.V1_12_2, 0x1A));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketTitle.class, map(Version.V1_8, 0x45), map(Version.V1_12_2, 0x48));
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, PacketPlayerListHeaderFooter.class, map(Version.V1_8, 0x47), map(Version.V1_12_2, 0x4A));
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
            server.getLogger().error("PacketManager.registerPacket(...) error", "PacketID is already in use (" + packetId + ", " + packet.getName() + ", " + version + ")");
            return;
        }
        try {
            final RegisteredPacket registeredPacket = new RegisteredPacket(packet);
            packets.computeIfAbsent(direction, k -> new EnumMap<>(Version.class))
                    .computeIfAbsent(version, k -> new EnumMap<>(ConnectionState.class))
                    .computeIfAbsent(connectionState, k -> new HashMap<>())
                    .put(packetId, registeredPacket);
        } catch (final NoSuchMethodException e) {
            server.getLogger().error("PacketManager.registerPacket(...) error", "Failed to register packet (" + packetId + ")\n" + e.getMessage());
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
