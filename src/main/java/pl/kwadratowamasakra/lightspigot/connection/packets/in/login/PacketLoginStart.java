package pl.kwadratowamasakra.lightspigot.connection.packets.in.login;

import io.netty.channel.ChannelFutureListener;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketEnableCompression;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketLoginSuccess;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketJoinGame;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayerAbilitiesOut;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayerPosLook;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.GameProfile;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.Location;
import pl.kwadratowamasakra.lightspigot.event.PlayerLoginEvent;
import pl.kwadratowamasakra.lightspigot.event.PlayerPreLoginEvent;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;
import pl.kwadratowamasakra.lightspigot.utils.ConsoleColors;
import pl.kwadratowamasakra.lightspigot.utils.UUIDUtil;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PacketLoginStart extends PacketIn {

    private String username;
    private UUID uuid;

    @Override
    public final void read(final PacketBuffer packetBuffer) {
        username = packetBuffer.readString();
        uuid = UUIDUtil.getOfflineModeUUID(username);
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.LOGIN);
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("NULL/BLANK Name");
        }
        if (username.length() > 16 || username.length() < 3) {
            connection.disconnect(ChatUtil.fixColor(server.getConfig().getBadNameLength()));
            return;
        }
        final String invalidChars = ChatUtil.getInvalidChars(username);
        if (!invalidChars.isEmpty()) {
            connection.disconnect(ChatUtil.fixColor(server.getConfig().getBadNameChars()) + invalidChars);
            return;
        }
        final String usernameLower = username.toLowerCase();
        if (usernameLower.contains("bot")) {
            connection.disconnect(ChatUtil.fixColor(server.getConfig().getBadName()));
            return;
        }
        if (server.getConfig().getBlockedNames().contains(usernameLower)) {
            connection.closeConnection();
            return;
        }
        if (server.getConnectionManager().isPlayerOnline(username)) {
            connection.disconnect(ChatUtil.fixColor(server.getConfig().getPlayerOnServer()));
            return;
        }
        connection.setGameProfile(new GameProfile(UUIDUtil.getOfflineModeUUID(username), username));
        final PlayerPreLoginEvent e = new PlayerPreLoginEvent(connection, true, null);
        server.getEventManager().handleEvent(e);

        if (!e.isAccepted()) {
            connection.disconnect(e.getReason());
            return;
        }
        server.getLogger().connection(ConsoleColors.CYAN + "PacketLoginStart" + ConsoleColors.RESET + " username: " + username + " uuid: " + uuid);

        if (server.getConfig().getNetworkThreshold() > 0) {
            connection.sendPacket(new PacketEnableCompression(server.getConfig().getNetworkThreshold()), (ChannelFutureListener) future -> connection.setCompressionThreshold(server.getConfig().getNetworkThreshold()));
        }

        connection.sendPacket(new PacketLoginSuccess(uuid, username));
        connection.setConnectionState(ConnectionState.PLAY);

        connection.writePacket(new PacketJoinGame(ThreadLocalRandom.current().nextInt(), server.getConfig().getDefaultGamemode(), server.getConfig().getDefaultDimension(), 0, server.getConnectionManager().getMaxPlayers(), "flat", true));
        connection.writePacket(new PacketPlayerAbilitiesOut(0x02, 0.0F, 0.1F));

        final PlayerLoginEvent event = new PlayerLoginEvent(connection, new Location(0.0, 100.0, 0.0, 0.0f, 0.0f));
        server.getEventManager().handleEvent(event);

        connection.writePacket(new PacketPlayerPosLook(event.getLocation().getX(), event.getLocation().getY(), event.getLocation().getZ(), event.getLocation().getYaw(), event.getLocation().getPitch()));

        connection.sendKeepAlive();
        server.getConnectionManager().addConnection(connection);
    }

    @Override
    public final int getLimit() {
        return 3;
    }

}
