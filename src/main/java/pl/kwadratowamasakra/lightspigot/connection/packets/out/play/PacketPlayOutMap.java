package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.maps.MapIcon;

import java.util.Collection;

@NoArgsConstructor
public class PacketPlayOutMap extends PacketOut {

    private static final MapIcon[] A = new MapIcon[0];
    private int mapId;
    private byte scale;
    private boolean track;
    private MapIcon[] icons;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private byte[] data;

    public PacketPlayOutMap(final int mapId, final byte scale, final boolean track, final Collection<MapIcon> icons, final byte[] buffer, final int minX, final int minY, final int maxX, final int maxY) {
        this.mapId = mapId;
        this.scale = scale;
        this.track = track;
        this.icons = icons.toArray(A);
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        data = new byte[maxX * maxY];

        for (int i = 0; i < maxX; ++i) {
            for (int j = 0; j < maxY; ++j) {
                data[i + j * maxX] = buffer[minX + i + ((minY + j) << 7)];
            }
        }

    }

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(mapId);
        packetBuffer.writeByte(scale);
        if (connection.getVersion().isEqualOrHigher(Version.V1_12_2)) {
            packetBuffer.writeBoolean(track);
        }
        packetBuffer.writeVarInt(icons.length);

        for (final MapIcon icon : icons) {
            packetBuffer.writeByte((icon.getType() & 15) << 4 | icon.getRotation() & 15);
            packetBuffer.writeByte(icon.getX());
            packetBuffer.writeByte(icon.getY());
        }

        packetBuffer.writeByte(maxX);
        if (maxX > 0) {
            packetBuffer.writeByte(maxY);
            packetBuffer.writeByte(minX);
            packetBuffer.writeByte(minY);
            packetBuffer.writeVarInt(data.length);
            packetBuffer.writeBytes(data);
        }
    }
}
