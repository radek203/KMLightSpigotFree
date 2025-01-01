package pl.kwadratowamasakra.lightspigot.utils.maps;

/**
 * Represents a cursor on a map.
 */
public final class MapCursor {
    private byte x, y;
    private byte direction, type;
    private boolean visible;

    /**
     * Initialize the map cursor.
     *
     * @param x         The x coordinate, from -128 to 127.
     * @param y         The y coordinate, from -128 to 127.
     * @param direction The facing of the cursor, from 0 to 15.
     * @param type      The type (color/style) of the map cursor.
     * @param visible   Whether the cursor is visible by default.
     * @deprecated Magic value
     */
    @Deprecated
    public MapCursor(final byte x, final byte y, final byte direction, final byte type, final boolean visible) {
        this.x = x;
        this.y = y;
        setDirection(direction);
        setRawType(type);
        this.visible = visible;
    }

    /**
     * Get the X position of this cursor.
     *
     * @return The X coordinate.
     */
    public byte getX() {
        return x;
    }

    /**
     * Set the X position of this cursor.
     *
     * @param x The X coordinate.
     */
    public void setX(final byte x) {
        this.x = x;
    }

    /**
     * Get the Y position of this cursor.
     *
     * @return The Y coordinate.
     */
    public byte getY() {
        return y;
    }

    /**
     * Set the Y position of this cursor.
     *
     * @param y The Y coordinate.
     */
    public void setY(final byte y) {
        this.y = y;
    }

    /**
     * Get the direction of this cursor.
     *
     * @return The facing of the cursor, from 0 to 15.
     */
    public byte getDirection() {
        return direction;
    }

    /**
     * Set the direction of this cursor.
     *
     * @param direction The facing of the cursor, from 0 to 15.
     */
    public void setDirection(final byte direction) {
        if (direction < 0 || direction > 15) {
            throw new IllegalArgumentException("Direction must be in the range 0-15");
        }
        this.direction = direction;
    }

    /**
     * Get the type of this cursor.
     *
     * @return The type (color/style) of the map cursor.
     */
    public Type getType() {
        return Type.byValue(type);
    }

    /**
     * Set the type of this cursor.
     *
     * @param type The type (color/style) of the map cursor.
     */
    public void setType(final Type type) {
        setRawType(type.value);
    }

    /**
     * Get the type of this cursor.
     *
     * @return The type (color/style) of the map cursor.
     */
    public byte getRawType() {
        return type;
    }

    /**
     * Set the type of this cursor.
     *
     * @param type The type (color/style) of the map cursor.
     * @deprecated Magic value
     */
    @Deprecated
    public void setRawType(final byte type) {
        if (type < 0 || type > 15) {
            throw new IllegalArgumentException("Type must be in the range 0-15");
        }
        this.type = type;
    }

    /**
     * Get the visibility status of this cursor.
     *
     * @return True if visible, false otherwise.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set the visibility status of this cursor.
     *
     * @param visible True if visible.
     */
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    /**
     * Represents the standard types of map cursors. More may be made
     * available by texture packs - the value is used by the client as an
     * index in the file './misc/mapicons.png' from minecraft.jar or from a
     * texture pack.
     */
    public enum Type {
        WHITE_POINTER(0),
        GREEN_POINTER(1),
        RED_POINTER(2),
        BLUE_POINTER(3),
        WHITE_CROSS(4);

        private final byte value;

        Type(final int value) {
            this.value = (byte) value;
        }

        /**
         * @param value the value
         * @return the matching type
         * @deprecated Magic value
         */
        @Deprecated
        public static Type byValue(final byte value) {
            for (final Type t : values()) {
                if (t.value == value) return t;
            }
            return null;
        }

        /**
         * @return the value
         * @deprecated Magic value
         */
        @Deprecated
        public byte getValue() {
            return value;
        }
    }

}

