package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.connection.Version;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ProtocolConfiguration {

    private static final Map<Version, List<byte[]>> REGISTRIES = loadRegistries();
    private static final Map<Version, byte[]> TAGS = loadAllTags();

    private ProtocolConfiguration() {
    }

    /**
     * Forces every configuration-era resource to be loaded before players can connect.
     */
    public static void preload() {
        // Accessing this method initializes both immutable maps.
    }

    public static List<byte[]> registries(final Version version) {
        final List<byte[]> registries = REGISTRIES.get(version);
        if (registries == null) {
            throw new IllegalArgumentException("No protocol configuration for " + version);
        }
        return registries;
    }

    public static void writeRequiredTags(final Version version, final PacketBuffer output) {
        if (version.isLessThan(Version.V1_20_5)) {
            output.writeVarInt(0);
            return;
        }
        final byte[] tags = TAGS.get(version);
        if (tags == null) {
            throw new IllegalArgumentException("No protocol tags for " + version);
        }
        output.writeBytes(tags);
    }

    private static Map<Version, List<byte[]>> loadRegistries() {
        final Map<Version, List<byte[]>> result = new EnumMap<>(Version.class);
        for (final Version version : Version.getVersionsRange(Version.V1_20_2, Version.V26_2)) {
            result.put(version, load(version));
        }
        return Map.copyOf(result);
    }

    private static Map<Version, byte[]> loadAllTags() {
        final Map<Version, byte[]> result = new EnumMap<>(Version.class);
        for (final Version version : Version.getVersionsRange(Version.V1_20_5, Version.V26_2)) {
            result.put(version, loadTags(version));
        }
        return Map.copyOf(result);
    }

    private static byte[] loadTags(final Version version) {
        final String fileName = "/protocol/tags-" + version.getProtocolNumber() + ".bin";
        try (InputStream stream = ProtocolConfiguration.class.getResourceAsStream(fileName)) {
            if (stream == null) {
                throw new IllegalStateException("Missing protocol tags resource " + fileName);
            }
            return stream.readAllBytes();
        } catch (final IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static List<byte[]> load(final Version version) {
        final String fileName = "/protocol/configuration-" + version.getProtocolNumber() + ".bin";
        try (InputStream stream = ProtocolConfiguration.class.getResourceAsStream(fileName)) {
            if (stream == null) {
                throw new IllegalStateException("Missing protocol configuration resource " + fileName);
            }
            final DataInputStream input = new DataInputStream(stream);
            final int count = input.readUnsignedShort();
            final List<byte[]> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                final int length = input.readInt();
                if (length < 0 || length > 16 * 1024 * 1024) {
                    throw new IllegalStateException("Invalid registry data length " + length);
                }
                final byte[] registry = input.readNBytes(length);
                if (registry.length != length) {
                    throw new IllegalStateException("Truncated registry data in " + fileName);
                }
                result.add(registry);
            }
            if (input.read() != -1) throw new IllegalStateException("Trailing registry data in " + fileName);
            return List.copyOf(result);
        } catch (final IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
