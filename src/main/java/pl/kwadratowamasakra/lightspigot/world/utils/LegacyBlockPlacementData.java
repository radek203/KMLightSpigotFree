package pl.kwadratowamasakra.lightspigot.world.utils;

import java.util.Set;

public final class LegacyBlockPlacementData {

    private static final Set<Integer> SLABS = Set.of(44, 126, 182);
    private static final Set<Integer> LOGS = Set.of(17, 162);

    private LegacyBlockPlacementData() {
    }

    public static int resolve(final int blockId, final int itemData, final int clickedFace,
                              final float hitY, final float playerYaw) {
        if (LegacyStairShape.isStairs(blockId << 4)) {
            final int direction = stairsDirection(playerYaw);
            final boolean upsideDown = clickedFace == 0 || clickedFace != 1 && hitY > 0.5F;
            return direction | (upsideDown ? 0x04 : 0);
        }
        if (SLABS.contains(blockId)) {
            final boolean upperHalf = clickedFace == 0 || clickedFace != 1 && hitY > 0.5F;
            return itemData & 0x07 | (upperHalf ? 0x08 : 0);
        }
        if (LOGS.contains(blockId)) {
            final int axis = switch (clickedFace) {
                case 4, 5 -> 0x04;
                case 2, 3 -> 0x08;
                default -> 0;
            };
            return itemData & 0x03 | axis;
        }
        return itemData & 0x0F;
    }

    private static int stairsDirection(final float yaw) {
        final int horizontalDirection = Math.floorMod((int) Math.floor(yaw * 4.0F / 360.0F + 0.5F), 4);
        return switch (horizontalDirection) {
            case 0 -> 2; // south
            case 1 -> 1; // west
            case 2 -> 3; // north
            default -> 0; // east
        };
    }
}
