import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.utils.UUIDUtil;

import java.util.UUID;

@SuppressWarnings("ConstantValue")
class UUIDUtilTest {

    @Test
    final void offlineModeUUIDShouldReturnCorrectUUID() {
        final UUID result = UUIDUtil.getOfflineModeUUID("radek203");
        Assertions.assertEquals(UUID.fromString("e1cfc672-d100-366e-a08e-88ea7f85b69b"), result, "Offline mode UUID from name generation error");
    }

    @Test
    final void offlineModeUUIDShouldReturnNullForBlankUsername() {
        final UUID result = UUIDUtil.getOfflineModeUUID("");
        Assertions.assertNull(result, "Offline mode UUID from empty name generation error");
    }

    @Test
    final void offlineModeUUIDShouldReturnNullForNullUsername() {
        final UUID result = UUIDUtil.getOfflineModeUUID(null);
        Assertions.assertNull(result, "Offline mode UUID from null name generation error");
    }

    @Test
    final void fromStringShouldReturnCorrectUUID() {
        final UUID result = UUIDUtil.fromString("e1cfc672-d100-366e-a08e-88ea7f85b69b");
        Assertions.assertEquals(UUID.fromString("e1cfc672-d100-366e-a08e-88ea7f85b69b"), result, "UUID string parsing error");
    }

    @Test
    final void fromStringShouldReturnCorrectUUIDForStringWithoutDashes() {
        final UUID result = UUIDUtil.fromString("e1cfc672d100366ea08e88ea7f85b69b");
        Assertions.assertEquals(UUID.fromString("e1cfc672-d100-366e-a08e-88ea7f85b69b"), result, "UUID string without dashes parsing error");
    }

    @Test
    final void fromStringShouldReturnNullForBlankString() {
        final UUID result = UUIDUtil.fromString("");
        Assertions.assertNull(result, "UUID from empty name parsing error");
    }

    @Test
    final void fromStringShouldReturnNullForNullString() {
        final UUID result = UUIDUtil.fromString(null);
        Assertions.assertNull(result, "UUID from null name parsing error");
    }

    @Test
    final void fromStringShouldReturnNullForInvalidUUID() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> UUIDUtil.fromString("invalid-uuid"), "UUID parsing error");
    }

}
