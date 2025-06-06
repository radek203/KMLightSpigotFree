package pl.kwadratowamasakra.lightspigot.connection;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The PacketLimiter class is used to limit the number of packets of a certain type that can be received within a certain time frame.
 * It includes methods to add a packet and get the current count of packets.
 */
public class PacketLimiter {

    private final Deque<Long> packetsCounter = new ArrayDeque<>();

    /**
     * Adds a packet to the counter and returns the current count of packets.
     * The counter is reset after a specified amount of time.
     *
     * @param resetTime The amount of time (in milliseconds) after which the counter is reset.
     * @return The current count of packets.
     */
    public final int addAndGetPacketsCount(final long resetTime) {
        final long time = System.currentTimeMillis();
        packetsCounter.addLast(time);
        while (!packetsCounter.isEmpty() && packetsCounter.peekFirst() + resetTime < time) {
            packetsCounter.removeFirst();
        }
        return packetsCounter.size();
    }
}
