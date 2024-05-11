package pl.kwadratowamasakra.lightspigot.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * The VarIntLengthEncoder class is an encoder for packets.
 * Is is based on how Minecraft encode packets. (Based on default Minecraft's VarIntLengthEncoder)
 */
@ChannelHandler.Sharable
public class VarIntLengthEncoder extends MessageToMessageEncoder<ByteBuf> {

    private static void writeVarInt(final ByteBuf buf, final int value) {
        int i = value;
        while (true) {
            if ((i & 0xFFFFFF80) == 0) {
                buf.writeByte(i);
                return;
            }

            buf.writeByte(i & 0x7F | 0x80);
            i >>>= 7;
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
