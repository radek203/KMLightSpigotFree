package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.PlayerChatEvent;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;
import pl.kwadratowamasakra.lightspigot.utils.ConsoleColors;

public class PacketPlayInChat extends PacketIn {

    private String message;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        message = packetBuffer.readString(Math.min(packetBuffer.readVarInt(), 100));
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
        if (message == null || message.isBlank() || message.length() > 100) {
            throw new IllegalArgumentException("NULL/BLANK, Too length message");
        }
        final boolean isCommand = message.charAt(0) == '/';
        if (isCommand) {
            server.getCommandManager().handleCommand(server, message, connection);
            return;
        }

        final PlayerChatEvent e = new PlayerChatEvent(connection, server.getConfig().isChatOn(), ChatUtil.fixColor("&7" + connection.getName() + " &6>> &7") + message, ChatUtil.fixColor(server.getConfig().getChatMessageOff()));
        server.getEventManager().handleEvent(e);

        if (e.isAccepted()) {
            server.getLogger().info(ConsoleColors.WHITE_BOLD_BRIGHT + "[CHAT] " + ConsoleColors.RESET + e.getMessage());
            server.getConnectionManager().broadcastMessage(e.getMessage());
        } else {
            connection.sendMessage(e.getReason());
        }
    }

    @Override
    public final int getLimit() {
        return 3;
    }

}
