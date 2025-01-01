package pl.kwadratowamasakra.lightspigot.utils.maps;

import java.awt.*;
import java.util.Arrays;

public class CraftMapCanvas implements MapCanvas {

    private final byte[] buffer = new byte[(128 << 7)];
    private final CraftMapView mapView;
    private byte[] base;
    private MapCursorCollection cursors = new MapCursorCollection();

    protected CraftMapCanvas(final CraftMapView mapView) {
        this.mapView = mapView;
        Arrays.fill(buffer, (byte) -1);
    }

    public final CraftMapView getMapView() {
        return mapView;
    }

    public final MapCursorCollection getCursors() {
        return cursors;
    }

    public final void setCursors(final MapCursorCollection cursors) {
        this.cursors = cursors;
    }

    public final void setPixel(final int x, final int y, final byte color) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128)
            return;
        if (buffer[(y << 7) + x] != color) {
            buffer[(y << 7) + x] = color;
        }
    }

    public final byte getPixel(final int x, final int y) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128)
            return 0;
        return buffer[(y << 7) + x];
    }

    public final byte getBasePixel(final int x, final int y) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128)
            return 0;
        return base[(y << 7) + x];
    }

    protected final void setBase(final byte[] base) {
        this.base = base;
    }

    protected final byte[] getBuffer() {
        return buffer;
    }

    public final void drawImage(final int x, final int y, final Image image) {
        final byte[] bytes = MapPalette.imageToBytes(image);
        for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
            for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
                setPixel(x + x2, y + y2, bytes[y2 * image.getWidth(null) + x2]);
            }
        }
    }

    public final void drawText(int x, int y, final MapFont font, final String text) {
        final int xStart = x;
        byte color = MapPalette.DARK_GRAY;
        if (!font.isValid(text)) {
            throw new IllegalArgumentException("text contains invalid characters");
        }

        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);
            if (ch == '\n') {
                x = xStart;
                y += font.getHeight() + 1;
                continue;
            } else if (ch == '§') {
                final int j = text.indexOf(';', i);
                if (j >= 0) {
                    try {
                        color = Byte.parseByte(text.substring(i + 1, j));
                        i = j;
                        continue;
                    } catch (final NumberFormatException ignored) {
                    }
                }
            }

            final MapFont.CharacterSprite sprite = font.getChar(text.charAt(i));
            for (int r = 0; r < font.getHeight(); ++r) {
                for (int c = 0; c < sprite.getWidth(); ++c) {
                    if (sprite.get(r, c)) {
                        setPixel(x + c, y + r, color);
                    }
                }
            }
            x += sprite.getWidth() + 1;
        }
    }

}
