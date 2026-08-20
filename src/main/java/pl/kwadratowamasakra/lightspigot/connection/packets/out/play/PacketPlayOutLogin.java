package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolNbt;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutLogin extends PacketOut {

    private int entityId;
    //private boolean hardcore;
    private int gameMode;
    private int dimension;
    private int difficulty;
    private int maxPlayers;
    private String levelType;
    private int viewDistance;
    private boolean reducedDebugInfo;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        final Version version = connection.getVersion();
        packetBuffer.writeInt(entityId);

        /*
        if (hardcore) {
            gameMode |= 8;
        }
         */

        if (version.isEqualOrHigher(Version.V1_16_2)) {
            packetBuffer.writeBoolean(false); // hardcore
        }
        if (version.isEqualOrHigher(Version.V1_20_2)) {
            writeConfigurationEraLogin(packetBuffer, version);
            return;
        }
        packetBuffer.writeByte(gameMode);
        if (version.isEqualOrHigher(Version.V1_16)) {
            packetBuffer.writeByte(-1); // previous game mode
            packetBuffer.writeVarInt(1);
            packetBuffer.writeString(dimensionName());
            packetBuffer.writeBytes(ProtocolNbt.dimensionCodec(version));
            if (version.isInRange(Version.V1_16_2, Version.V1_18_2)) {
                packetBuffer.writeBytes(ProtocolNbt.dimension(version, dimension));
            } else if (version.isEqualOrHigher(Version.V1_19)) {
                packetBuffer.writeString(dimensionName());
            } else {
                packetBuffer.writeString(dimensionName());
            }
            packetBuffer.writeString(dimensionName());
            packetBuffer.writeLong(0L); // hashed seed
            if (version.isEqualOrHigher(Version.V1_16_2)) {
                packetBuffer.writeVarInt(maxPlayers);
            } else {
                packetBuffer.writeByte(maxPlayers);
            }
            packetBuffer.writeVarInt(viewDistance);
            if (version.isEqualOrHigher(Version.V1_18)) {
                packetBuffer.writeVarInt(10); // simulation distance
            }
            packetBuffer.writeBoolean(reducedDebugInfo);
            packetBuffer.writeBoolean(true); // enable respawn screen
            packetBuffer.writeBoolean(false); // debug world
            packetBuffer.writeBoolean("flat".equalsIgnoreCase(levelType));
            if (version.isEqualOrHigher(Version.V1_19)) {
                packetBuffer.writeBoolean(false); // last death location
            }
            if (version.isEqualOrHigher(Version.V1_20)) {
                packetBuffer.writeVarInt(0); // portal cooldown
            }
            return;
        }
        if (connection.getVersion().isInRange(Version.V1_8, Version.V1_9)) {
            packetBuffer.writeByte(dimension);
        } else {
            packetBuffer.writeInt(dimension);
        }
        if (connection.getVersion().isLessThan(Version.V1_14)) {
            packetBuffer.writeByte(difficulty);
        }
        if (connection.getVersion().isEqualOrHigher(Version.V1_15)) {
            packetBuffer.writeLong(0L); // hashed seed
        }
        packetBuffer.writeByte(maxPlayers);
        packetBuffer.writeString(levelType);
        if (connection.getVersion().isEqualOrHigher(Version.V1_14)) {
            packetBuffer.writeVarInt(viewDistance);
        }
        packetBuffer.writeBoolean(reducedDebugInfo);
        if (connection.getVersion().isEqualOrHigher(Version.V1_15)) {
            packetBuffer.writeBoolean(true); // enable respawn screen
        }
    }

    private void writeConfigurationEraLogin(final PacketBuffer buffer, final Version version) {
        buffer.writeVarInt(1);
        buffer.writeString(dimensionName());
        buffer.writeVarInt(maxPlayers);
        buffer.writeVarInt(viewDistance);
        buffer.writeVarInt(10); // simulation distance
        buffer.writeBoolean(reducedDebugInfo);
        buffer.writeBoolean(true); // enable respawn screen
        buffer.writeBoolean(false); // limited crafting
        if (version.isLessThan(Version.V1_20_5)) {
            buffer.writeString(dimensionName());
            buffer.writeString(dimensionName());
            buffer.writeLong(0L);
            buffer.writeByte(gameMode);
            buffer.writeByte(-1);
            buffer.writeBoolean(false);
            buffer.writeBoolean("flat".equalsIgnoreCase(levelType));
            buffer.writeBoolean(false); // last death location
            buffer.writeVarInt(0); // portal cooldown
            return;
        }
        buffer.writeVarInt(dimensionIndex());
        buffer.writeString(dimensionName());
        buffer.writeLong(0L);
        buffer.writeByte(gameMode);
        buffer.writeByte(-1);
        buffer.writeBoolean(false);
        buffer.writeBoolean("flat".equalsIgnoreCase(levelType));
        buffer.writeBoolean(false); // last death location
        buffer.writeVarInt(0); // portal cooldown
        if (version.isEqualOrHigher(Version.V1_21_2)) {
            buffer.writeVarInt(63); // sea level
        }
        if (version.isEqualOrHigher(Version.V26_2)) {
            buffer.writeBoolean(false); // online mode
        }
        buffer.writeBoolean(false); // enforce secure chat
    }

    private int dimensionIndex() {
        return switch (dimension) {
            case -1 -> 2;
            case 1 -> 1;
            default -> 0;
        };
    }

    private String dimensionName() {
        return switch (dimension) {
            case -1 -> "minecraft:the_nether";
            case 1 -> "minecraft:the_end";
            default -> "minecraft:overworld";
        };
    }

}
