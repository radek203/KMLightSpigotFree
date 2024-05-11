import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;

class ChatUtilTest {

    @Test
    final void fixColorShouldTranslateColorCodes() {
        final String input = "&4Hello &6World";
        final String expected = "§4Hello §6World";
        Assertions.assertEquals(expected, ChatUtil.fixColor(input), "Color codes translation error");
    }

    @Test
    final void fixColorShouldIgnoreInvalidColorCodes() {
        final String input = "&zHello World";
        final String expected = "&zHello World";
        Assertions.assertEquals(expected, ChatUtil.fixColor(input), "Invalid color codes translation error");
    }

    @Test
    final void fixColorShouldHandleEmptyString() {
        final String input = "";
        final String expected = "";
        Assertions.assertEquals(expected, ChatUtil.fixColor(input), "Empty string translation error");
    }

    @Test
    final void getInvalidCharsShouldReturnInvalidCharacters() {
        final String input = "Hello_World!";
        final String expected = "!";
        Assertions.assertEquals(expected, ChatUtil.getInvalidChars(input), "Invalid characters extraction error");
    }

    @Test
    final void getInvalidCharsShouldReturnEmptyStringForValidInput() {
        final String input = "HelloWorld123";
        final String expected = "";
        Assertions.assertEquals(expected, ChatUtil.getInvalidChars(input), "Invalid characters extraction error");
    }

    @Test
    final void getInvalidCharsShouldHandleEmptyString() {
        final String input = "";
        final String expected = "";
        Assertions.assertEquals(expected, ChatUtil.getInvalidChars(input), "Empty string extraction error");
    }

}
