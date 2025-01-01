package pl.kwadratowamasakra.lightspigot.utils.maps;

public class MapIcon {
    private final byte type;
    private final byte x;
    private final byte y;
    private final byte rotation;

    public MapIcon(final byte var1, final byte var2, final byte var3, final byte var4) {
        type = var1;
        x = var2;
        y = var3;
        rotation = var4;
    }

    public MapIcon(final MapIcon var1) {
        type = var1.type;
        x = var1.x;
        y = var1.y;
        rotation = var1.rotation;
    }

    public final byte getType() {
        return type;
    }

    public final byte getX() {
        return x;
    }

    public final byte getY() {
        return y;
    }

    public final byte getRotation() {
        return rotation;
    }

    public final boolean equals(final Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof final MapIcon var2)) {
            return false;
        } else {
            if (type != var2.type) {
                return false;
            } else if (rotation != var2.rotation) {
                return false;
            } else if (x != var2.x) {
                return false;
            } else {
                return y == var2.y;
            }
        }
    }

    public final int hashCode() {
        int var1 = type;
        var1 = 31 * var1 + x;
        var1 = 31 * var1 + y;
        var1 = 31 * var1 + rotation;
        return var1;
    }
}
