import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.Version;

class VersionTest {

    @Test
    void protocolNumbersShouldResolveToExactVersions() {
        Assertions.assertEquals(Version.V1_8, Version.of(47));
        Assertions.assertEquals(Version.V1_12_2, Version.of(340));
        Assertions.assertEquals(Version.UNDEFINED, Version.of(999_999));
    }

    @Test
    void rangesShouldBeInclusiveAndOrderIndependent() {
        Assertions.assertArrayEquals(
                new Version[]{Version.V1_9, Version.V1_9_1, Version.V1_9_2, Version.V1_9_3, Version.V1_10},
                Version.getVersionsRange(Version.V1_9, Version.V1_10));
        Assertions.assertArrayEquals(
                Version.getVersionsRange(Version.V1_9, Version.V1_10),
                Version.getVersionsRange(Version.V1_10, Version.V1_9));
    }

    @Test
    void comparisonHelpersShouldRespectProtocolNumbers() {
        Assertions.assertTrue(Version.V1_12_2.isEqualOrHigher(Version.V1_8));
        Assertions.assertTrue(Version.V1_8.isLessThan(Version.V1_9));
        Assertions.assertTrue(Version.V1_10.isInRange(Version.V1_9, Version.V1_11));
        Assertions.assertFalse(Version.V1_12.isInRange(Version.V1_8, Version.V1_11));
    }
}
