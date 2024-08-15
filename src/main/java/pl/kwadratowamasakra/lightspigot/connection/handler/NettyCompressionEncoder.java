package pl.kwadratowamasakra.lightspigot.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;

import java.util.zip.Deflater;

/**
 * The NettyCompressionEncoder class is an encoder for packets.
 * It is based on how Minecraft encode packets. (Based on default Minecraft's NettyCompressionEncoder)
 */
public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
    private final byte[] buffer = new byte[8192];
    private final Deflater deflater;
    private int compressionThreshold;

    public NettyCompressionEncoder(final int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
        deflater = new Deflater();
    }

    protected final void encode(final ChannelHandlerContext handlerContext, final ByteBuf byteBuf, final ByteBuf buf) {
        final int i = byteBuf.readableBytes();
        final PacketBuffer packetbuffer = new PacketBuffer(buf);

        if (i < compressionThreshold) {
            packetbuffer.writeVarInt(0);
            packetbuffer.writeBytes(byteBuf);
        } else {
            final byte[] abyte = new byte[i];
            byteBuf.readBytes(abyte);
            packetbuffer.writeVarInt(abyte.length);
            deflater.setInput(abyte, 0, i);
            deflater.finish();

            while (!deflater.finished()) {
                final int j = deflater.deflate(buffer);
                packetbuffer.writeBytes(buffer, 0, j);
            }

            deflater.reset();
        }
    }

    public final void setCompressionThreshold(final int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
}
