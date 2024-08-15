package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

@AllArgsConstructor
@NoArgsConstructor
public class PacketJoinGame extends PacketOut {

    private int entityId;
    private int gameMode;
    private int dimension;
    private int difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reducedDebugInfo;

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeInt(entityId);
        packetBuffer.writeByte(gameMode);
        packetBuffer.writeByte(dimension);
        packetBuffer.writeByte(difficulty);
        packetBuffer.writeByte(maxPlayers);
        packetBuffer.writeString(levelType);
        packetBuffer.writeBoolean(reducedDebugInfo);
    }

}
