package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutSetSlot extends PacketOut {

    private int containerId;
    private int slot;
    private ItemStack itemStack;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeByte(containerId);
        if (connection.getVersion().isEqualOrHigher(Version.V1_17_1)) {
            packetBuffer.writeVarInt(0); // inventory state id
        }
        packetBuffer.writeShort(slot);
        packetBuffer.writeItemStack(connection, itemStack);
    }

}
