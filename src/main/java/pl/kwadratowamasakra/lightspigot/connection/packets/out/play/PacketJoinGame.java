package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketJoinGame extends PacketOut {

    private int entityId;
    private int gameMode;
    private int dimension;
    private int difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reducedDebugInfo;

    public PacketJoinGame(final int entityId, final int gameMode, final int dimension, final int difficulty, final int maxPlayers, final String levelType, final boolean reducedDebugInfo) {
        this.entityId = entityId;
        this.gameMode = gameMode;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.levelType = levelType;
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public PacketJoinGame() {

    }

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
