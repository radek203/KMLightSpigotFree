package pl.kwadratowamasakra.lightspigot.connection.packets.in.login;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutGameEvent;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutLogin;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutPosition;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutUpdateViewPosition;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.Location;
import pl.kwadratowamasakra.lightspigot.event.PlayerLoginEvent;
import pl.kwadratowamasakra.lightspigot.world.World;

import java.util.concurrent.ThreadLocalRandom;

public final class LoginFlow {

    private LoginFlow() {
    }

    public static void enterPlay(final PlayerConnection connection, final LightSpigotServer server) {
        connection.setConnectionState(ConnectionState.PLAY);
        final World world = server.getWorld();
        connection.writePacket(new PacketPlayOutLogin(ThreadLocalRandom.current().nextInt(),
                server.getConfig().getDefaultGamemode(), world.getDimension(), world.getDifficulty(),
                server.getConnectionManager().getMaxPlayers(), world.getLevelType(),
                world.getViewDistance(), true));
        final boolean positionBeforeChunks = connection.getVersion().isEqualOrHigher(Version.V1_20_2);
        if (!positionBeforeChunks) world.sendToPlayer(connection);
        final Location spawnLocation = world.getSpawnLocation();
        final PlayerLoginEvent event = new PlayerLoginEvent(connection, new Location(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch()));
        server.getEventManager().handleEvent(event);
        connection.updatePosition(event.getLocation().getX(), event.getLocation().getY(), event.getLocation().getZ());
        connection.updateRotation(event.getLocation().getYaw(), event.getLocation().getPitch());
        connection.writePacket(new PacketPlayOutPosition(event.getLocation().getX(), event.getLocation().getY(), event.getLocation().getZ(), event.getLocation().getYaw(), event.getLocation().getPitch(), 0));
        if (positionBeforeChunks) {
            connection.writePacket(new PacketPlayOutGameEvent(
                    PacketPlayOutGameEvent.LEVEL_CHUNKS_LOAD_START, 0.0F));
            connection.writePacket(new PacketPlayOutUpdateViewPosition(
                    Math.floorDiv((int) Math.floor(event.getLocation().getX()), 16),
                    Math.floorDiv((int) Math.floor(event.getLocation().getZ()), 16)));
            world.sendToPlayer(connection);
        }
        connection.sendKeepAlive();
        server.getConnectionManager().addConnection(connection);
    }
}
