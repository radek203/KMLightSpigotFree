package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.PacketDirection;
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

    private final Map<PacketDirection, Map<ConnectionState, Map<Integer, RegisteredPacket>>> packets = new EnumMap<>(PacketDirection.class);

    /**
     * Registers all the packets that will be used in the network communication.
     *
     * @param server The server that will be using the packets.
     */
    public final void registerPackets(final LightSpigotServer server) {
        registerPacket(server, PacketDirection.IN, ConnectionState.HANDSHAKING, 0x00, PacketHandshake.class);

        registerPacket(server, PacketDirection.IN, ConnectionState.STATUS, 0x00, PacketServerQuery.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.STATUS, 0x01, PacketPing.class);

        registerPacket(server, PacketDirection.OUT, ConnectionState.STATUS, 0x00, PacketServerInfo.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.STATUS, 0x01, PacketPong.class);

        registerPacket(server, PacketDirection.IN, ConnectionState.LOGIN, 0x00, PacketLoginStart.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.LOGIN, 0x01, PacketEncryptionResponse.class);

        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, 0x00, PacketDisconnect.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, 0x01, PacketEncryptionRequest.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, 0x02, PacketLoginSuccess.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.LOGIN, 0x03, PacketEnableCompression.class);

        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x00, PacketKeepAliveIn.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x01, PacketChatMessage.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x02, PacketUseEntity.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x03, PacketPlayer.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x04, PacketPlayer.PacketPlayerPosition.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x05, PacketPlayer.PacketPlayerLook.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x06, PacketPlayer.PacketPlayerPosLook.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x07, PacketPlayerDigging.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x08, PacketPlayerBlockPlacement.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x09, PacketHeldItemChange.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x0A, PacketAnimation.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x0B, PacketEntityAction.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x0C, PacketInput.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x0D, PacketCloseWindow.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x0E, PacketClickWindow.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x0F, PacketConfirmTransaction.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x10, PacketCreativeInventoryAction.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x11, PacketEnchantItem.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x12, PacketUpdateSign.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x13, PacketPlayerAbilitiesIn.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x14, PacketTabComplete.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x15, PacketClientSettings.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x16, PacketClientStatus.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x17, PacketCustomPayload.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x18, PacketSpectate.class);
        registerPacket(server, PacketDirection.IN, ConnectionState.PLAY, 0x19, PacketResourcePackStatus.class);

        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, 0x00, PacketKeepAliveOut.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, 0x01, PacketJoinGame.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, 0x02, PacketChat.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, 0x08, PacketPlayerPosLook.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, 0x39, PacketPlayerAbilitiesOut.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, 0x45, PacketTitle.class);
        registerPacket(server, PacketDirection.OUT, ConnectionState.PLAY, 0x47, PacketPlayerListHeaderFooter.class);
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
    private void registerPacket(final LightSpigotServer server, final PacketDirection direction, final ConnectionState connectionState, final int packetId, final Class<? extends Packet> packet) {
        if (containsPacketId(direction, connectionState, packetId)) {
            server.getLogger().error("PacketManager.registerPacket(...) error", "PacketID is already in use (" + packetId + ")");
            return;
        }
        try {
            final RegisteredPacket registeredPacket = new RegisteredPacket(packet);
            if (!packets.containsKey(direction)) {
                packets.put(direction, new EnumMap<>(ConnectionState.class));
            }
            if (!packets.get(direction).containsKey(connectionState)) {
                packets.get(direction).put(connectionState, new HashMap<>());
            }
            packets.get(direction).get(connectionState).put(packetId, registeredPacket);
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
    public final int getPacketId(final PacketDirection direction, final Class<? extends Packet> packetClass) {
        for (final Map.Entry<ConnectionState, Map<Integer, RegisteredPacket>> it : packets.get(direction).entrySet()) {
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
    public final Packet constructPacket(final PacketDirection direction, final ConnectionState connectionState, final int packetId) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return packets.get(direction).get(connectionState).get(packetId).getConstructor().newInstance();
    }

    /**
     * Checks if a packet ID exists for the specified direction and connection state.
     *
     * @param direction       The direction of the packet (IN or OUT).
     * @param connectionState The connection state when the packet is used.
     * @param id              The ID of the packet.
     * @return True if the packet ID exists, false otherwise.
     */
    public final boolean containsPacketId(final PacketDirection direction, final ConnectionState connectionState, final int id) {
        if (packets.containsKey(direction)) {
            if (packets.get(direction).containsKey(connectionState)) {
                return packets.get(direction).get(connectionState).containsKey(id);
            }
        }
        return false;
    }

}
