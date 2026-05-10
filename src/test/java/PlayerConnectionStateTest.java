import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;

class PlayerConnectionStateTest {

    @Test
    void selectedHotbarAndOffhandItemsShouldResolveByProtocolHand() {
        final EmbeddedChannel channel = new EmbeddedChannel();
        final PlayerConnection connection = new PlayerConnection(channel, null);
        final ItemStack hotbar = new ItemStack(53, 1, 0);
        final ItemStack offhand = new ItemStack(1, 1, 2);
        connection.updateInventoryItem(38, hotbar);
        connection.updateInventoryItem(45, offhand);
        connection.setHeldItemSlot(2);

        Assertions.assertSame(hotbar, connection.getHeldItem(0));
        Assertions.assertSame(offhand, connection.getHeldItem(1));
        Assertions.assertNull(connection.getHeldItem(2));
        channel.finishAndReleaseAll();
    }

    @Test
    void emptyItemShouldRemoveTrackedInventorySlot() {
        final EmbeddedChannel channel = new EmbeddedChannel();
        final PlayerConnection connection = new PlayerConnection(channel, null);
        connection.updateInventoryItem(36, new ItemStack(1, 1, 0));
        connection.updateInventoryItem(36, new ItemStack());

        Assertions.assertNull(connection.getHeldItem(0));
        channel.finishAndReleaseAll();
    }

    @Test
    void inventoryAndHeldSlotBoundsShouldBeValidated() {
        final EmbeddedChannel channel = new EmbeddedChannel();
        final PlayerConnection connection = new PlayerConnection(channel, null);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> connection.updateInventoryItem(-1, new ItemStack()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> connection.updateInventoryItem(46, new ItemStack()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> connection.setHeldItemSlot(-1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> connection.setHeldItemSlot(9));
        channel.finishAndReleaseAll();
    }

    @Test
    void protocolConnectionAndRotationStateShouldBeStored() {
        final EmbeddedChannel channel = new EmbeddedChannel();
        final PlayerConnection connection = new PlayerConnection(channel, null);
        connection.setVersion(Version.V1_8);
        connection.setConnectionState(ConnectionState.PLAY);
        connection.updateRotation(-90.0F, 30.0F);

        Assertions.assertEquals(Version.V1_8, connection.getVersion());
        Assertions.assertEquals(-90.0F, connection.getYaw());
        Assertions.assertDoesNotThrow(() -> connection.verifyState(ConnectionState.PLAY));
        Assertions.assertThrows(IllegalStateException.class, () -> connection.verifyState(ConnectionState.LOGIN));
        channel.finishAndReleaseAll();
    }
}
