import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.PacketLimiter;

class PacketLimiterTest {

    @Test
    void packetsInsideWindowShouldAccumulate() {
        final PacketLimiter limiter = new PacketLimiter();

        Assertions.assertEquals(1, limiter.addAndGetPacketsCount(10_000));
        Assertions.assertEquals(2, limiter.addAndGetPacketsCount(10_000));
        Assertions.assertEquals(3, limiter.addAndGetPacketsCount(10_000));
    }

    @Test
    void expiredPacketsShouldBeRemoved() throws InterruptedException {
        final PacketLimiter limiter = new PacketLimiter();
        Assertions.assertEquals(1, limiter.addAndGetPacketsCount(1));
        Thread.sleep(5);

        Assertions.assertEquals(1, limiter.addAndGetPacketsCount(1));
    }
}
