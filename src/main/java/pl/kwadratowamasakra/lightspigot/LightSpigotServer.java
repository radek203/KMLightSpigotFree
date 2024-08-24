package pl.kwadratowamasakra.lightspigot;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import pl.kwadratowamasakra.lightspigot.command.CommandManager;
import pl.kwadratowamasakra.lightspigot.config.ServerConfig;
import pl.kwadratowamasakra.lightspigot.connection.ClientChannelInitializer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionManager;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketManager;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.EventManager;
import pl.kwadratowamasakra.lightspigot.plugin.PluginManager;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;
import pl.kwadratowamasakra.lightspigot.utils.ConsoleColors;
import pl.kwadratowamasakra.lightspigot.utils.ServerLogger;

import java.net.InetSocketAddress;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The LightSpigotServer class represents the main server class for the LightSpigot server.
 * It includes methods to start and stop the server, handle console commands, and manage connections, packets, commands, events, and plugins.
 */
public class LightSpigotServer {

    private final ConnectionManager connectionManager;
    private final PacketManager packetManager = new PacketManager();
    private final CommandManager commandManager = new CommandManager();
    private final EventManager eventManager = new EventManager();
    private final PluginManager pluginManager = new PluginManager();

    private ServerLogger logger;
    private ServerConfig config;
    private Timer keepAliveTask;
    private ConsoleHandler console;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private boolean closing = false;

    /**
     * Constructs a new LightSpigotServer.
     * The connection manager is initialized, and the server configuration is loaded.
     * If the configuration fails to load, the server is not started.
     */
    LightSpigotServer() {
        connectionManager = new ConnectionManager(this);

        try {
            config = new ServerConfig(this);
        } catch (final RuntimeException e) {
            e.printStackTrace();
            return;
        }
        logger = new ServerLogger(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSS"), ZoneId.of("Europe/Warsaw"), config.isDebugOn());
        logger.startLoggerThread();

        packetManager.registerPackets(this);
    }

    /**
     * Starts the server.
     * The server bootstrap is started, the keep-alive task is scheduled, plugins are loaded, and the console thread is started.
     */
    final void start() {
        startBootstrap();

        connectionManager.addOps(config.getOps());

        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                connectionManager.broadcastKeepAlive();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0L, config.getKeepAliveBroadcast());
        keepAliveTask = timer;

        pluginManager.loadPlugins(this);

        console = new ConsoleHandler(this);
        console.startConsoleThread();

        logger.info(ConsoleColors.GREEN_BRIGHT + "The server has started!" + ConsoleColors.RESET);
    }

    /**
     * Starts the server bootstrap.
     * The appropriate event loop groups and channel class are selected based on whether Epoll is available, and the server bootstrap is configured and bound to the server's hostname and port.
     */
    private void startBootstrap() {
        final Class<? extends ServerChannel> channelClass;

        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(4);
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(4);
            channelClass = NioServerSocketChannel.class;
        }

        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(channelClass)
                .childHandler(new ClientChannelInitializer(this, packetManager))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(new InetSocketAddress(config.getHostname(), config.getPort()))
                .bind();
    }

    /**
     * Stops the server.
     * The console thread is stopped, plugins are disabled, all player connections are disconnected, the keep-alive task is cancelled, the event loop groups are shut down gracefully, and the server logger is stopped.
     */
    public final void stop() {
        setClosing(true);
        console.stopThread();
        pluginManager.disablePlugins();
        logger.info(ConsoleColors.GREEN_BRIGHT + "Stopping server..." + ConsoleColors.RESET);

        final String reason = ChatUtil.fixColor(config.getRestart());
        for (final PlayerConnection connection : connectionManager.getConnections()) {
            connection.disconnect(reason);
        }

        keepAliveTask.cancel();

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        logger.info(ConsoleColors.GREEN_BRIGHT + "Server stopped!" + ConsoleColors.RESET);
        logger.stop();
    }

    /**
     * @return The connection manager.
     */
    public final ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * @return The event manager.
     */
    public final EventManager getEventManager() {
        return eventManager;
    }

    /**
     * @return The command manager.
     */
    public final CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return The plugin manager.
     */
    public final PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * @return The server logger.
     */
    public final ServerLogger getLogger() {
        return logger;
    }

    /**
     * @return The server configuration.
     */
    public final ServerConfig getConfig() {
        return config;
    }

    /**
     * Returns whether the server is in closing state.
     *
     * @return True if the server is closing, false otherwise.
     */
    public final boolean isClosing() {
        return closing;
    }

    /**
     * Sets whether the server is closing.
     *
     * @param closing True if the server is closing, false otherwise.
     */
    private void setClosing(final boolean closing) {
        this.closing = closing;
    }

}
