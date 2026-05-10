package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutBlockChange extends PacketOut {

    private int x;
    private int y;
    private int z;

    private int blockId;
    private int blockMeta;

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer buf) {
        buf.writeLong(new BlockPosition(x, y, z).toLong());
        buf.writeVarInt(BlockUtils.createLegacyBlockValue(blockId, blockMeta));
    }
}
