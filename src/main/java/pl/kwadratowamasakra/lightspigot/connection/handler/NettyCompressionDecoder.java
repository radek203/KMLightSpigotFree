package pl.kwadratowamasakra.lightspigot.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;

import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * The NettyCompressionDecoder class is a decoder for packets.
 * It is based on how Minecraft decode packets. (Based on default Minecraft's NettyCompressionDecoder)
 */
public class NettyCompressionDecoder extends ByteToMessageDecoder {

    private final Inflater inflater;
    private int compressionThreshold;

    public NettyCompressionDecoder(final int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
        inflater = new Inflater();
    }

    protected final void decode(final ChannelHandlerContext handlerContext, final ByteBuf byteBuf, final List<Object> list) {
        if (byteBuf.readableBytes() != 0) {
            final PacketBuffer packetbuffer = new PacketBuffer(byteBuf);
            final int i = packetbuffer.readVarInt();

            if (i == 0) {
                list.add(packetbuffer.readBytes(packetbuffer.readableBytes()));
            } else {
                if (i < compressionThreshold) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + compressionThreshold);
                }

                if (i > 2097152) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 2097152);
                }

                final byte[] abyte = new byte[packetbuffer.readableBytes()];
                packetbuffer.readBytes(abyte);
                inflater.setInput(abyte);
                final byte[] abyte1 = new byte[i];
                try {
                    inflater.inflate(abyte1);
                } catch (final DataFormatException ignored) {
                }
                list.add(Unpooled.wrappedBuffer(abyte1));
                inflater.reset();
            }
        }
    }

    /**
     * Sets the compression threshold.
     *
     * @param compressionThreshold The new compression threshold.
     */
    public final void setCompressionThreshold(final int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
}
