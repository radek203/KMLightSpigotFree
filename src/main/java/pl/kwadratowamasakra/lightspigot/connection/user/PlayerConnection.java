package pl.kwadratowamasakra.lightspigot.connection.user;

import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.command.CommandSender;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.PacketLimiter;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.handler.NettyCompressionDecoder;
import pl.kwadratowamasakra.lightspigot.connection.handler.NettyCompressionEncoder;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.login.PacketLoginOutDisconnect;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.*;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.event.PlayerQuitEvent;
import pl.kwadratowamasakra.lightspigot.utils.ConsoleColors;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The PlayerConnection class represents a player's connection to the server.
 * It includes methods for handling incoming packets, sending packets, and managing the player's game profile and permissions.
 */
public class PlayerConnection extends ChannelInboundHandlerAdapter implements CommandSender {

    private final List<Long> packetsCounter = new ArrayList<>();
    private final Map<Class<? extends PacketIn>, PacketLimiter> packetsIndividualCounter = new HashMap<>();

    private final Channel channel;
    private final LightSpigotServer server;
    private final PermissionManager permissionManager = new PermissionManager();
    private ConnectionState connectionState;
    private GameProfile gameProfile;
    private Version version = Version.UNDEFINED;

    /**
     * Constructs a new PlayerConnection with the specified channel and server.
     *
     * @param channel The channel associated with the connection.
     * @param server  The server that the connection is associated with.
     */
    public PlayerConnection(final Channel channel, final LightSpigotServer server) {
        this.channel = channel;
        this.server = server;

        connectionState = ConnectionState.HANDSHAKING;
    }

    /**
     * Called when the channel becomes inactive.
     * It removes the connection from the server and closes the connection.
     *
     * @param ctx The context of the channel handler.
     */
    @Override
    public final void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (getName() != null) {
            final PlayerQuitEvent event = new PlayerQuitEvent(this);
            server.getEventManager().handleEvent(event);

            server.getLogger().connection(ConsoleColors.CYAN + "Disconnected" + ConsoleColors.RESET + " username: " + getName() + " uuid: " + getUUID());
        }
        server.getConnectionManager().removeConnection(this);
        closeConnection();
    }

    /**
     * Called when an exception is caught.
     * It adds the connection to the list of suspected connections, closes the connection, and logs the error.
     *
     * @param ctx   The context of the channel handler.
     * @param cause The exception that was caught.
     */
    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        server.getConnectionManager().addToSuspected(this);
        closeConnection();
        server.getLogger().error("PlayerConnection username: " + getNameString() + " ip: " + getIp() + " exceptionCaught(...)", cause.getMessage());
    }

    /**
     * Called when a message is read from the channel.
     * If the message is an instance of PacketIn, it handles the packet.
     *
     * @param ctx The context of the channel handler.
     * @param msg The message that was read.
     */
    @Override
    public final void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        if (msg instanceof PacketIn) {
            ((PacketIn) msg).handle(this, server);
        }
    }

    /**
     * Sets the compression threshold for the connection.
     * It adds or updates the compression decoders and encoders in the channel pipeline.
     *
     * @param threshold The compression threshold.
     */
    public final void setCompressionThreshold(final int threshold) {
        if (channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder) channel.pipeline().get("decompress")).setCompressionThreshold(threshold);
        } else {
            channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(threshold));
        }

        if (channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder) channel.pipeline().get("decompress")).setCompressionThreshold(threshold);
        } else {
            channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(threshold));
        }
    }

    /**
     * Checks if the channel is active.
     *
     * @return True if the channel is active, false otherwise.
     */
    private boolean isConnected() {
        return channel.isActive();
    }

    /**
     * Sends a packet to the client.
     * If the channel is active, it writes and flushes the packet to the channel.
     *
     * @param packet The packet to send.
     */
    public final void sendPacket(final PacketOut packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet, channel.voidPromise());
        }
    }

    /**
     * Sends a packet to the client and adds a listener to the future of the channel.
     * If the channel is active, it writes and flushes the packet to the channel and adds the listener to the future.
     *
     * @param packet The packet to send.
     * @param future The listener to add to the future.
     */
    public final void sendPacket(final PacketOut packet, final GenericFutureListener<? extends Future<? super Void>> future) {
        if (isConnected()) {
            final ChannelFuture channelfuture = channel.writeAndFlush(packet);
            channelfuture.addListener(future);
        }
    }

    /**
     * Writes a packet to the channel.
     * If the channel is active, it writes the packet to the channel.
     *
     * @param packet The packet to write.
     */
    public final void writePacket(final PacketOut packet) {
        if (isConnected()) {
            channel.write(packet, channel.voidPromise());
        }
    }

    /**
     * Sends a packet to the client and closes the channel.
     * If the channel is active, it writes and flushes the packet to the channel and closes the channel.
     *
     * @param packet The packet to send.
     */
    public final void sendPacketAndClose(final PacketOut packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet).addListeners(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Disconnects the client with the specified reason.
     * If the reason is null, it closes the connection.
     * Otherwise, it sends a PacketDisconnect with the reason and closes the connection.
     *
     * @param reason The reason for the disconnect.
     */
    public final void disconnect(final String reason) {
        if (reason == null) {
            closeConnection();
            return;
        }
        if (connectionState == ConnectionState.PLAY) {
            sendPacketAndClose(new PacketPlayOutKickDisconnect(reason));
            return;
        }
        sendPacketAndClose(new PacketLoginOutDisconnect(reason));
    }

    /**
     * Sends a keep alive packet to the client.
     * If the connection state is PLAY, it sends a PacketKeepAliveOut with a random long.
     */
    public final void sendKeepAlive() {
        if (connectionState == ConnectionState.PLAY) {
            sendPacket(new PacketPlayOutKeepAlive((int) ThreadLocalRandom.current().nextLong()));
        }
    }

    /**
     * Closes the channel.
     */
    public final void closeConnection() {
        channel.close();
    }

    /**
     * @return The connection state of the connection.
     */
    public final ConnectionState getConnectionState() {
        return connectionState;
    }

    /**
     * Sets the connection state of the connection.
     *
     * @param connectionState The connection state to set.
     */
    public final void setConnectionState(final ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    /**
     * @return The channel associated with the connection.
     */
    public final Channel getChannel() {
        return channel;
    }

    /**
     * @return The game version that player uses
     */
    public final Version getVersion() {
        return version;
    }

    /**
     * Sets the game version that player uses
     *
     * @param version The version to set.
     */
    public final void setVersion(final Version version) {
        this.version = version;
    }

    /**
     * @return The game profile of the player.
     */
    public final GameProfile getGameProfile() {
        return gameProfile;
    }

    /**
     * Sets the game profile of the player.
     *
     * @param gameProfile The game profile to set.
     */
    public final void setGameProfile(final GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    /**
     * @return The permission manager of the player.
     */
    public final PermissionManager getPermissionManager() {
        return permissionManager;
    }

    /**
     * Checks if the player has the specified permission.
     * If the player is an operator or the permission manager has the permission, it returns true.
     * Otherwise, it returns false.
     *
     * @param permission The permission to check.
     * @return True if the player has the permission, false otherwise.
     */
    public final boolean hasPermission(final String permission) {
        return isOp() || permissionManager.hasPermission(permission);
    }

    /**
     * Returns the name of the player as a string.
     * If the game profile is not null, it returns the name of the game profile.
     * Otherwise, it returns "NULL".
     *
     * @return The name of the player as a string.
     */
    public final String getNameString() {
        if (gameProfile != null) {
            return gameProfile.getName();
        }
        return "NULL";
    }

    /**
     * Returns the name of the player.
     * If the game profile is not null, it returns the name of the game profile.
     * Otherwise, it returns null.
     *
     * @return The name of the player.
     */
    public final String getName() {
        if (gameProfile != null) {
            return gameProfile.getName();
        }
        return null;
    }

    /**
     * Returns the name of the player in lowercase.
     * If the game profile is not null, it returns the name of the game profile in lowercase.
     * Otherwise, it returns null.
     *
     * @return The name of the player in lowercase.
     */
    public final String getNameLowerCase() {
        if (gameProfile != null) {
            return gameProfile.getName().toLowerCase();
        }
        return null;
    }

    /**
     * Returns the UUID of the player as a string.
     * If the game profile is not null, it returns the UUID of the game profile as a string.
     * Otherwise, it returns "NULL".
     *
     * @return The UUID of the player as a string.
     */
    public final String getUUIDString() {
        if (gameProfile != null) {
            return gameProfile.getUUID().toString();
        }
        return "NULL";
    }

    /**
     * Returns the UUID of the player.
     * If the game profile is not null, it returns the UUID of the game profile.
     * Otherwise, it returns null.
     *
     * @return The UUID of the player.
     */
    public final UUID getUUID() {
        if (gameProfile != null) {
            return gameProfile.getUUID();
        }
        return null;
    }

    /**
     * @return The IP address of the client.
     */
    public final String getIp() {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostName();
    }

    /**
     * Adds the current time to the packets counter and removes times that are older than the reset time.
     * It returns the size of the packets counter.
     * It is general for all packets. It is not specific to any packet.
     *
     * @param resetTime The reset time.
     * @return The size of the packets counter.
     */
    public final int addAndGetPacketsCount(final long resetTime) {
        final long time = System.currentTimeMillis();
        packetsCounter.add(time);
        packetsCounter.removeIf(aLong -> aLong + resetTime < time);
        return packetsCounter.size();
    }

    /**
     * Adds the current time to the packets counter for the specified packet and removes times that are older than the reset time.
     * It returns the size of the packets counter for the packet.
     *
     * @param resetTime The reset time.
     * @param packet    The packet to count.
     * @return The size of the packets counter for the packet.
     */
    public final int addAndGetPacketsCount(final long resetTime, final Class<? extends PacketIn> packet) {
        final PacketLimiter packetLimiter = getPacketCounterByPacket(packet);
        return packetLimiter.addAndGetPacketsCount(resetTime);
    }

    /**
     * Returns the packet limiter for the specified packet.
     * If a packet limiter for the packet exists in the packets individual counter, it returns the packet limiter.
     * Otherwise, it creates a new packet limiter for the packet, adds it to the packets individual counter, and returns the packet limiter.
     *
     * @param packet The packet to get the packet limiter for.
     * @return The packet limiter for the packet.
     */
    private PacketLimiter getPacketCounterByPacket(final Class<? extends PacketIn> packet) {
        return packetsIndividualCounter.computeIfAbsent(packet, p -> new PacketLimiter(packet));
    }

    /**
     * Sends a chat message to the client.
     * If the message is not null, it sends a PacketChat with the message and a byte of 1.
     *
     * @param message The message to send.
     */
    public final void sendMessage(final String message) {
        if (message != null) {
            sendPacket(new PacketPlayOutChat(message, (byte) 1));
        }
    }

    /**
     * Sends an action bar message to the client.
     * If the message is not null, it sends a PacketChat with the message and a byte of 2.
     *
     * @param message The message to send.
     */
    public final void sendActionBar(final String message) {
        if (message != null) {
            sendPacket(new PacketPlayOutChat(message, (byte) 2));
        }
    }

    /**
     * Sends a tab's header and footer to the client.
     *
     * @param header The header to send.
     * @param footer The footer to send.
     */
    public final void sendTabHeaderFooter(final String header, final String footer) {
        if (header != null && footer != null) {
            sendPacket(new PacketPlayOutPlayerListHeaderFooter(header, footer));
        }
    }

    /**
     * Sends a title and subtitle to the client with default fade in, stay, and fade out times.
     *
     * @param title    The title to send.
     * @param subTitle The subtitle to send.
     */
    public final void sendTitle(final String title, final String subTitle) {
        sendTitle(title, subTitle, 20, 40, 20);
    }

    /**
     * Sends a title and subtitle to the client with specified fade in, stay, and fade out times.
     *
     * @param title    The title to send.
     * @param subTitle The subtitle to send.
     * @param fadeIn   The time in ticks for the title to fade in.
     * @param stay     The time in ticks for the title to stay.
     * @param stayOut  The time in ticks for the title to fade out.
     */
    public final void sendTitle(final String title, final String subTitle, final int fadeIn, final int stay, final int stayOut) {
        writePacket(new PacketPlayOutTitle(title == null ? "" : title, 0, 0, 0, PacketPlayOutTitle.Type.TITLE));
        writePacket(new PacketPlayOutTitle(subTitle == null ? "" : subTitle, 0, 0, 0, PacketPlayOutTitle.Type.SUBTITLE));
        sendPacket(new PacketPlayOutTitle(null, fadeIn, stay, stayOut, PacketPlayOutTitle.Type.TIMES));
    }

    /**
     * Shows item in the player's inventory.
     *
     * @param slot The slot to show the item in. (36-44 hotbar)
     * @param item The item to show.
     */
    public final void setItem(final int slot, final ItemStack item) {
        sendPacket(new PacketPlayOutSetSlot(0, slot, item));
    }

    /**
     * Verifies the connection state of the player.
     * If the connection state is not the specified state, it throws an IllegalStateException.
     *
     * @param state The expected connection state.
     */
    public final void verifyState(final ConnectionState state) {
        if (connectionState != state) {
            throw new IllegalStateException("Invalid ConnectionState");
        }
    }

    /**
     * Checks if the player is an operator.
     *
     * @return True if the player is an operator, false otherwise.
     */
    public final boolean isOp() {
        return server.getConnectionManager().isOp(getName());
    }
}
