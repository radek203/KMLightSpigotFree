package pl.kwadratowamasakra.lightspigot.connection;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;

import java.util.ArrayList;
import java.util.List;

/**
 * The PacketLimiter class is used to limit the number of packets of a certain type that can be received within a certain time frame.
 * It includes methods to add a packet and get the current count of packets.
 */
public class PacketLimiter {

    private final Class<? extends PacketIn> packetIn;
    private final List<Long> packetsCounter = new ArrayList<>();

    /**
     * Constructs a new PacketLimiter for the specified type of packet.
     *
     * @param packetIn The type of packet to limit.
     */
    public PacketLimiter(final Class<? extends PacketIn> packetIn) {
        this.packetIn = packetIn;
    }

    /**
     * Returns the type of packet that this PacketLimiter is limiting.
     *
     * @return The type of packet.
     */
    public final Class<? extends PacketIn> getPacketIn() {
        return packetIn;
    }

    /**
     * Adds a packet to the counter and returns the current count of packets.
     * The counter is reset after a specified amount of time.
     *
     * @param resetTime The amount of time (in milliseconds) after which the counter is reset.
     * @return The current count of packets.
     */
    public final int addAndGetPacketsCount(final long resetTime) {
        final long time = System.currentTimeMillis();
        packetsCounter.add(time);
        packetsCounter.removeIf(aLong -> aLong + resetTime < time);
        return packetsCounter.size();
    }
}
