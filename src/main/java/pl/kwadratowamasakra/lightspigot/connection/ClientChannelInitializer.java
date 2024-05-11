package pl.kwadratowamasakra.lightspigot.connection;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.handler.PacketDecoder;
import pl.kwadratowamasakra.lightspigot.connection.handler.PacketEncoder;
import pl.kwadratowamasakra.lightspigot.connection.handler.VarIntFrameDecoder;
import pl.kwadratowamasakra.lightspigot.connection.handler.VarIntLengthEncoder;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketManager;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

/**
 * The ClientChannelInitializer class initializes the channel for each client connection.
 * It sets up the pipeline for handling packets, including decoding and encoding packets, and handling read timeouts.
 */
public class ClientChannelInitializer extends ChannelInitializer<Channel> {

    private final LightSpigotServer server;
    private final PacketManager packetManager;

    /**
     * Constructs a new ClientChannelInitializer with the specified server and packet manager.
     *
     * @param server        The server that the initializer is associated with.
     * @param packetManager The packet manager for handling packets.
     */
    public ClientChannelInitializer(final LightSpigotServer server, final PacketManager packetManager) {
        this.server = server;
        this.packetManager = packetManager;
    }

    /**
     * Initializes the channel for a client connection.
     * The pipeline for handling packets is set up, including decoding and encoding packets, and handling read timeouts.
     * If the client's IP is not in the proxy list and proxy is enabled, the connection is closed.
     *
     * @param channel The channel to initialize.
     */
    @Override
    protected final void initChannel(final Channel channel) {
        final ChannelPipeline pipeline = channel.pipeline();

        final PlayerConnection connection = new PlayerConnection(channel, server);
        final String ip;
        try {
            ip = connection.getIp();
        } catch (final RuntimeException ignored) {
            connection.closeConnection();
            return;
        }
        if (ip == null || (server.getConfig().isProxyEnabled() && !server.getConfig().getProxyList().contains(ip))) {
            connection.closeConnection();
            return;
        }

        final PacketDecoder decoder = new PacketDecoder(server, packetManager, connection);
        final PacketEncoder encoder = new PacketEncoder(server, packetManager);

        final ChannelConfig config = channel.config();
        config.setOption(ChannelOption.TCP_NODELAY, true);
        config.setOption(ChannelOption.IP_TOS, 0x18);
        config.setAllocator(ByteBufAllocator.DEFAULT);

        pipeline.addLast("timeout", new ReadTimeoutHandler(30));
        pipeline.addLast("splitter", new VarIntFrameDecoder());
        pipeline.addLast("decoder", decoder);
        pipeline.addLast("prepender", new VarIntLengthEncoder());
        pipeline.addLast("encoder", encoder);
        pipeline.addLast("packet_handler", connection);
    }

}
