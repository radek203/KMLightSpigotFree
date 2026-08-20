package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.*;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;
import pl.kwadratowamasakra.lightspigot.world.ChunkSnapshot;
import pl.kwadratowamasakra.lightspigot.world.World;

final class BlockUpdateSender {

    private BlockUpdateSender() {
    }

    static void sendBlock(final PlayerConnection connection, final World world, final BlockPosition position) {
        sendSingleBlock(connection, world, position);
        if (connection.getVersion().isLessThan(Version.V1_13)) return;
        sendSingleBlock(connection, world, new BlockPosition(position.x() - 1, position.y(), position.z()));
        sendSingleBlock(connection, world, new BlockPosition(position.x() + 1, position.y(), position.z()));
        sendSingleBlock(connection, world, new BlockPosition(position.x(), position.y(), position.z() - 1));
        sendSingleBlock(connection, world, new BlockPosition(position.x(), position.y(), position.z() + 1));
    }

    private static void sendSingleBlock(final PlayerConnection connection, final World world,
                                        final BlockPosition position) {
        final int blockValue = world.getBlockValue(position.x(), position.y(), position.z());
        connection.sendPacket(new PacketPlayOutBlockChange(position.x(), position.y(), position.z(),
                blockValue >>> 4, blockValue & 0x0F, world));
    }

    static void sendChunk(final PlayerConnection connection, final World world, final BlockPosition position) {
        final ChunkSnapshot chunk = world.getChunkAt(position.x(), position.z());
        if (chunk != null) {
            final boolean batched = connection.getVersion().isEqualOrHigher(Version.V1_20_2);
            if (batched) connection.writePacket(new PacketPlayOutChunkBatchStart());
            final PacketPlayOutChunkData chunkPacket = new PacketPlayOutChunkData(
                    chunk.chunkX(), chunk.chunkZ(), true, chunk.primaryBitMask(), chunk.chunkData(), world);
            if (batched) connection.writePacket(chunkPacket);
            else connection.sendPacket(chunkPacket);
            if (connection.getVersion().isInRange(Version.V1_14, Version.V1_17_1)) {
                connection.sendPacket(new PacketPlayOutUpdateLight(
                        chunk.chunkX(), chunk.chunkZ(), chunk.primaryBitMask(), chunk.chunkData()));
            }
            if (batched) connection.sendPacket(new PacketPlayOutChunkBatchFinish(1));
        }
    }
}
