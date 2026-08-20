import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolMappings;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;
import pl.kwadratowamasakra.lightspigot.world.utils.LegacyStairShape;

class ProtocolMappingsTest {

    @Test
    void flatteningMappingsShouldCoverEveryRequestedProtocol() {
        for (final Version version : Version.getVersionsRange(Version.V1_13, Version.V26_2)) {
            Assertions.assertEquals(1, ProtocolMappings.blockState(version, 1, 0), version.name());
            final int stairsState = ProtocolMappings.blockState(version, 53, 2);
            Assertions.assertTrue(stairsState > 0, version.name());
            Assertions.assertNotEquals(53 << 4 | 2, stairsState, version.name());
            final ItemStack legacyStone = new ItemStack(1, 32, 0);
            final int modernId = ProtocolMappings.itemId(version, legacyStone);
            Assertions.assertTrue(modernId > 0, version.name());
            final ItemStack roundTrip = ProtocolMappings.legacyItem(version, modernId, 32);
            Assertions.assertEquals(1, roundTrip.getItemId(), version.name());
            Assertions.assertEquals(32, roundTrip.getCount(), version.name());
        }
    }

    @Test
    void legacyStairsShouldMapToStraightModernStates() {
        Assertions.assertEquals(1679, ProtocolMappings.blockState(Version.V1_13, 53, 2));
        Assertions.assertEquals(2905, ProtocolMappings.blockState(Version.V1_20_2, 53, 2));
    }

    @Test
    void legacyStairCornersShouldMapToExplicitModernShapes() {
        final int straight = ProtocolMappings.blockState(Version.V1_13, 53, 2);
        for (final LegacyStairShape.Shape shape : LegacyStairShape.Shape.values()) {
            Assertions.assertEquals(straight + shape.ordinal() * 2,
                    ProtocolMappings.blockState(Version.V1_13, 53, 2, shape));
        }
    }
}
