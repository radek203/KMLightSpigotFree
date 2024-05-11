package pl.kwadratowamasakra.lightspigot.connection.registry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Is a wrapper for {@link ByteBuf} with additional methods for reading and writing data.
 * Is is based on how packets are read and written in Minecraft.
 */
public class PacketBuffer extends ByteBuf {

    private final ByteBuf buf;

    /**
     * Save {@link ByteBuf} of read packet.
     */
    public PacketBuffer(final ByteBuf buf) {
        this.buf = buf;
    }

    /**
     * Create new {@link ByteBuf} for new packet.
     */
    public PacketBuffer() {
        buf = Unpooled.buffer();
    }

    public final ByteBuf getBuf() {
        return buf;
    }

    public final int readVarInt() {
        int i = 0;
        final int maxRead = Math.min(5, buf.readableBytes());

        for (int j = 0; j < maxRead; j++) {
            final int k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }

        buf.readBytes(maxRead);

        throw new IllegalArgumentException("Cannot read VarInt");
    }

    public final void writeVarInt(final int value) {
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

    public final String readString() {
        return readString(readVarInt());
    }

    public final String readString(final int length) {
        final String str = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
        buf.skipBytes(length);
        return str;
    }

    public final void writeString(final CharSequence charSequence) {
        final int size = ByteBufUtil.utf8Bytes(charSequence);
        writeVarInt(size);
        buf.writeCharSequence(charSequence, StandardCharsets.UTF_8);
    }

    public final byte[] readBytesArray() {
        final int length = readVarInt();
        final byte[] array = new byte[length];
        buf.readBytes(array);
        return array;
    }

    public final void writeBytesArray(final byte[] array) {
        writeVarInt(array.length);
        buf.writeBytes(array);
    }

    public final UUID readUUID() {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public final void writeUUID(final UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public final <T extends Enum<T>> T readEnumValue(final Class<T> enumClass) {
        return (enumClass.getEnumConstants())[readVarInt()];
    }

    public final void writeEnumValue(final Enum<?> value) {
        writeVarInt(value.ordinal());
    }

    @Override
    public final int capacity() {
        return buf.capacity();
    }

    @Override
    public final ByteBuf capacity(final int newCapacity) {
        return buf.capacity(newCapacity);
    }

    @Override
    public final int maxCapacity() {
        return buf.maxCapacity();
    }

    @Override
    public final ByteBufAllocator alloc() {
        return buf.alloc();
    }

    @Override
    @Deprecated
    public final ByteOrder order() {
        return buf.order();
    }

    @Override
    @Deprecated
    public final ByteBuf order(final ByteOrder endianness) {
        return buf.order(endianness);
    }

    @Override
    public final ByteBuf unwrap() {
        return buf.unwrap();
    }

    @Override
    public final boolean isDirect() {
        return buf.isDirect();
    }

    @Override
    public final boolean isReadOnly() {
        return buf.isReadOnly();
    }

    @Override
    public final ByteBuf asReadOnly() {
        return buf.asReadOnly();
    }

    @Override
    public final int readerIndex() {
        return buf.readerIndex();
    }

    @Override
    public final ByteBuf readerIndex(final int readerIndex) {
        return buf.readerIndex(readerIndex);
    }

    @Override
    public final int writerIndex() {
        return buf.writerIndex();
    }

    @Override
    public final ByteBuf writerIndex(final int writerIndex) {
        return buf.writerIndex(writerIndex);
    }

    @Override
    public final ByteBuf setIndex(final int readerIndex, final int writerIndex) {
        return buf.setIndex(readerIndex, writerIndex);
    }

    @Override
    public final int readableBytes() {
        return buf.readableBytes();
    }

    @Override
    public final int writableBytes() {
        return buf.writableBytes();
    }

    @Override
    public final int maxWritableBytes() {
        return buf.maxWritableBytes();
    }

    @Override
    public final int maxFastWritableBytes() {
        return buf.maxFastWritableBytes();
    }

    @Override
    public final boolean isReadable() {
        return buf.isReadable();
    }

    @Override
    public final boolean isReadable(final int size) {
        return buf.isReadable(size);
    }

    @Override
    public final boolean isWritable() {
        return buf.isWritable();
    }

    @Override
    public final boolean isWritable(final int size) {
        return buf.isWritable(size);
    }

    @Override
    public final ByteBuf clear() {
        return buf.clear();
    }

    @Override
    public final ByteBuf markReaderIndex() {
        return buf.markReaderIndex();
    }

    @Override
    public final ByteBuf resetReaderIndex() {
        return buf.resetReaderIndex();
    }

    @Override
    public final ByteBuf markWriterIndex() {
        return buf.markWriterIndex();
    }

    @Override
    public final ByteBuf resetWriterIndex() {
        return buf.resetWriterIndex();
    }

    @Override
    public final ByteBuf discardReadBytes() {
        return buf.discardReadBytes();
    }

    @Override
    public final ByteBuf discardSomeReadBytes() {
        return buf.discardSomeReadBytes();
    }

    @Override
    public final ByteBuf ensureWritable(final int minWritableBytes) {
        return buf.ensureWritable(minWritableBytes);
    }

    @Override
    public final int ensureWritable(final int minWritableBytes, final boolean force) {
        return buf.ensureWritable(minWritableBytes, force);
    }

    @Override
    public final boolean getBoolean(final int index) {
        return buf.getBoolean(index);
    }

    @Override
    public final byte getByte(final int index) {
        return buf.getByte(index);
    }

    @Override
    public final short getUnsignedByte(final int index) {
        return buf.getUnsignedByte(index);
    }

    @Override
    public final short getShort(final int index) {
        return buf.getShort(index);
    }

    @Override
    public final short getShortLE(final int index) {
        return buf.getShortLE(index);
    }

    @Override
    public final int getUnsignedShort(final int index) {
        return buf.getUnsignedShort(index);
    }

    @Override
    public final int getUnsignedShortLE(final int index) {
        return buf.getUnsignedShortLE(index);
    }

    @Override
    public final int getMedium(final int index) {
        return buf.getMedium(index);
    }

    @Override
    public final int getMediumLE(final int index) {
        return buf.getMediumLE(index);
    }

    @Override
    public final int getUnsignedMedium(final int index) {
        return buf.getUnsignedMedium(index);
    }

    @Override
    public final int getUnsignedMediumLE(final int index) {
        return buf.getUnsignedMediumLE(index);
    }

    @Override
    public final int getInt(final int index) {
        return buf.getInt(index);
    }

    @Override
    public final int getIntLE(final int index) {
        return buf.getIntLE(index);
    }

    @Override
    public final long getUnsignedInt(final int index) {
        return buf.getUnsignedInt(index);
    }

    @Override
    public final long getUnsignedIntLE(final int index) {
        return buf.getUnsignedIntLE(index);
    }

    @Override
    public final long getLong(final int index) {
        return buf.getLong(index);
    }

    @Override
    public final long getLongLE(final int index) {
        return buf.getLongLE(index);
    }

    @Override
    public final char getChar(final int index) {
        return buf.getChar(index);
    }

    @Override
    public final float getFloat(final int index) {
        return buf.getFloat(index);
    }

    @Override
    public final float getFloatLE(final int index) {
        return buf.getFloatLE(index);
    }

    @Override
    public final double getDouble(final int index) {
        return buf.getDouble(index);
    }

    @Override
    public final double getDoubleLE(final int index) {
        return buf.getDoubleLE(index);
    }

    @Override
    public final ByteBuf getBytes(final int index, final ByteBuf dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public final ByteBuf getBytes(final int index, final ByteBuf dst, final int length) {
        return buf.getBytes(index, dst, length);
    }

    @Override
    public final ByteBuf getBytes(final int index, final ByteBuf dst, final int dstIndex, final int length) {
        return buf.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public final ByteBuf getBytes(final int index, final byte[] dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public final ByteBuf getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        return buf.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public final ByteBuf getBytes(final int index, final ByteBuffer dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public final ByteBuf getBytes(final int index, final OutputStream out, final int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public final int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public final int getBytes(final int index, final FileChannel out, final long position, final int length) throws IOException {
        return buf.getBytes(index, out, position, length);
    }

    @Override
    public final CharSequence getCharSequence(final int index, final int length, final Charset charset) {
        return buf.getCharSequence(index, length, charset);
    }

    @Override
    public final ByteBuf setBoolean(final int index, final boolean value) {
        return buf.setBoolean(index, value);
    }

    @Override
    public final ByteBuf setByte(final int index, final int value) {
        return buf.setByte(index, value);
    }

    @Override
    public final ByteBuf setShort(final int index, final int value) {
        return buf.setShort(index, value);
    }

    @Override
    public final ByteBuf setShortLE(final int index, final int value) {
        return buf.setShortLE(index, value);
    }

    @Override
    public final ByteBuf setMedium(final int index, final int value) {
        return buf.setMedium(index, value);
    }

    @Override
    public final ByteBuf setMediumLE(final int index, final int value) {
        return buf.setMediumLE(index, value);
    }

    @Override
    public final ByteBuf setInt(final int index, final int value) {
        return buf.setInt(index, value);
    }

    @Override
    public final ByteBuf setIntLE(final int index, final int value) {
        return buf.setIntLE(index, value);
    }

    @Override
    public final ByteBuf setLong(final int index, final long value) {
        return buf.setLong(index, value);
    }

    @Override
    public final ByteBuf setLongLE(final int index, final long value) {
        return buf.setLongLE(index, value);
    }

    @Override
    public final ByteBuf setChar(final int index, final int value) {
        return buf.setChar(index, value);
    }

    @Override
    public final ByteBuf setFloat(final int index, final float value) {
        return buf.setFloat(index, value);
    }

    @Override
    public final ByteBuf setFloatLE(final int index, final float value) {
        return buf.setFloatLE(index, value);
    }

    @Override
    public final ByteBuf setDouble(final int index, final double value) {
        return buf.setDouble(index, value);
    }

    @Override
    public final ByteBuf setDoubleLE(final int index, final double value) {
        return buf.setDoubleLE(index, value);
    }

    @Override
    public final ByteBuf setBytes(final int index, final ByteBuf src) {
        return buf.setBytes(index, src);
    }

    @Override
    public final ByteBuf setBytes(final int index, final ByteBuf src, final int length) {
        return buf.setBytes(index, src, length);
    }

    @Override
    public final ByteBuf setBytes(final int index, final ByteBuf src, final int srcIndex, final int length) {
        return buf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public final ByteBuf setBytes(final int index, final byte[] src) {
        return buf.setBytes(index, src);
    }

    @Override
    public final ByteBuf setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        return buf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public final ByteBuf setBytes(final int index, final ByteBuffer src) {
        return buf.setBytes(index, src);
    }

    @Override
    public final int setBytes(final int index, final InputStream in, final int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public final int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public final int setBytes(final int index, final FileChannel in, final long position, final int length) throws IOException {
        return buf.setBytes(index, in, position, length);
    }

    @Override
    public final ByteBuf setZero(final int index, final int length) {
        return buf.setZero(index, length);
    }

    @Override
    public final int setCharSequence(final int index, final CharSequence sequence, final Charset charset) {
        return buf.setCharSequence(index, sequence, charset);
    }

    @Override
    public final boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public final byte readByte() {
        return buf.readByte();
    }

    @Override
    public final short readUnsignedByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public final short readShort() {
        return buf.readShort();
    }

    @Override
    public final short readShortLE() {
        return buf.readShortLE();
    }

    @Override
    public final int readUnsignedShort() {
        return buf.readUnsignedShort();
    }

    @Override
    public final int readUnsignedShortLE() {
        return buf.readUnsignedShortLE();
    }

    @Override
    public final int readMedium() {
        return buf.readMedium();
    }

    @Override
    public final int readMediumLE() {
        return buf.readMediumLE();
    }

    @Override
    public final int readUnsignedMedium() {
        return buf.readUnsignedMedium();
    }

    @Override
    public final int readUnsignedMediumLE() {
        return buf.readUnsignedMediumLE();
    }

    @Override
    public final int readInt() {
        return buf.readInt();
    }

    @Override
    public final int readIntLE() {
        return buf.readIntLE();
    }

    @Override
    public final long readUnsignedInt() {
        return buf.readUnsignedInt();
    }

    @Override
    public final long readUnsignedIntLE() {
        return buf.readUnsignedIntLE();
    }

    @Override
    public final long readLong() {
        return buf.readLong();
    }

    @Override
    public final long readLongLE() {
        return buf.readLongLE();
    }

    @Override
    public final char readChar() {
        return buf.readChar();
    }

    @Override
    public final float readFloat() {
        return buf.readFloat();
    }

    @Override
    public final float readFloatLE() {
        return buf.readFloatLE();
    }

    @Override
    public final double readDouble() {
        return buf.readDouble();
    }

    @Override
    public final double readDoubleLE() {
        return buf.readDoubleLE();
    }

    @Override
    public final ByteBuf readBytes(final int length) {
        return buf.readBytes(length);
    }

    @Override
    public final ByteBuf readSlice(final int length) {
        return buf.readSlice(length);
    }

    @Override
    public final ByteBuf readRetainedSlice(final int length) {
        return buf.readRetainedSlice(length);
    }

    @Override
    public final ByteBuf readBytes(final ByteBuf dst) {
        return buf.readBytes(dst);
    }

    @Override
    public final ByteBuf readBytes(final ByteBuf dst, final int length) {
        return buf.readBytes(dst, length);
    }

    @Override
    public final ByteBuf readBytes(final ByteBuf dst, final int dstIndex, final int length) {
        return buf.readBytes(dst, dstIndex, length);
    }

    @Override
    public final ByteBuf readBytes(final byte[] dst) {
        return buf.readBytes(dst);
    }

    @Override
    public final ByteBuf readBytes(final byte[] dst, final int dstIndex, final int length) {
        return buf.readBytes(dst, dstIndex, length);
    }

    @Override
    public final ByteBuf readBytes(final ByteBuffer dst) {
        return buf.readBytes(dst);
    }

    @Override
    public final ByteBuf readBytes(final OutputStream out, final int length) throws IOException {
        return buf.readBytes(out, length);
    }

    @Override
    public final int readBytes(final GatheringByteChannel out, final int length) throws IOException {
        return buf.readBytes(out, length);
    }

    @Override
    public final CharSequence readCharSequence(final int length, final Charset charset) {
        return buf.readCharSequence(length, charset);
    }

    @Override
    public final int readBytes(final FileChannel out, final long position, final int length) throws IOException {
        return buf.readBytes(out, position, length);
    }

    @Override
    public final ByteBuf skipBytes(final int length) {
        return buf.skipBytes(length);
    }

    @Override
    public final ByteBuf writeBoolean(final boolean value) {
        return buf.writeBoolean(value);
    }

    @Override
    public final ByteBuf writeByte(final int value) {
        return buf.writeByte(value);
    }

    @Override
    public final ByteBuf writeShort(final int value) {
        return buf.writeShort(value);
    }

    @Override
    public final ByteBuf writeShortLE(final int value) {
        return buf.writeShortLE(value);
    }

    @Override
    public final ByteBuf writeMedium(final int value) {
        return buf.writeMedium(value);
    }

    @Override
    public final ByteBuf writeMediumLE(final int value) {
        return buf.writeMediumLE(value);
    }

    @Override
    public final ByteBuf writeInt(final int value) {
        return buf.writeInt(value);
    }

    @Override
    public final ByteBuf writeIntLE(final int value) {
        return buf.writeIntLE(value);
    }

    @Override
    public final ByteBuf writeLong(final long value) {
        return buf.writeLong(value);
    }

    @Override
    public final ByteBuf writeLongLE(final long value) {
        return buf.writeLongLE(value);
    }

    @Override
    public final ByteBuf writeChar(final int value) {
        return buf.writeChar(value);
    }

    @Override
    public final ByteBuf writeFloat(final float value) {
        return buf.writeFloat(value);
    }

    @Override
    public final ByteBuf writeFloatLE(final float value) {
        return buf.writeFloatLE(value);
    }

    @Override
    public final ByteBuf writeDouble(final double value) {
        return buf.writeDouble(value);
    }

    @Override
    public final ByteBuf writeDoubleLE(final double value) {
        return buf.writeDoubleLE(value);
    }

    @Override
    public final ByteBuf writeBytes(final ByteBuf src) {
        return buf.writeBytes(src);
    }

    @Override
    public final ByteBuf writeBytes(final ByteBuf src, final int length) {
        return buf.writeBytes(src, length);
    }

    @Override
    public final ByteBuf writeBytes(final ByteBuf src, final int srcIndex, final int length) {
        return buf.writeBytes(src, srcIndex, length);
    }

    @Override
    public final ByteBuf writeBytes(final byte[] src) {
        return buf.writeBytes(src);
    }

    @Override
    public final ByteBuf writeBytes(final byte[] src, final int srcIndex, final int length) {
        return buf.writeBytes(src, srcIndex, length);
    }

    @Override
    public final ByteBuf writeBytes(final ByteBuffer src) {
        return buf.writeBytes(src);
    }

    @Override
    public final int writeBytes(final InputStream in, final int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public final int writeBytes(final ScatteringByteChannel in, final int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public final int writeBytes(final FileChannel in, final long position, final int length) throws IOException {
        return buf.writeBytes(in, position, length);
    }

    @Override
    public final ByteBuf writeZero(final int length) {
        return buf.writeZero(length);
    }

    @Override
    public final int writeCharSequence(final CharSequence sequence, final Charset charset) {
        return buf.writeCharSequence(sequence, charset);
    }

    @Override
    public final int indexOf(final int fromIndex, final int toIndex, final byte value) {
        return buf.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public final int bytesBefore(final byte value) {
        return buf.bytesBefore(value);
    }

    @Override
    public final int bytesBefore(final int length, final byte value) {
        return buf.bytesBefore(length, value);
    }

    @Override
    public final int bytesBefore(final int index, final int length, final byte value) {
        return buf.bytesBefore(index, length, value);
    }

    @Override
    public final int forEachByte(final ByteProcessor processor) {
        return buf.forEachByte(processor);
    }

    @Override
    public final int forEachByte(final int index, final int length, final ByteProcessor processor) {
        return buf.forEachByte(index, length, processor);
    }

    @Override
    public final int forEachByteDesc(final ByteProcessor processor) {
        return buf.forEachByteDesc(processor);
    }

    @Override
    public final int forEachByteDesc(final int index, final int length, final ByteProcessor processor) {
        return buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public final ByteBuf copy() {
        return buf.copy();
    }

    @Override
    public final ByteBuf copy(final int index, final int length) {
        return buf.copy(index, length);
    }

    @Override
    public final ByteBuf slice() {
        return buf.slice();
    }

    @Override
    public final ByteBuf retainedSlice() {
        return buf.retainedSlice();
    }

    @Override
    public final ByteBuf slice(final int index, final int length) {
        return buf.slice(index, length);
    }

    @Override
    public final ByteBuf retainedSlice(final int index, final int length) {
        return buf.retainedSlice(index, length);
    }

    @Override
    public final ByteBuf duplicate() {
        return buf.duplicate();
    }

    @Override
    public final ByteBuf retainedDuplicate() {
        return buf.retainedDuplicate();
    }

    @Override
    public final int nioBufferCount() {
        return buf.nioBufferCount();
    }

    @Override
    public final ByteBuffer nioBuffer() {
        return buf.nioBuffer();
    }

    @Override
    public final ByteBuffer nioBuffer(final int index, final int length) {
        return buf.nioBuffer(index, length);
    }

    @Override
    public final ByteBuffer internalNioBuffer(final int index, final int length) {
        return buf.internalNioBuffer(index, length);
    }

    @Override
    public final ByteBuffer[] nioBuffers() {
        return buf.nioBuffers();
    }

    @Override
    public final ByteBuffer[] nioBuffers(final int index, final int length) {
        return buf.nioBuffers(index, length);
    }

    @Override
    public final boolean hasArray() {
        return buf.hasArray();
    }

    @Override
    public final byte[] array() {
        return buf.array();
    }

    @Override
    public final int arrayOffset() {
        return buf.arrayOffset();
    }

    @Override
    public final boolean hasMemoryAddress() {
        return buf.hasMemoryAddress();
    }

    @Override
    public final long memoryAddress() {
        return buf.memoryAddress();
    }

    @Override
    public final boolean isContiguous() {
        return buf.isContiguous();
    }

    @Override
    public final String toString(final Charset charset) {
        return buf.toString(charset);
    }

    @Override
    public final String toString(final int index, final int length, final Charset charset) {
        return buf.toString(index, length, charset);
    }

    @Override
    public final int hashCode() {
        return buf.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        return buf.equals(obj);
    }

    @Override
    public final int compareTo(final ByteBuf buffer) {
        return buf.compareTo(buffer);
    }

    @Override
    public final String toString() {
        return buf.toString();
    }

    @Override
    public final ByteBuf retain(final int increment) {
        return buf.retain(increment);
    }

    @Override
    public final ByteBuf retain() {
        return buf.retain();
    }

    @Override
    public final ByteBuf touch() {
        return buf.touch();
    }

    @Override
    public final ByteBuf touch(final Object hint) {
        return buf.touch(hint);
    }

    @Override
    public final int refCnt() {
        return buf.refCnt();
    }

    @Override
    public final boolean release() {
        return buf.release();
    }

    @Override
    public final boolean release(final int decrement) {
        return buf.release(decrement);
    }

}