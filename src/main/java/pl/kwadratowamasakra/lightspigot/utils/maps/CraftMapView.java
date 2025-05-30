package pl.kwadratowamasakra.lightspigot.utils.maps;

import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutMap;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

import java.util.*;

public final class CraftMapView implements MapView {

    private final List<MapRenderer> renderers = new ArrayList<>();
    private final Map<MapRenderer, CraftMapCanvas> canvases = new HashMap<>();
    private Scale scale = Scale.NORMAL;

    @Override
    public short getId() {
        return 1;
    }

    @Override
    public Scale getScale() {
        return scale;
    }

    @Override
    public void setScale(final Scale scale) {
        this.scale = scale;
    }

    @Override
    public List<MapRenderer> getRenderers() {
        return new ArrayList<>(renderers);
    }

    @Override
    public void addRenderer(final MapRenderer renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            canvases.put(renderer, null);
            renderer.initialize(this);
        }
    }

    @Override
    public boolean removeRenderer(final MapRenderer renderer) {
        if (renderers.contains(renderer)) {
            renderers.remove(renderer);
            final CraftMapCanvas entry = canvases.get(renderer);
            for (int x = 0; x < 128; ++x) {
                for (int y = 0; y < 128; ++y) {
                    entry.setPixel(x, y, (byte) -1);
                }
            }
            canvases.remove(renderer);
            return true;
        } else {
            return false;
        }
    }

    public RenderData render() {
        final RenderData render = new RenderData();

        Arrays.fill(render.buffer, (byte) 0);
        render.cursors.clear();

        for (final MapRenderer renderer : renderers) {
            CraftMapCanvas canvas = canvases.get(renderer);
            if (canvas == null) {
                canvas = new CraftMapCanvas(this);
                canvases.replace(renderer, canvas);
            }

            canvas.setBase(render.buffer);
            renderer.render(this, canvas);

            final byte[] buf = canvas.getBuffer();
            final int len = buf.length;
            for (int i = 0; i < len; ++i) {
                final byte color = buf[i];
                // There are 143 valid color id's, 0 -> 127 and -128 -> -113
                if (color >= 0 || color <= -113) render.buffer[i] = color;
            }

            for (int i = 0; i < canvas.getCursors().size(); ++i) {
                render.cursors.add(canvas.getCursors().getCursor(i));
            }
        }

        return render;
    }

    public void sendMap(final PlayerConnection connection) {
        final RenderData data = render();
        final Collection<MapIcon> icons = new ArrayList<>();
        for (final MapCursor cursor : data.cursors) {
            if (cursor.isVisible()) {
                icons.add(new MapIcon(cursor.getRawType(), cursor.getX(), cursor.getY(), cursor.getDirection()));
            }
        }

        final PacketPlayOutMap packet = new PacketPlayOutMap(getId(), getScale().getValue(), false, icons, data.buffer, 0, 0, 128, 128);
        connection.sendPacket(packet);
    }

}
