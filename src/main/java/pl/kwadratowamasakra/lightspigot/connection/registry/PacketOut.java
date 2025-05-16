package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

/**
 * The PacketOut class represents a packet that is sent to a client.
 * It is an abstract class that defines the methods that all outgoing packets should implement.
 */
public abstract class PacketOut implements Packet {

    /**
     * Writes the data to the packet buffer.
     *
     * @param packetBuffer The packet buffer to write to.
     */
    public abstract void write(final PlayerConnection connection, PacketBuffer packetBuffer);

}
