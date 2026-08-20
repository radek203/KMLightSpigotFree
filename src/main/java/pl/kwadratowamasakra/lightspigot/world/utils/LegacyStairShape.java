package pl.kwadratowamasakra.lightspigot.world.utils;

import java.util.Set;

/**
 * Resolves the explicit stair shape required by flattened (1.13+) block states.
 */
public final class LegacyStairShape {

    private static final Set<Integer> STAIRS = Set.of(
            53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180
    );

    private LegacyStairShape() {
    }

    public static boolean isStairs(final int legacyBlockValue) {
        return STAIRS.contains(legacyBlockValue >>> 4);
    }

    public static Shape resolve(final BlockGetter blocks, final int x, final int y, final int z) {
        final int stairs = blocks.get(x, y, z);
        if (!isStairs(stairs)) return Shape.STRAIGHT;

        final Direction facing = Direction.fromLegacyMeta(stairs & 0x03);
        final int front = blocks.get(x + facing.x, y, z + facing.z);
        if (isCompatible(stairs, front)) {
            final Direction frontFacing = Direction.fromLegacyMeta(front & 0x03);
            if (frontFacing.axis != facing.axis
                    && canTakeShape(blocks, stairs, facing, x, y, z, frontFacing.opposite())) {
                return frontFacing == facing.counterClockwise()
                        ? Shape.OUTER_LEFT : Shape.OUTER_RIGHT;
            }
        }

        final int back = blocks.get(x - facing.x, y, z - facing.z);
        if (isCompatible(stairs, back)) {
            final Direction backFacing = Direction.fromLegacyMeta(back & 0x03);
            if (backFacing.axis != facing.axis
                    && canTakeShape(blocks, stairs, facing, x, y, z, backFacing)) {
                return backFacing == facing.counterClockwise()
                        ? Shape.INNER_LEFT : Shape.INNER_RIGHT;
            }
        }
        return Shape.STRAIGHT;
    }

    private static boolean isCompatible(final int stairs, final int neighbour) {
        return isStairs(neighbour) && (stairs & 0x04) == (neighbour & 0x04);
    }

    private static boolean canTakeShape(final BlockGetter blocks, final int stairs,
                                        final Direction facing, final int x, final int y, final int z,
                                        final Direction side) {
        final int neighbour = blocks.get(x + side.x, y, z + side.z);
        return !isStairs(neighbour)
                || (neighbour & 0x04) != (stairs & 0x04)
                || Direction.fromLegacyMeta(neighbour & 0x03) != facing;
    }

    public enum Shape {
        STRAIGHT,
        INNER_LEFT,
        INNER_RIGHT,
        OUTER_LEFT,
        OUTER_RIGHT
    }

    private enum Axis {X, Z}

    private enum Direction {
        NORTH(0, -1, Axis.Z),
        SOUTH(0, 1, Axis.Z),
        WEST(-1, 0, Axis.X),
        EAST(1, 0, Axis.X);

        private final int x;
        private final int z;
        private final Axis axis;

        Direction(final int x, final int z, final Axis axis) {
            this.x = x;
            this.z = z;
            this.axis = axis;
        }

        private static Direction fromLegacyMeta(final int meta) {
            return switch (meta & 0x03) {
                case 0 -> EAST;
                case 1 -> WEST;
                case 2 -> SOUTH;
                default -> NORTH;
            };
        }

        private Direction opposite() {
            return switch (this) {
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
                case WEST -> EAST;
                case EAST -> WEST;
            };
        }

        private Direction counterClockwise() {
            return switch (this) {
                case NORTH -> WEST;
                case WEST -> SOUTH;
                case SOUTH -> EAST;
                case EAST -> NORTH;
            };
        }
    }

    @FunctionalInterface
    public interface BlockGetter {
        int get(int x, int y, int z);
    }
}
