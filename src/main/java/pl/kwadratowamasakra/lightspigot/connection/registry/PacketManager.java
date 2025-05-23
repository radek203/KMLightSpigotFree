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

    /**
     * Registers all the packets that will be used in the network communication.
     * clientBound - OUT
     * serverBound - IN
     *
     * @param server The server that will be using the packets.
     */
    public final void registerPackets(final LightSpigotServer server) {
        registerPacket(server, PacketDirection.IN, ConnectionState.HANDSHAKING, Map.of(Version.values(), 0x00), PacketHandshake.class);

        registerPacket(server, PacketDirection.IN, ConnectionState.STATUS, Map.of(Version.values(), 0x00), PacketServerQuery.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.STATUS, Map.of(Version.values(), 0x01), PacketPing.class);

        registerPacket(server, PacketDirection.OUT, ConnectionState.STATUS, Map.of(Version.values(), 0x00), PacketServerInfo.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.STATUS, Map.of(Version.values(), 0x01), PacketPong.class);

        registerPacket(server, PacketDirection.IN, ConnectionState.LOGIN, Map.of(Version.values(), 0x00), PacketLoginStart.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.LOGIN, Map.of(Version.values(), 0x01), PacketEncryptionResponse.class);

        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, Map.of(Version.values(), 0x00), PacketDisconnect.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, Map.of(Version.values(), 0x01), PacketEncryptionRequest.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, Map.of(Version.values(), 0x02), PacketLoginSuccess.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, Map.of(Version.values(), 0x03), PacketEnableCompression.class);

        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_12_2}, 0x00), PacketTeleportAccept.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x00, new Version[]{Version.V1_12_2}, 0x0B), PacketKeepAliveIn.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x01, new Version[]{Version.V1_12_2}, 0x02), PacketChatMessage.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x02, new Version[]{Version.V1_12_2}, 0x0A), PacketUseEntity.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x03, new Version[]{Version.V1_12_2}, 0x0C), PacketPlayer.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x04, new Version[]{Version.V1_12_2}, 0x0D), PacketPlayer.PacketPlayerPosition.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x05, new Version[]{Version.V1_12_2}, 0x0E), PacketPlayer.PacketPlayerLook.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x06, new Version[]{Version.V1_12_2}, 0x0F), PacketPlayer.PacketPlayerPosLook.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x07, new Version[]{Version.V1_12_2}, 0x14), PacketPlayerDigging.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x08, new Version[]{Version.V1_12_2}, 0x20), PacketPlayerBlockPlacement.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x09, new Version[]{Version.V1_12_2}, 0x1A), PacketHeldItemChange.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x0A, new Version[]{Version.V1_12_2}, 0x1D), PacketAnimation.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x0B, new Version[]{Version.V1_12_2}, 0x15), PacketEntityAction.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x0C, new Version[]{Version.V1_12_2}, 0x16), PacketInput.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x0D, new Version[]{Version.V1_12_2}, 0x08), PacketCloseWindow.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x0E, new Version[]{Version.V1_12_2}, 0x07), PacketClickWindow.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x0F, new Version[]{Version.V1_12_2}, 0x05), PacketConfirmTransaction.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x10, new Version[]{Version.V1_12_2}, 0x1B), PacketCreativeInventoryAction.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x11, new Version[]{Version.V1_12_2}, 0x06), PacketEnchantItem.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x12, new Version[]{Version.V1_12_2}, 0x1C), PacketUpdateSign.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x13, new Version[]{Version.V1_12_2}, 0x13), PacketPlayerAbilitiesIn.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x14, new Version[]{Version.V1_12_2}, 0x01), PacketTabComplete.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x15, new Version[]{Version.V1_12_2}, 0x04), PacketClientSettings.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x16, new Version[]{Version.V1_12_2}, 0x03), PacketClientStatus.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x17, new Version[]{Version.V1_12_2}, 0x09), PacketCustomPayload.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x18, new Version[]{Version.V1_12_2}, 0x1E), PacketSpectate.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x19, new Version[]{Version.V1_12_2}, 0x18), PacketResourcePackStatus.class);

        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x00, new Version[]{Version.V1_12_2}, 0x1F), PacketKeepAliveOut.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x01, new Version[]{Version.V1_12_2}, 0x23), PacketJoinGame.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x02, new Version[]{Version.V1_12_2}, 0x0F), PacketChat.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x08, new Version[]{Version.V1_12_2}, 0x2F), PacketPlayerPosLook.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x2F, new Version[]{Version.V1_12_2}, 0x16), PacketPlaySetSlot.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x34, new Version[]{Version.V1_12_2}, 0x24), PacketPlayMap.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x39, new Version[]{Version.V1_12_2}, 0x2C), PacketPlayerAbilitiesOut.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x40, new Version[]{Version.V1_12_2}, 0x1A), PacketKickDisconnect.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x45, new Version[]{Version.V1_12_2}, 0x48), PacketTitle.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, Map.of(new Version[]{Version.V1_8}, 0x47, new Version[]{Version.V1_12_2}, 0x4A), PacketPlayerListHeaderFooter.class);
    }

    private void registerPacket(final LightSpigotServer server, final PacketDirection direction, final ConnectionState connectionState, final Map<Version[], Integer> versions, final Class<? extends Packet> packet) {
        for (Map.Entry<Version[], Integer> version : versions.entrySet()) {
            for (Version singleVersion : version.getKey()) {
                registerPacket(server, direction, connectionState, singleVersion, version.getValue(), packet);
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

}
