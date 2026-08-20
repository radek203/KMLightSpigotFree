package pl.kwadratowamasakra.lightspigot.connection.registry;

/**
 * Loads and validates every generated protocol resource before networking starts.
 */
public final class ProtocolResources {

    private ProtocolResources() {
    }

    public static void preload() {
        ProtocolMappings.preload();
        ProtocolNbt.preload();
        ProtocolConfiguration.preload();
    }
}
