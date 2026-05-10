import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.utils.maps.*;

import java.util.concurrent.atomic.AtomicInteger;

class MapRenderingTest {

    @Test
    void rendererShouldInitializeOnceAndProducePixelsAndCursors() {
        final CraftMapView view = new CraftMapView();
        final AtomicInteger initializations = new AtomicInteger();
        final MapRenderer renderer = new MapRenderer() {
            @Override
            public void initialize(final MapView map) {
                initializations.incrementAndGet();
            }

            @Override
            public void render(final MapView map, final MapCanvas canvas) {
                canvas.setPixel(3, 4, (byte) 12);
                canvas.setPixel(-1, 0, (byte) 7);
                canvas.getCursors().addCursor(10, -10, (byte) 15, (byte) 2, true);
            }
        };

        view.addRenderer(renderer);
        view.addRenderer(renderer);
        final RenderData rendered = view.render();

        Assertions.assertEquals(1, initializations.get());
        Assertions.assertEquals(12, rendered.buffer[4 * 128 + 3]);
        Assertions.assertEquals(0, rendered.buffer[0]);
        Assertions.assertEquals(1, rendered.cursors.size());
        Assertions.assertEquals(15, rendered.cursors.getFirst().getDirection());
    }

    @Test
    void rendererCanBeRemovedBeforeItsFirstRender() {
        final CraftMapView view = new CraftMapView();
        final MapRenderer renderer = new MapRenderer() {
            @Override
            public void render(final MapView map, final MapCanvas canvas) {
            }
        };
        view.addRenderer(renderer);

        Assertions.assertTrue(view.removeRenderer(renderer));
        Assertions.assertFalse(view.removeRenderer(renderer));
        Assertions.assertTrue(view.getRenderers().isEmpty());
    }

    @Test
    void cursorShouldValidateProtocolDirectionAndTypeRanges() {
        Assertions.assertDoesNotThrow(() -> new MapCursor((byte) -128, (byte) 127, (byte) 15, (byte) 15, true));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MapCursor((byte) 0, (byte) 0, (byte) 16, (byte) 0, true));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MapCursor((byte) 0, (byte) 0, (byte) 0, (byte) -1, true));
    }

    @Test
    void fontShouldCalculateDimensionsAndRejectUnknownCharacters() {
        final MapFont font = new MapFont();
        font.setChar('A', new MapFont.CharacterSprite(2, 3,
                new boolean[]{true, false, true, true, true, false}));
        font.setChar('B', new MapFont.CharacterSprite(1, 2, new boolean[]{true, true}));

        Assertions.assertEquals(4, font.getWidth("AB"));
        Assertions.assertEquals(3, font.getHeight());
        Assertions.assertTrue(font.isValid("A\nB"));
        Assertions.assertFalse(font.isValid("AC"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> font.getWidth("AC"));
        Assertions.assertFalse(font.getChar('A').get(-1, 0));
        Assertions.assertFalse(font.getChar('A').get(0, 2));
    }
}
