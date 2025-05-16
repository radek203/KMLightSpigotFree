package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;

public class PacketCreativeInventoryAction extends PacketIn {

    private int slotId;
    private ItemStack item;
    private short stack;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        slotId = packetBuffer.readShort();
        if (connection.getVersion().isEqualOrHigher(Version.V1_12_2)) {
            item = packetBuffer.readItemStack();
        } else {
            stack = packetBuffer.readShort();
        }
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 3;
    }
}
