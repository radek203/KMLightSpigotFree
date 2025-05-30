package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutLogin extends PacketOut {

    private int entityId;
    private int gameMode;
    private int dimension;
    private int difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reducedDebugInfo;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeInt(entityId);
        packetBuffer.writeByte(gameMode);
        if (connection.getVersion().isInRange(Version.V1_8, Version.V1_9)) {
            packetBuffer.writeByte(dimension);
        } else {
            packetBuffer.writeInt(dimension);
        }
        packetBuffer.writeByte(difficulty);
        packetBuffer.writeByte(maxPlayers);
        packetBuffer.writeString(levelType);
        packetBuffer.writeBoolean(reducedDebugInfo);
    }

}
