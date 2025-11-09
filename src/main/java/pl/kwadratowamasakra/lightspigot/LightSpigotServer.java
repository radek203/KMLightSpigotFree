package pl.kwadratowamasakra.lightspigot;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import pl.kwadratowamasakra.lightspigot.command.CommandManager;
import pl.kwadratowamasakra.lightspigot.config.ServerConfigEntity;
import pl.kwadratowamasakra.lightspigot.connection.ClientChannelInitializer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionManager;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketManager;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.EventManager;
import pl.kwadratowamasakra.lightspigot.plugin.PluginManager;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;
import pl.kwadratowamasakra.lightspigot.utils.logger.ServerLogger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.util.TimeZone;
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

    private final ServerLogger logger;
    private final ServerConfigEntity config;
    private Timer keepAliveTask;
    private ConsoleHandler console;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    private boolean closing = false;

    /**
     * Constructs a new LightSpigotServer.
     * The connection manager is initialized, and the server configuration is loaded.
     * If the configuration fails to load, the server is not started.
     */
    LightSpigotServer() {
        connectionManager = new ConnectionManager(this);

        config = new ServerConfigEntity(this);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(config.getTimeZone())));
        logger = new ServerLogger(config.isDebugOn());

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

        logger.success("The server has started!");
    }

    /**
     * Starts the server bootstrap.
     * The appropriate event loop groups and channel class are selected based on whether Epoll is available, and the server bootstrap is configured and bound to the server's hostname and port.
     */
    private void startBootstrap() {
        final Class<? extends ServerChannel> channelClass;

        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(config.getBossGroupThreads());
            workerGroup = new EpollEventLoopGroup(config.getWorkerGroupThreads());
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(config.getBossGroupThreads());
            workerGroup = new NioEventLoopGroup(config.getWorkerGroupThreads());
            channelClass = NioServerSocketChannel.class;
        }

        try {
            final InetAddress address = InetAddress.getByName(config.getHostname());

            final ChannelFuture bindFuture = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(channelClass)
                    .childHandler(new ClientChannelInitializer(this, packetManager))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .localAddress(new InetSocketAddress(address, config.getPort()))
                    .bind()
                    .sync();
            serverChannel = bindFuture.channel();
        } catch (final UnknownHostException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the server.
     * The console thread is stopped, plugins are disabled, all player connections are disconnected, the keep-alive task is cancelled, the event loop groups are shut down gracefully, and the server logger is stopped.
     */
    public final void stop() {
        setClosing(true);
        console.stopThread();
        pluginManager.disablePlugins(this);
        logger.success("Stopping server...");

        final String reason = ChatUtil.fixColor(config.getRestart());
        for (final PlayerConnection connection : connectionManager.getConnections()) {
            connection.disconnect(reason);
        }

        keepAliveTask.cancel();

        serverChannel.close().syncUninterruptibly();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        logger.success("Server stopped!");
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
    public final ServerConfigEntity getConfig() {
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
