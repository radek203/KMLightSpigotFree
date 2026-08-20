package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.connection.Version;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public final class ProtocolNbt {

    private static final Map<Version, byte[]> MODERN_CODECS = loadModernCodecs();
    private static final Map<Version, byte[]> MODERN_DIMENSIONS = loadModernDimensions();

    private static final Map<String, byte[]> RESOURCES = Map.of(
            "codec-1.16", load("dimension-codec-1.16.nbt"),
            "codec-1.16.2", load("dimension-codec-1.16.2.nbt"),
            "codec-1.17", load("dimension-codec-1.17.nbt"),
            "dimension-1.16.2-overworld", load("dimension-1.16.2-overworld.nbt"),
            "dimension-1.16.2-nether", load("dimension-1.16.2-nether.nbt"),
            "dimension-1.16.2-end", load("dimension-1.16.2-end.nbt"),
            "dimension-1.17-overworld", load("dimension-1.17-overworld.nbt"),
            "dimension-1.17-nether", load("dimension-1.17-nether.nbt"),
            "dimension-1.17-end", load("dimension-1.17-end.nbt")
    );

    private ProtocolNbt() {
    }

    /**
     * Forces all dimension resources to be loaded during server startup.
     */
    public static void preload() {
        // Accessing this method initializes the legacy and modern resource maps.
    }

    public static byte[] dimensionCodec(final Version version) {
        if (version.isEqualOrHigher(Version.V1_18)) {
            final byte[] codec = MODERN_CODECS.get(version);
            if (codec == null) throw new IllegalArgumentException("No dimension codec for " + version);
            return codec;
        }
        if (version.isEqualOrHigher(Version.V1_17)) {
            return RESOURCES.get("codec-1.17");
        }
        if (version.isEqualOrHigher(Version.V1_16_2)) {
            return RESOURCES.get("codec-1.16.2");
        }
        return RESOURCES.get("codec-1.16");
    }

    public static byte[] dimension(final Version version, final int dimension) {
        if (version.isInRange(Version.V1_18, Version.V1_18_2)) {
            return MODERN_DIMENSIONS.get(version);
        }
        final String versionKey = version.isEqualOrHigher(Version.V1_17) ? "1.17" : "1.16.2";
        final String dimensionKey = switch (dimension) {
            case -1 -> "nether";
            case 1 -> "end";
            default -> "overworld";
        };
        return RESOURCES.get("dimension-" + versionKey + '-' + dimensionKey);
    }

    public static void writeEmptyHeightmaps(final PacketBuffer buffer) {
        buffer.writeByte(10); // root compound
        buffer.writeShort(0); // empty root name
        writeLongArray(buffer, "MOTION_BLOCKING", 36);
        writeLongArray(buffer, "WORLD_SURFACE", 36);
        buffer.writeByte(0); // end compound
    }

    public static void writeEmptyHeightmaps(final PacketBuffer buffer, final Version version) {
        if (version.isEqualOrHigher(Version.V1_21_5)) {
            buffer.writeVarInt(0);
            return;
        }
        buffer.writeByte(10); // compound
        if (version.isLessThan(Version.V1_20_2)) buffer.writeShort(0); // named NBT root
        writeLongArray(buffer, "MOTION_BLOCKING", 36);
        writeLongArray(buffer, "WORLD_SURFACE", 36);
        buffer.writeByte(0);
    }

    public static void writeHeightmaps(final PacketBuffer buffer, final Version version, final long[] values) {
        if (version.isEqualOrHigher(Version.V1_21_5)) {
            buffer.writeVarInt(3);
            writeNetworkHeightmap(buffer, 5, values); // MOTION_BLOCKING_NO_LEAVES
            writeNetworkHeightmap(buffer, 1, values); // WORLD_SURFACE
            writeNetworkHeightmap(buffer, 4, values); // MOTION_BLOCKING
            return;
        }
        buffer.writeByte(10); // compound
        if (version.isLessThan(Version.V1_20_2)) buffer.writeShort(0); // named NBT root
        writeLongArray(buffer, "MOTION_BLOCKING", values);
        writeLongArray(buffer, "WORLD_SURFACE", values);
        buffer.writeByte(0);
    }

    private static void writeNetworkHeightmap(final PacketBuffer buffer, final int type, final long[] values) {
        buffer.writeVarInt(type);
        buffer.writeVarInt(values.length);
        for (final long value : values) buffer.writeLong(value);
    }

    private static void writeLongArray(final PacketBuffer buffer, final String name, final int length) {
        buffer.writeByte(12);
        final byte[] nameBytes = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        buffer.writeShort(nameBytes.length);
        buffer.writeBytes(nameBytes);
        buffer.writeInt(length);
        for (int i = 0; i < length; i++) {
            buffer.writeLong(0L);
        }
    }

    private static void writeLongArray(final PacketBuffer buffer, final String name, final long[] values) {
        buffer.writeByte(12);
        final byte[] nameBytes = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        buffer.writeShort(nameBytes.length);
        buffer.writeBytes(nameBytes);
        buffer.writeInt(values.length);
        for (final long value : values) buffer.writeLong(value);
    }

    private static byte[] load(final String fileName) {
        try (InputStream stream = ProtocolNbt.class.getResourceAsStream("/protocol/" + fileName)) {
            if (stream == null) {
                throw new IllegalStateException("Missing protocol NBT resource " + fileName);
            }
            return stream.readAllBytes();
        } catch (final IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static Map<Version, byte[]> loadModernCodecs() {
        final Map<Version, byte[]> result = new EnumMap<>(Version.class);
        for (final Version version : Version.getVersionsRange(Version.V1_18, Version.V1_20)) {
            result.put(version, load("dimension-codec-" + version.getProtocolNumber() + ".nbt"));
        }
        return Map.copyOf(result);
    }

    private static Map<Version, byte[]> loadModernDimensions() {
        final Map<Version, byte[]> result = new EnumMap<>(Version.class);
        for (final Version version : Version.getVersionsRange(Version.V1_18, Version.V1_18_2)) {
            result.put(version, load("dimension-" + version.getProtocolNumber() + "-overworld.nbt"));
        }
        return Map.copyOf(result);
    }
}
