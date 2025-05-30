package pl.kwadratowamasakra.lightspigot.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.PacketDirection;
import pl.kwadratowamasakra.lightspigot.connection.registry.Packet;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketManager;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

/**
 * The PacketEncoder class is an encoder for packets.
 * It extends the MessageToByteEncoder class from Netty and overrides the encode method.
 * It uses the PacketManager to get the id of the packet and write it to a new ByteBuf.
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final LightSpigotServer server;
    private final PlayerConnection connection;
    private final PacketManager packetManager;

    /**
     * Constructs a new PacketEncoder with the specified server and packet manager.
     *
     * @param server        The server that the encoder is associated with.
     * @param packetManager The packet manager that the encoder uses to get the id of the packet and write it to a new ByteBuf.
     * @param connection    The connection that the decoder is associated with.
     */
    public PacketEncoder(final LightSpigotServer server, final PacketManager packetManager, final PlayerConnection connection) {
        this.server = server;
        this.packetManager = packetManager;
        this.connection = connection;
    }

    /**
     * Encodes the packet into a ByteBuf.
     * It gets the id of the packet from the PacketManager and writes it to a new ByteBuf.
     * If the id is less than 0, an error message is logged and the method returns immediately.
     * The packet is then written to the ByteBuf.
     *
     * @param handlerContext The context of the channel handler.
     * @param packet         The packet to encode.
     * @param byteBuf        The ByteBuf to which the encoded packet is written.
     */
    protected final void encode(final ChannelHandlerContext handlerContext, final Packet packet, final ByteBuf byteBuf) {
        final int packetId = packetManager.getPacketId(PacketDirection.CLIENTBOUND, connection.getVersion(), packet.getClass());
        if (packetId < 0) {
            server.getLogger().error("PacketEncoder error", "Returned PacketId by registry is < 0 (" + packetId + ")");
            return;
        }

        final PacketBuffer buffer = new PacketBuffer();
        buffer.writeVarInt(packetId);
        ((PacketOut) packet).write(connection, buffer);

        byteBuf.writeBytes(buffer.getBuf());
    }

}