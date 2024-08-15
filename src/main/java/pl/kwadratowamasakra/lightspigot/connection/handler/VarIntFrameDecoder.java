package pl.kwadratowamasakra.lightspigot.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/**
 * The VarIntFrameDecoder class is a decoder for packets.
 * It is based on how Minecraft decode packets. (Based on default Minecraft's VarIntFrameDecoder)
 */
public class VarIntFrameDecoder extends ByteToMessageDecoder {

    private static int readVarInt(final ByteBuf byteBuf) {
        int i = 0;
        final int maxRead = Math.min(5, byteBuf.readableBytes());

        for (int j = 0; j < maxRead; j++) {
            final int k = byteBuf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }

        byteBuf.readBytes(maxRead);

        throw new IllegalArgumentException("VarInt too big");
    }

    @Override
    protected final void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        if (!ctx.channel().isActive()) {
            in.clear();
            return;
        }

        if (!in.isReadable()) return;
        final int origReaderIndex = in.readerIndex();

        for (int i = 0; i < 3; i++) {
            if (!in.isReadable()) {
                in.readerIndex(origReaderIndex);
                return;
            }

            final byte read = in.readByte();
            if (read >= 0) {
                in.readerIndex(origReaderIndex);
                final int length = readVarInt(in);
                if (length == 0) return;

                if (in.readableBytes() < length) {
                    in.readerIndex(origReaderIndex);
                    return;
                }

                out.add(in.readRetainedSlice(length));
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }

}
