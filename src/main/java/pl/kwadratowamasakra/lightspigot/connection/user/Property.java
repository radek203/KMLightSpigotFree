package pl.kwadratowamasakra.lightspigot.connection.user;

/**
 * The Property class represents a property of a user's game profile.
 * It includes the name, value, and signature of the property.
 */
public class Property {

    private final String name;
    private final String value;
    private final String signature;

    /**
     * Constructs a new Property with the specified name, value, and signature.
     *
     * @param name      The name of the property.
     * @param value     The value of the property.
     * @param signature The signature of the property.
     */
    public Property(final String name, final String value, final String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    /**
     * @return The name of the property.
     */
    public final String getName() {
        return name;
    }

    /**
     * @return The value of the property.
     */
    public final String getValue() {
        return value;
    }

    /**
     * @return The signature of the property.
     */
    public final String getSignature() {
        return signature;
    }

}
