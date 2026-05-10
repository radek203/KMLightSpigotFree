package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketPlayInBlockPlace extends PacketIn {

    private long position;
    private int placedBlockDirection;
    //private ItemStack stack;
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
        } else if (connection.getVersion().isEqualOrHigher(Version.V1_11)) {
            position = packetBuffer.readLong();
            placedBlockDirection = packetBuffer.readVarInt();
            hand = packetBuffer.readVarInt();
            facingX = packetBuffer.readFloat();
            facingY = packetBuffer.readFloat();
            facingZ = packetBuffer.readFloat();
        } else {
            position = packetBuffer.readLong();
            placedBlockDirection = packetBuffer.readUnsignedByte();
            if (packetBuffer.readableBytes() < 3) {
                throw new IllegalArgumentException("Expected 3 facing bytes");
            }
            packetBuffer.skipBytes(packetBuffer.readableBytes() - 3);
            facingX = packetBuffer.readUnsignedByte() / 16.0F;
            facingY = packetBuffer.readUnsignedByte() / 16.0F;
            facingZ = packetBuffer.readUnsignedByte() / 16.0F;
        }
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 30;
    }
}
