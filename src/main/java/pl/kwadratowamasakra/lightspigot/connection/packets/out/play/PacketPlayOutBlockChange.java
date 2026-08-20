package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolMappings;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;
import pl.kwadratowamasakra.lightspigot.world.World;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.LegacyStairShape;

@NoArgsConstructor
public class PacketPlayOutBlockChange extends PacketOut {

    private int x;
    private int y;
    private int z;

    private int blockId;
    private int blockMeta;
    private World world;

    public PacketPlayOutBlockChange(final int x, final int y, final int z,
                                    final int blockId, final int blockMeta) {
        this(x, y, z, blockId, blockMeta, null);
    }

    public PacketPlayOutBlockChange(final int x, final int y, final int z,
                                    final int blockId, final int blockMeta, final World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockId = blockId;
        this.blockMeta = blockMeta;
        this.world = world;
    }

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer buf) {
        final Version version = connection == null ? Version.V1_8 : connection.getVersion();
        buf.writeLong(new BlockPosition(x, y, z).toLong(version));
        if (version.isEqualOrHigher(Version.V1_13)) {
            final int legacyValue = BlockUtils.createLegacyBlockValue(blockId, blockMeta);
            final LegacyStairShape.Shape shape = world != null && LegacyStairShape.isStairs(legacyValue)
                    ? LegacyStairShape.resolve(world::getBlockValue, x, y, z)
                    : LegacyStairShape.Shape.STRAIGHT;
            buf.writeVarInt(ProtocolMappings.blockState(version, blockId, blockMeta, shape));
        } else {
            buf.writeVarInt(BlockUtils.createLegacyBlockValue(blockId, blockMeta));
        }
    }
}
