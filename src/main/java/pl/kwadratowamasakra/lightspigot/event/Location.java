package pl.kwadratowamasakra.lightspigot.event;

/**
 * The Location class represents a location in a 3D space.
 * It includes x, y, and z coordinates, as well as yaw and pitch for orientation.
 */
public class Location {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(final double x, final double z) {
        this.x = x;
        this.z = z;
    }

    public final double getX() {
        return x;
    }

    public final void setX(final double x) {
        this.x = x;
    }

    public final double getY() {
        return y;
    }

    public final void setY(final double y) {
        this.y = y;
    }

    public final double getZ() {
        return z;
    }

    public final void setZ(final double z) {
        this.z = z;
    }

    public final float getYaw() {
        return yaw;
    }

    public final void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public final float getPitch() {
        return pitch;
    }

    public final void setPitch(final float pitch) {
        this.pitch = pitch;
    }

}
