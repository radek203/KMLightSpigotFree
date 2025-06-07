package pl.kwadratowamasakra.lightspigot.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.PacketDirection;
import pl.kwadratowamasakra.lightspigot.connection.registry.Packet;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketManager;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * The PacketDecoder class is a decoder for packets.
 * It extends the MessageToMessageDecoder class from Netty and overrides the decode method.
 * It uses the PacketManager to construct and read packets from data in ByteBuf by their id.
 */
public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final LightSpigotServer server;
    private final PlayerConnection connection;
    private final PacketManager packetManager;

    /**
     * Constructs a new PacketDecoder with the specified server, packet manager, and connection.
     *
     * @param server        The server that the decoder is associated with.
     * @param packetManager The packet manager that the decoder uses to construct and read packets.
     * @param connection    The connection that the decoder is associated with.
     */
    public PacketDecoder(final LightSpigotServer server, final PacketManager packetManager, final PlayerConnection connection) {
        this.server = server;
        this.packetManager = packetManager;
        this.connection = connection;
    }

    /**
     * Decodes all linked packets by {@link VarIntFrameDecoder},
     * construct and read packets from data in {@link ByteBuf} by their id.
     * If the channel is not active, the method returns immediately.
     * If the ByteBuf is invalid, a DecoderException is thrown.
     * If the packet count exceeds the maximum allowed by the server configuration, a DecoderException is thrown.
     * If the packet id is invalid, a DecoderException is thrown.
     * If an error occurs while constructing the packet, a DecoderException is thrown.
     * If the packet count for the specific packet type exceeds the limit, a DecoderException is thrown.
     * The packet is read from the buffer and passed to the next handler in the pipeline.
     */
    protected final void decode(final ChannelHandlerContext handlerContext, final ByteBuf byteBuf, final List<Object> list) {
        if (!handlerContext.channel().isActive()) {
            return;
        }

        if (byteBuf.refCnt() < 1) {
            throw new DecoderException("Invalid refCnt");
        }
        if (byteBuf.capacity() < 1 || byteBuf.capacity() > 4800) {
            throw new DecoderException("Invalid Capacity v1");
        }
        if (byteBuf.capacity() > byteBuf.maxCapacity()) {
            throw new DecoderException("Invalid Capacity v2");
        }
        if (byteBuf.readableBytes() > 4000) {
            throw new DecoderException("Invalid ReadableBytes");
        }
        if (!(byteBuf instanceof EmptyByteBuf)) {
            if (byteBuf.readerIndex() > 1000) {
                throw new DecoderException("Invalid ByteBuf Index");
            }
            if (byteBuf.writerIndex() > 4000) {
                throw new DecoderException("Invalid WriterIndex");
            }
        }

        if (server.getConfig().isPacketLimiterEnabled() && connection.tryAcceptGlobalPacket(server.getConfig().getPacketCountReset(), server.getConfig().getPacketCountMax())) {
            throw new DecoderException("PacketCount > " + server.getConfig().getPacketCountMax());
        }

        final PacketBuffer buffer = new PacketBuffer(byteBuf);
        final int packetId = buffer.readVarInt();
        if (!packetManager.containsPacketId(PacketDirection.SERVERBOUND, connection.getVersion(), connection.getConnectionState(), packetId)) {
            throw new DecoderException("Received invalid PacketId (" + packetId + ", " + connection.getVersion() + ", " + connection.getConnectionState() + ")");
        }
        final Packet packet;
        try {
            packet = packetManager.constructPacket(PacketDirection.SERVERBOUND, connection.getVersion(), connection.getConnectionState(), packetId);
        } catch (final InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new DecoderException("Decoder error while constructing PacketId (" + packetId + ", " + connection.getVersion() + ", " + connection.getConnectionState() + ")");
        }
        final PacketIn packetIn = ((PacketIn) packet);

        if (server.getConfig().isPacketLimiterEnabled() && connection.tryAcceptPacket(server.getConfig().getPacketCountReset(), packetIn.getClass(), packetIn.getLimit())) {
            throw new DecoderException("PacketCount (" + packetIn.getClass().getSimpleName() + ") > Limit");
        }

        packetIn.read(connection, buffer);

        handlerContext.fireChannelRead(packet);
    }

}