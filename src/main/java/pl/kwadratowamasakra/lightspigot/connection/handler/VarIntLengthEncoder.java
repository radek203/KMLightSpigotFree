package pl.kwadratowamasakra.lightspigot.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * The VarIntLengthEncoder class is an encoder for packets.
 * It is based on how Minecraft encode packets. (Based on default Minecraft's VarIntLengthEncoder)
 */
@ChannelHandler.Sharable
public class VarIntLengthEncoder extends MessageToMessageEncoder<ByteBuf> {

    private static void writeVarInt(final ByteBuf buf, final int value) {
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            final int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            final int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
            buf.writeMedium(w);
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            final int w = (value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16) | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21);
            buf.writeInt(w);
        } else {
            final int w = (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16 | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80);
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        }
    }

    @Override
    protected final void encode(final ChannelHandlerContext ctx, final ByteBuf buf, final List<Object> out) {
        final ByteBuf lengthBuf = ctx.alloc().buffer(5);
        writeVarInt(lengthBuf, buf.readableBytes());
        out.add(lengthBuf);
        out.add(buf.retain());
    }

}
