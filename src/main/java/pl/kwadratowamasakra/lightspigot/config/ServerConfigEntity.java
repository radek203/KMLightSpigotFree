package pl.kwadratowamasakra.lightspigot.config;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;

import java.util.List;

public class ServerConfigEntity implements FileProviderEntity {

    private final LightSpigotServer server;
    private final List<String> proxyList;
    private final List<String> ops;
    private final String hostname;
    private final int port;
    private final List<String> blockedNames;
    private final String badNameLength;
    private final String badNameChars;
    private final String badName;
    private final String playerOnServer;
    private final int defaultGamemode;
    private final int defaultDimension;
    private final long keepAliveBroadcast;
    private final String motdVersion;
    private final String motdDescription;
    private final boolean packetLimiterEnabled;
    private final int packetCountMax;
    private final long packetCountReset;
    private final int minVersion;
    private final int maxVersion;
    private final String unsupportedVersionMessage;
    private final int maxPlayers;
    private final String maxPlayersMessage;
    private final int networkThreshold;
    private final int bossGroupThreads;
    private final int workerGroupThreads;
    private final boolean proxyEnabled;
    private final boolean chatOn;
    private final String chatMessageOff;
    private final boolean debugOn;
    private final String timeZone;
    private final String commandNotExists;
    private final String commandNoPermission;
    private final String restart;

    /**
     * Constructor for the ServerConfig class.
     * This constructor initializes the server and loads the configuration from the configuration file.
     * If the configuration file cannot be created, a RuntimeException is thrown.
     * The configuration file is used to initialize various server settings.
     *
     * @param server The LightSpigotServer object.
     * @throws RuntimeException If the configuration file cannot be created.
     */
    public ServerConfigEntity(final LightSpigotServer server) {
        this.server = server;
        final FileHelper fileHelper = new ServerFileHelper(this);
        final Configuration configuration = createConfig(fileHelper);
        debugOn = configuration.getBoolean("debug");
        timeZone = configuration.getString("timeZone");
        hostname = configuration.getString("server.hostname");
        port = configuration.getInt("server.port");
        ops = configuration.getStringList("server.ops");

        blockedNames = configuration.getStringList("checks.blockedNames");
        badNameLength = configuration.getString("checks.badNameLength");
        badNameChars = configuration.getString("checks.badNameChars");
        badName = configuration.getString("checks.badName");

        motdVersion = configuration.getString("motd.version");
        motdDescription = configuration.getString("motd.description");

        proxyEnabled = configuration.getBoolean("network.proxy.enabled");
        proxyList = configuration.getStringList("network.proxy.ips");

        minVersion = configuration.getInt("network.version.min");
        maxVersion = configuration.getInt("network.version.max");
        unsupportedVersionMessage = configuration.getString("network.version.message");

        playerOnServer = configuration.getString("network.playerOnServer");
        packetLimiterEnabled = configuration.getBoolean("network.packets.limited");
        packetCountMax = configuration.getInt("network.packets.count");
        packetCountReset = configuration.getInt("network.packets.time");
        defaultGamemode = configuration.getInt("network.defaultGamemode");
        defaultDimension = configuration.getInt("network.defaultDimension");
        keepAliveBroadcast = configuration.getInt("network.keepAliveBroadcast");
        maxPlayers = configuration.getInt("network.maxPlayers");
        maxPlayersMessage = configuration.getString("network.maxPlayersMessage");
        networkThreshold = configuration.getInt("network.networkThreshold");
        bossGroupThreads = configuration.getInt("network.bossGroupThreads");
        workerGroupThreads = configuration.getInt("network.workerGroupThreads");
        chatOn = configuration.getBoolean("network.chat.status");
        chatMessageOff = configuration.getString("network.chat.message");
        restart = configuration.getString("network.restart");

        commandNotExists = configuration.getString("command.notExists");
        commandNoPermission = configuration.getString("command.noPermission");
    }

    public final boolean isDebugOn() {
        return debugOn;
    }

    public final String getTimeZone() {
        return timeZone;
    }

    public final String getHostname() {
        return hostname;
    }

    public final int getPort() {
        return port;
    }

    public final List<String> getBlockedNames() {
        return blockedNames;
    }

    public final String getBadNameLength() {
        return badNameLength;
    }

    public final String getBadNameChars() {
        return badNameChars;
    }

    public final String getBadName() {
        return badName;
    }

    public final String getPlayerOnServer() {
        return playerOnServer;
    }

    public final int getDefaultGamemode() {
        return defaultGamemode;
    }

    public final int getDefaultDimension() {
        return defaultDimension;
    }

    public final long getKeepAliveBroadcast() {
        return keepAliveBroadcast;
    }

    public final String getMotdVersion() {
        return motdVersion;
    }

    public final String getMotdDescription() {
        return motdDescription;
    }

    public final boolean isPacketLimiterEnabled() {
        return packetLimiterEnabled;
    }

    public final int getPacketCountMax() {
        return packetCountMax;
    }

    public final long getPacketCountReset() {
        return packetCountReset;
    }

    public final int getMinVersion() {
        return minVersion;
    }

    public final int getMaxVersion() {
        return maxVersion;
    }

    public final String getUnsupportedVersionMessage() {
        return unsupportedVersionMessage;
    }

    public final int getMaxPlayers() {
        return maxPlayers;
    }

    public final String getMaxPlayersMessage() {
        return maxPlayersMessage;
    }

    public final int getNetworkThreshold() {
        return networkThreshold;
    }

    public final int getBossGroupThreads() {
        return bossGroupThreads;
    }

    public final int getWorkerGroupThreads() {
        return workerGroupThreads;
    }

    public final boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public final List<String> getProxyList() {
        return proxyList;
    }

    public final List<String> getOps() {
        return ops;
    }

    public final boolean isChatOn() {
        return chatOn;
    }

    public final String getChatMessageOff() {
        return chatMessageOff;
    }

    public final String getRestart() {
        return restart;
    }

    public final String getCommandNotExists() {
        return commandNotExists;
    }

    public final String getCommandNoPermission() {
        return commandNoPermission;
    }

    @Override
    public final ClassLoader getClassLoader() {
        return server.getClass().getClassLoader();
    }

    @Override
    public final String getName() {
        return server.getClass().getSimpleName();
    }

    @Override
    public final String getJarPath() {
        return server.getClass().getSimpleName();
    }
}
