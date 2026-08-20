package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;
import pl.kwadratowamasakra.lightspigot.world.World;

public class PacketPlayInBlockDig extends PacketIn {

    private long position;
    private int facing;
    private PacketPlayInBlockDig.Action status;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        status = packetBuffer.readEnumValue(Action.class);
        position = packetBuffer.readLong();
        facing = packetBuffer.readUnsignedByte();
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
        if (status != Action.START_DESTROY_BLOCK && status != Action.STOP_DESTROY_BLOCK) {
            return;
        }

        final BlockPosition blockPosition = BlockPosition.fromLong(position, connection.getVersion());
        final World world = server.getWorld();
        if (!connection.isOp() || !world.isInsideWorldBounds(blockPosition.x(), blockPosition.z())) {
            BlockUpdateSender.sendBlock(connection, world, blockPosition);
            return;
        }
        if (world.getBlockValue(blockPosition.x(), blockPosition.y(), blockPosition.z()) != 0) {
            world.setBlock(blockPosition.x(), blockPosition.y(), blockPosition.z(), 0, 0);
        }

        BlockUpdateSender.sendBlock(connection, world, blockPosition);
        for (final PlayerConnection other : server.getConnectionManager().getConnections()) {
            if (other != connection) {
                BlockUpdateSender.sendBlock(other, world, blockPosition);
            }
        }
    }

    @Override
    public final int getLimit() {
        return 30;
    }

    public enum Action {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS //>=1.9
    }
}
