package pl.kwadratowamasakra.lightspigot.utils.maps;

public record MapIcon(byte type, byte x, byte y, byte rotation) {

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

}
