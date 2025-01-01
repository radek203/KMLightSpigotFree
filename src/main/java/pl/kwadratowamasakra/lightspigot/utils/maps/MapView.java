package pl.kwadratowamasakra.lightspigot.utils.maps;

import java.util.List;

/**
 * Represents a map item.
 */
public interface MapView {

    /**
     * Get the ID of this map item. Corresponds to the damage value of a map
     * in an inventory.
     *
     * @return The ID of the map.
     */
    short getId();

    /**
     * Get the scale of this map.
     *
     * @return The scale of the map.
     */
    Scale getScale();

    /**
     * Set the scale of this map.
     *
     * @param scale The scale to set.
     */
    void setScale(Scale scale);

    /**
     * Get a list of MapRenderers currently in effect.
     *
     * @return A {@code List<MapRenderer>} containing each map renderer.
     */
    List<MapRenderer> getRenderers();

    /**
     * Add a renderer to this map.
     *
     * @param renderer The MapRenderer to add.
     */
    void addRenderer(MapRenderer renderer);

    /**
     * Remove a renderer from this map.
     *
     * @param renderer The MapRenderer to remove.
     * @return True if the renderer was successfully removed.
     */
    boolean removeRenderer(MapRenderer renderer);

    /**
     * An enum representing all possible scales a map can be set to.
     */
    enum Scale {
        CLOSEST(0),
        CLOSE(1),
        NORMAL(2),
        FAR(3),
        FARTHEST(4);

        private final byte value;

        Scale(final int value) {
            this.value = (byte) value;
        }

        /**
         * Get the scale given the raw value.
         *
         * @param value The raw scale
         * @return The enum scale, or null for an invalid input
         */
        public static Scale valueOf(final byte value) {
            return switch (value) {
                case 0 -> CLOSEST;
                case 1 -> CLOSE;
                case 2 -> NORMAL;
                case 3 -> FAR;
                case 4 -> FARTHEST;
                default -> null;
            };
        }

        /**
         * Get the raw value of this scale level.
         *
         * @return The scale value
         */
        public byte getValue() {
            return value;
        }
    }

}
