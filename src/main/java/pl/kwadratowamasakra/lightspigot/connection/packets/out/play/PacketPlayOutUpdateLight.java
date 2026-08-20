package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutUpdateLight extends PacketOut {

    private int chunkX;
    private int chunkZ;
    private int primaryBitMask;
    private byte[] legacyChunkData;

    private static void writeLongArray(final PacketBuffer buffer, final long value) {
        buffer.writeVarInt(1);
        buffer.writeLong(value);
    }

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer buffer) {
        final Version version = connection.getVersion();
        final LegacyChunkData data = LegacyChunkData.decode(primaryBitMask, legacyChunkData);
        if (version.isEqualOrHigher(Version.V1_18)) {
            buffer.writeVarInt(chunkX);
            buffer.writeVarInt(chunkZ);
            PacketPlayOutChunkData.writeModernLight(buffer, version, data);
            return;
        }
        final long lightMask = (primaryBitMask & 0xFFFFL) << 1;
        final long emptyMask = ~lightMask & 0x3FFFFL;

        buffer.writeVarInt(chunkX);
        buffer.writeVarInt(chunkZ);
        if (version.isEqualOrHigher(Version.V1_16)) {
            buffer.writeBoolean(true); // trust edges
        }
        if (version.isEqualOrHigher(Version.V1_17)) {
            writeLongArray(buffer, lightMask);
            writeLongArray(buffer, lightMask);
            writeLongArray(buffer, emptyMask);
            writeLongArray(buffer, emptyMask);
            buffer.writeVarInt(data.skyLight.length);
            for (final byte[] light : data.skyLight) buffer.writeBytesArray(light);
            buffer.writeVarInt(data.blockLight.length);
            for (final byte[] light : data.blockLight) buffer.writeBytesArray(light);
        } else {
            buffer.writeVarInt((int) lightMask);
            buffer.writeVarInt((int) lightMask);
            buffer.writeVarInt((int) emptyMask);
            buffer.writeVarInt((int) emptyMask);
            for (final byte[] light : data.skyLight) buffer.writeBytesArray(light);
            for (final byte[] light : data.blockLight) buffer.writeBytesArray(light);
        }
    }
}
