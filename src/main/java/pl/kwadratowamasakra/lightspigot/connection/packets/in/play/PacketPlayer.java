package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketPlayer extends PacketIn {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    //protected boolean onGround;
    //protected boolean moving;
    //protected boolean rotating;

    @Override
    public void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {

    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 125;
    }

    public final double getX() {
        return x;
    }

    final void setX(final double x) {
        this.x = x;
    }

    public final double getY() {
        return y;
    }

    final void setY(final double y) {
        this.y = y;
    }

    public final double getZ() {
        return z;
    }

    final void setZ(final double z) {
        this.z = z;
    }

    public final float getYaw() {
        return yaw;
    }

    final void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public final float getPitch() {
        return pitch;
    }

    final void setPitch(final float pitch) {
        this.pitch = pitch;
    }

    public static class PacketPlayerPosition extends PacketPlayer {

        public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
            setX(packetBuffer.readDouble());
            setY(packetBuffer.readDouble());
            setZ(packetBuffer.readDouble());
            super.read(connection, packetBuffer);
        }

    }

    public static class PacketPlayerLook extends PacketPlayer {

        public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
            setYaw(packetBuffer.readFloat());
            setPitch(packetBuffer.readFloat());
            super.read(connection, packetBuffer);
        }

    }

    public static class PacketPlayerPosLook extends PacketPlayer {

        public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
            setX(packetBuffer.readDouble());
            setY(packetBuffer.readDouble());
            setZ(packetBuffer.readDouble());
            setYaw(packetBuffer.readFloat());
            setPitch(packetBuffer.readFloat());
            super.read(connection, packetBuffer);
        }

    }

}
