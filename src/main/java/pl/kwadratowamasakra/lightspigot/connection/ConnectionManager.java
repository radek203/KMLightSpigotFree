package pl.kwadratowamasakra.lightspigot.connection;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ConnectionManager class manages all connections to the server.
 * It includes methods to add and remove connections, broadcast packets and messages, and manage suspected connections and server operators.
 */
public class ConnectionManager {

    private static final int MAX_PLAYERS = 15360;

    private final LightSpigotServer server;
    private final MultiIndexPlayerStore connections = new MultiIndexPlayerStore();
    private final Map<String, Integer> suspectedConnections = new ConcurrentHashMap<>();
    private final Set<String> serverOperators = ConcurrentHashMap.newKeySet();
    private final AtomicInteger onlinePlayers = new AtomicInteger(0);

    /**
     * Constructs a new ConnectionManager for the specified server.
     *
     * @param server The server that the manager is associated with.
     */
    public ConnectionManager(final LightSpigotServer server) {
        this.server = server;
    }

    /**
     * @return The number of players currently online.
     */
    public final int getOnlinePlayers() {
        return onlinePlayers.get();
    }

    /**
     * Returns the maximum number of players that can be online at the same time.
     * If the maximum number of players is set to -1 in the server configuration, it returns the number of online players plus one.
     *
     * @return The maximum number of players.
     */
    public final int getMaxPlayers() {
        return server.getConfig().getMaxPlayers() == -1 ? Math.min(onlinePlayers.get() + 1, MAX_PLAYERS) : server.getConfig().getMaxPlayers();
    }

    /**
     * Adds a connection to the list of connections and increments the number of online players.
     *
     * @param connection The connection to add.
     */
    public final void addConnection(final PlayerConnection connection) {
        connections.add(connection);
        onlinePlayers.incrementAndGet();
    }

    /**
     * Removes a connection from the list of connections and decrements the number of online players.
     *
     * @param connection The connection to remove.
     */
    public final void removeConnection(final PlayerConnection connection) {
        if (connections.remove(connection)) {
            onlinePlayers.decrementAndGet();
        }
    }

    /**
     * Adds a connection to the list of suspected connections.
     * If the connection's IP is suspected three times, it is added to the firewall.
     *
     * @param connection The connection to add to the list of suspected connections.
     */
    public final void addToSuspected(final PlayerConnection connection) {
        if (server.getConfig().isProxyEnabled()) {
            return;
        }
        final String ip = connection.getIp();
        suspectedConnections.putIfAbsent(ip, 0);
        final int count = suspectedConnections.get(ip) + 1;
        suspectedConnections.replace(ip, count);
        if (count == 3) {
            try {
                new ProcessBuilder("iptables -I INPUT --src " + ip + " -j DROP").start();
                server.getLogger().info("IP Address " + ip + " has been added to firewall!");
            } catch (final IOException ignored) {
            }
        }
    }

    /**
     * Broadcasts a packet to all connections.
     *
     * @param packet The packet to broadcast.
     */
    public final void broadcastPacket(final PacketOut packet) {
        connections.doActionOnAll(connection -> connection.sendPacket(packet));
    }

    /**
     * Broadcasts a message to all connections.
     *
     * @param message The message to broadcast.
     */
    public final void broadcastMessage(final String message) {
        connections.doActionOnAll(connection -> connection.sendMessage(message));
    }

    /**
     * Broadcasts an action bar message to all connections.
     *
     * @param message The action bar message to broadcast.
     */
    public final void broadcastActionBar(final String message) {
        connections.doActionOnAll(connection -> connection.sendActionBar(message));
    }

    /**
     * Broadcasts a title and subtitle to all connections.
     *
     * @param title    The title to broadcast.
     * @param subTitle The subtitle to broadcast.
     */
    public final void broadcastTitle(final String title, final String subTitle) {
        connections.doActionOnAll(connection -> connection.sendTitle(title, subTitle));
    }

    /**
     * Broadcasts a keep-alive message to all connections.
     */
    public final void broadcastKeepAlive() {
        connections.doActionOnAll(PlayerConnection::sendKeepAlive);
    }

    /**
     * Checks if a player with the specified name is online.
     *
     * @param name The name of the player to check.
     * @return True if the player is online, false otherwise.
     */
    public final boolean isPlayerOnline(final String name) {
        return connections.getByNameLowerCase(name) != null;
    }

    /**
     * Checks if a player with the specified name is online and add to connecting list if it is possible.
     *
     * @param name The name of the player to check.
     * @return TryAddResult indicating the result of the operation:
     *  - ALREADY_CONNECTED if the player is already connected,
     *  - MAX_PLAYERS_REACHED if the maximum number of players has been reached,
     *  - SUCCESS if the player was successfully added to the connecting list.
     */
    public final TryAddResult tryAddConnecting(final String name) {
        return connections.tryAddConnecting(name, Math.min(MAX_PLAYERS, server.getConfig().getMaxPlayers() == -1 ? MAX_PLAYERS : server.getConfig().getMaxPlayers()));
    }

    /**
     * @return The list of all active connections.
     */
    public final List<PlayerConnection> getConnections() {
        return connections.getAll();
    }

    /**
     * Returns the list of connections from a specified IP.
     *
     * @param ip The IP to get connections from.
     * @return The list of connections from the specified IP.
     */
    public final List<PlayerConnection> getConnectionsByIp(final String ip) {
        return connections.getByIp(ip);
    }

    /**
     * Returns the connection of a player with the specified name.
     *
     * @param name The name of the player to get the connection of.
     * @return The connection of the player, or null if the player is not online.
     */
    public final PlayerConnection getConnectionByName(final String name) {
        return connections.getByName(name);
    }

    /**
     * Adds a list of server operators.
     *
     * @param ops The list of server operators to add.
     */
    public final void addOps(final List<String> ops) {
        serverOperators.addAll(ops);
    }

    /**
     * Checks if a player with the specified name is a server operator.
     *
     * @param name The name of the player to check.
     * @return True if the player is a server operator, false otherwise.
     */
    public final boolean isOp(final String name) {
        return serverOperators.contains(name);
    }

    /**
     * Adds a player to the list of server operators.
     *
     * @param name The name of the player to add.
     */
    public final void addOp(final String name) {
        serverOperators.add(name);
    }

    /**
     * Removes a player from the list of server operators.
     *
     * @param name The name of the player to remove.
     */
    public final void removeOp(final String name) {
        serverOperators.remove(name);
    }

    public enum TryAddResult {
        ALREADY_CONNECTED,
        MAX_PLAYERS_REACHED,
        SUCCESS
    }

}
