import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.LegacyStairShape;

import java.util.HashMap;
import java.util.Map;

class LegacyStairShapeTest {

    private static final int Y = 64;
    private final Map<String, Integer> blocks = new HashMap<>();

    @Test
    void shouldResolveEveryCornerDirection() {
        stairs(0, 0, 3); // north

        stairs(-1, -1, 0); // ignored diagonal marker
        stairs(0, -1, 1); // front faces west
        Assertions.assertEquals(LegacyStairShape.Shape.OUTER_LEFT, shape());

        stairs(0, -1, 0); // front faces east
        Assertions.assertEquals(LegacyStairShape.Shape.OUTER_RIGHT, shape());

        stairs(0, -1, 3); // front no longer forms a corner
        stairs(0, 1, 1); // back faces west
        Assertions.assertEquals(LegacyStairShape.Shape.INNER_LEFT, shape());

        stairs(0, 1, 0); // back faces east
        Assertions.assertEquals(LegacyStairShape.Shape.INNER_RIGHT, shape());
    }

    @Test
    void differentHalfOrContinuationAtTheSideShouldKeepStraightShape() {
        stairs(0, 0, 3); // north
        stairs(0, -1, 1 | 0x04); // top stair cannot join a bottom stair
        Assertions.assertEquals(LegacyStairShape.Shape.STRAIGHT, shape());

        stairs(0, -1, 1);
        stairs(1, 0, 3); // a continuing row blocks the outer corner
        Assertions.assertEquals(LegacyStairShape.Shape.STRAIGHT, shape());
    }

    private LegacyStairShape.Shape shape() {
        return LegacyStairShape.resolve((x, y, z) -> blocks.getOrDefault(key(x, z), 0), 0, Y, 0);
    }

    private void stairs(final int x, final int z, final int meta) {
        blocks.put(key(x, z), BlockUtils.createLegacyBlockValue(53, meta));
    }

    private static String key(final int x, final int z) {
        return x + ":" + z;
    }
}
