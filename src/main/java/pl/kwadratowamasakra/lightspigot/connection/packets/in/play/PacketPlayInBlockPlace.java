package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;
import pl.kwadratowamasakra.lightspigot.world.World;
import pl.kwadratowamasakra.lightspigot.world.utils.LegacyBlockPlacementData;

public class PacketPlayInBlockPlace extends PacketIn {

    private long position;
    private int placedBlockDirection;
    private ItemStack stack;
    private float facingX;
    private float facingY;
    private float facingZ;
    private int hand;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        if (connection.getVersion().isInRange(Version.V1_9, Version.V1_10)) {
            position = packetBuffer.readLong();
            placedBlockDirection = packetBuffer.readVarInt();
            hand = packetBuffer.readVarInt();
            facingX = packetBuffer.readUnsignedByte() / 16.0F;
            facingY = packetBuffer.readUnsignedByte() / 16.0F;
            facingZ = packetBuffer.readUnsignedByte() / 16.0F;
        } else if (connection.getVersion().isInRange(Version.V1_11, Version.V1_13_2)) {
            position = packetBuffer.readLong();
            placedBlockDirection = packetBuffer.readVarInt();
            hand = packetBuffer.readVarInt();
            facingX = packetBuffer.readFloat();
            facingY = packetBuffer.readFloat();
            facingZ = packetBuffer.readFloat();
        } else if (connection.getVersion().isEqualOrHigher(Version.V1_14)) {
            hand = packetBuffer.readVarInt();
            position = packetBuffer.readLong();
            placedBlockDirection = packetBuffer.readVarInt();
            facingX = packetBuffer.readFloat();
            facingY = packetBuffer.readFloat();
            facingZ = packetBuffer.readFloat();
            packetBuffer.readBoolean(); // cursor is inside a block
        } else {
            position = packetBuffer.readLong();
            placedBlockDirection = packetBuffer.readUnsignedByte();
            final short itemId = packetBuffer.readShort();
            if (itemId == -1) {
                stack = new ItemStack();
            } else {
                stack = new ItemStack(itemId, packetBuffer.readUnsignedByte(), packetBuffer.readShort());
            }
            if (packetBuffer.readableBytes() < 3) {
                throw new IllegalArgumentException("Expected block placement facing bytes");
            }
            if (itemId != -1) {
                packetBuffer.skipBytes(packetBuffer.readableBytes() - 3);
            }
            facingX = packetBuffer.readUnsignedByte() / 16.0F;
            facingY = packetBuffer.readUnsignedByte() / 16.0F;
            facingZ = packetBuffer.readUnsignedByte() / 16.0F;
        }
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
        if (placedBlockDirection < 0 || placedBlockDirection > 5) {
            return;
        }

        final BlockPosition placedPosition = BlockPosition.fromLong(position, connection.getVersion()).relative(placedBlockDirection);
        final ItemStack placedItem = connection.getVersion().isEqual(Version.V1_8) ? stack : connection.getHeldItem(hand);
        if (!connection.isOp() || placedItem == null || !placedItem.isItem()) {
            BlockUpdateSender.sendBlock(connection, server.getWorld(), placedPosition);
            return;
        }

        final int blockId = placedItem.getItemId();
        final int blockData = LegacyBlockPlacementData.resolve(
                blockId, placedItem.getData(), placedBlockDirection, facingY, connection.getYaw());
        final World world = server.getWorld();
        final boolean knownSection = world.hasSectionAt(placedPosition.x(), placedPosition.y(), placedPosition.z());
        if (!world.setBlock(placedPosition.x(), placedPosition.y(), placedPosition.z(), blockId, blockData)) {
            BlockUpdateSender.sendBlock(connection, world, placedPosition);
            return;
        }

        if (!knownSection) {
            BlockUpdateSender.sendChunk(connection, world, placedPosition);
        }
        BlockUpdateSender.sendBlock(connection, world, placedPosition);
        for (final PlayerConnection other : server.getConnectionManager().getConnections()) {
            if (other != connection) {
                if (!knownSection) {
                    BlockUpdateSender.sendChunk(other, world, placedPosition);
                }
                BlockUpdateSender.sendBlock(other, world, placedPosition);
            }
        }
    }

    @Override
    public final int getLimit() {
        return 30;
    }

}
