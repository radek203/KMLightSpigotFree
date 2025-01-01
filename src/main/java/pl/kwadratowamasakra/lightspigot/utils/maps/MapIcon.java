package pl.kwadratowamasakra.lightspigot.utils.maps;

public class MapIcon {
    private final byte type;
    private final byte x;
    private final byte y;
    private final byte rotation;

    public MapIcon(final byte type, final byte x, final byte y, final byte rotation) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
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

    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof final MapIcon icon)) {
            return false;
        } else {
            if (type != icon.type) {
                return false;
            } else if (rotation != icon.rotation) {
                return false;
            } else if (x != icon.x) {
                return false;
            } else {
                return y == icon.y;
            }
        }
    }

    public final int hashCode() {
        int hash = type;
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        hash = 31 * hash + rotation;
        return hash;
    }
}
