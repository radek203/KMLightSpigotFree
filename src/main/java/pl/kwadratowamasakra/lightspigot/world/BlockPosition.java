package pl.kwadratowamasakra.lightspigot.world;

import pl.kwadratowamasakra.lightspigot.connection.Version;

public record BlockPosition(int x, int y, int z) {

    public static BlockPosition fromLong(final long packedPosition) {
        final int x = (int) (packedPosition >> 38);
        final int y = (int) (packedPosition << 26 >> 52);
        final int z = (int) (packedPosition << 38 >> 38);
        return new BlockPosition(x, y, z);
    }

    public static BlockPosition fromLong(final long packedPosition, final Version version) {
        if (version.isLessThan(Version.V1_14)) {
            return fromLong(packedPosition);
        }
        final int x = (int) (packedPosition >> 38);
        final int z = (int) (packedPosition << 26 >> 38);
        final int y = (int) (packedPosition << 52 >> 52);
        return new BlockPosition(x, y, z);
    }

    public long toLong() {
        return ((long) x & 0x3FFFFFFL) << 38
                | ((long) y & 0xFFFL) << 26
                | (long) z & 0x3FFFFFFL;
    }

    public long toLong(final Version version) {
        if (version.isLessThan(Version.V1_14)) {
            return toLong();
        }
        return ((long) x & 0x3FFFFFFL) << 38
                | ((long) z & 0x3FFFFFFL) << 12
                | (long) y & 0xFFFL;
    }

    public BlockPosition relative(final int face) {
        return switch (face) {
            case 0 -> new BlockPosition(x, y - 1, z);
            case 1 -> new BlockPosition(x, y + 1, z);
            case 2 -> new BlockPosition(x, y, z - 1);
            case 3 -> new BlockPosition(x, y, z + 1);
            case 4 -> new BlockPosition(x - 1, y, z);
            case 5 -> new BlockPosition(x + 1, y, z);
            default -> throw new IllegalArgumentException("Invalid block face: " + face);
        };
    }
}
