package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutBlockChange;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkData;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;
import pl.kwadratowamasakra.lightspigot.world.ChunkSnapshot;
import pl.kwadratowamasakra.lightspigot.world.World;

final class BlockUpdateSender {

    private BlockUpdateSender() {
    }

    static void sendBlock(final PlayerConnection connection, final World world, final BlockPosition position) {
        final int blockValue = world.getBlockValue(position.x(), position.y(), position.z());
        connection.sendPacket(new PacketPlayOutBlockChange(position.x(), position.y(), position.z(),
                blockValue >>> 4, blockValue & 0x0F));
    }

    static void sendChunk(final PlayerConnection connection, final World world, final BlockPosition position) {
        final ChunkSnapshot chunk = world.getChunkAt(position.x(), position.z());
        if (chunk != null) {
            connection.sendPacket(new PacketPlayOutChunkData(
                    chunk.chunkX(), chunk.chunkZ(), true, chunk.primaryBitMask(), chunk.chunkData()));
        }
    }
}
