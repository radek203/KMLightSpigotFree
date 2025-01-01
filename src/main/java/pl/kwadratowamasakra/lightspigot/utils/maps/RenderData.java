package pl.kwadratowamasakra.lightspigot.utils.maps;

import java.util.ArrayList;
import java.util.List;

public class RenderData {

    public final List<MapCursor> cursors;
    public byte[] buffer;

    public RenderData() {
        buffer = new byte[(128 << 7)];
        cursors = new ArrayList<>();
    }

}
