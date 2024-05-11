package pl.kwadratowamasakra.lightspigot.connection.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The GameProfile class represents a player's game profile.
 * It includes the player's UUID, name, and properties such as skin texture.
 */
public class GameProfile {

    private final UUID uuid;
    private final String name;

    private final Map<String, Property> propertyMap = new HashMap<>();

    /**
     * Constructs a new GameProfile with the specified UUID and name.
     *
     * @param uuid The UUID of the player.
     * @param name The name of the player.
     */
    public GameProfile(final UUID uuid, final String name) {
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * @return The UUID of the player.
     */
    public final UUID getUUID() {
        return uuid;
    }

    /**
     * @return The name of the player.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the property associated with the specified key.
     *
     * @param key The key of the property.
     * @return The property associated with the key.
     */
    public final Property getProperty(final String key) {
        return propertyMap.get(key);
    }

    /**
     * Adds a property to the property map with the specified key.
     *
     * @param key      The key of the property.
     * @param property The property to add.
     */
    public final void addProperty(final String key, final Property property) {
        propertyMap.put(key, property);
    }

    /**
     * Removes the property associated with the specified key from the property map.
     *
     * @param key The key of the property to remove.
     */
    public final void removeProperty(final String key) {
        propertyMap.remove(key);
    }

    /**
     * Sets the skin of the player.
     * The skin is represented as a property with the key "textures".
     *
     * @param value     The value of the skin property.
     * @param signature The signature of the skin property.
     */
    public final void setSkin(final String value, final String signature) {
        propertyMap.remove("textures");
        propertyMap.put("textures", new Property("textures", value, signature));
    }
}
