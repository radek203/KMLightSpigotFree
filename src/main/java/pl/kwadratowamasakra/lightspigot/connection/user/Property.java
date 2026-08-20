package pl.kwadratowamasakra.lightspigot.connection.user;

/**
 * The Property class represents a property of a user's game profile.
 * It includes the name, value, and signature of the property.
 */
public record Property(String name, String value, String signature) {

    /**
     * Constructs a new Property with the specified name, value, and signature.
     *
     * @param name      The name of the property.
     * @param value     The value of the property.
     * @param signature The signature of the property.
     */
    public Property {
    }

    /**
     * @return The name of the property.
     */
    @Override
    public final String name() {
        return name;
    }

    /**
     * @return The value of the property.
     */
    @Override
    public final String value() {
        return value;
    }

    /**
     * @return The signature of the property.
     */
    @Override
    public final String signature() {
        return signature;
    }

}
