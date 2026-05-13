package pl.kwadratowamasakra.lightspigot.world.utils;

import java.util.Map;

import static pl.kwadratowamasakra.lightspigot.world.World.CHUNK_SIZE;
import static pl.kwadratowamasakra.lightspigot.world.World.WORLD_MIN_Y;

public final class BlockUtils {

    private BlockUtils() {
    }

    public static int createLegacyBlockValue(final int blockId, final int blockData) {
        return blockId << 4 | blockData & 0x0F;
    }

    public static int normalizeY(final int y) {
        return y >= WORLD_MIN_Y ? y - WORLD_MIN_Y : y;
    }

    public static boolean isInsideChunk(final int coordinate) {
        return coordinate >= 0 && coordinate < CHUNK_SIZE;
    }

    public static int resolveLegacyBlockValue(final Map<?, ?> entry) {
        final Object blockValue = entry.get("block");
        if (blockValue instanceof Number number) {
            return createLegacyBlockValue(number.intValue(), MapUtils.readInt(entry, "data", 0));
        }
        return createLegacyBlockValue(MapUtils.readInt(entry, "id", 0), MapUtils.readInt(entry, "data", 0));
    }
}
