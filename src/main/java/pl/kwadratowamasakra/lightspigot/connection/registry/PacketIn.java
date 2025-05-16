package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

/**
 * The PacketIn class represents a packet that is received from a client.
 * It is an abstract class that defines the methods that all incoming packets should implement.
 */
public abstract class PacketIn implements Packet {

    /**
     * Reads the data from the packet buffer.
     *
     * @param packetBuffer The packet buffer to read from.
     */
    public abstract void read(PlayerConnection connection, PacketBuffer packetBuffer);

    /**
     * Handles the packet after it has been read.
     * This method is responsible for performing the actions that should be taken when the packet is received.
     *
     * @param connection The connection from which the packet was received.
     * @param server     The server that received the packet.
     */
    public abstract void handle(PlayerConnection connection, LightSpigotServer server);

    /**
     * Returns the limit for the count of packets of this type that can be received within a certain time frame.
     * If the limit is exceeded, the packet will be ignored and user will be disconnected.
     *
     * @return The limit for the count of packets.
     */
    public abstract int getLimit();
}
