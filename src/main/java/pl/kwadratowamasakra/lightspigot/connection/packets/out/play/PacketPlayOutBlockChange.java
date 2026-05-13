package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

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

        long pos = ((x & 0x3FFFFFFL) << 38) | ((z & 0x3FFFFFFL) << 12) | (y & 0xFFFL);

        buf.writeLongVar(pos);

        buf.writeVarInt(blockId);
    }
}
