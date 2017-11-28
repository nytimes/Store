package com.nytimes.android.external.fs3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.annotation.Nullable;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Options;
import okio.Sink;
import okio.Timeout;

public final class ReadResultBufferedSource implements BufferedSource {

    private final Buffer buffer;
    private final Throwable throwable;

    ReadResultBufferedSource(Throwable throwable) {
        this.buffer = new Buffer();
        this.throwable = throwable;
    }

    public boolean isSuccess() {
        return throwable == null;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public Buffer buffer() {
        return buffer;
    }

    @Override
    public boolean exhausted() throws IOException {
        return buffer.exhausted();
    }

    @Override
    public void require(long byteCount) throws IOException {
        buffer.require(byteCount);
    }

    @Override
    public boolean request(long byteCount) throws IOException {
        return buffer.request(byteCount);
    }

    @Override
    public byte readByte() throws IOException {
        return buffer.readByte();
    }

    @Override
    public short readShort() throws IOException {
        return buffer.readShort();
    }

    @Override
    public short readShortLe() throws IOException {
        return buffer.readShortLe();
    }

    @Override
    public int readInt() throws IOException {
        return buffer.readInt();
    }

    @Override
    public int readIntLe() throws IOException {
        return buffer.readIntLe();
    }

    @Override
    public long readLong() throws IOException {
        return buffer.readLong();
    }

    @Override
    public long readLongLe() throws IOException {
        return buffer.readLongLe();
    }

    @Override
    public long readDecimalLong() throws IOException {
        return buffer.readDecimalLong();
    }

    @Override
    public long readHexadecimalUnsignedLong() throws IOException {
        return buffer.readHexadecimalUnsignedLong();
    }

    @Override
    public void skip(long byteCount) throws IOException {
        buffer.skip(byteCount);
    }

    @Override
    public ByteString readByteString() throws IOException {
        return buffer.readByteString();
    }

    @Override
    public ByteString readByteString(long byteCount) throws IOException {
        return buffer.readByteString(byteCount);
    }

    @Override
    public int select(Options options) throws IOException {
        return buffer.select(options);
    }

    @Override
    public byte[] readByteArray() throws IOException {
        return buffer.readByteArray();
    }

    @Override
    public byte[] readByteArray(long byteCount) throws IOException {
        return buffer.readByteArray(byteCount);
    }

    @Override
    public int read(byte[] sink) throws IOException {
        return buffer.read(sink);
    }

    @Override
    public void readFully(byte[] sink) throws IOException {
        buffer.readFully(sink);
    }

    @Override
    public int read(byte[] sink, int offset, int byteCount) throws IOException {
        return buffer.read(sink, offset, byteCount);
    }

    @Override
    public void readFully(Buffer sink, long byteCount) throws IOException {
        buffer.readFully(sink, byteCount);
    }

    @Override
    public long readAll(Sink sink) throws IOException {
        return buffer.readAll(sink);
    }

    @Override
    public String readUtf8() throws IOException {
        return buffer.readUtf8();
    }

    @Override
    public String readUtf8(long byteCount) throws IOException {
        return buffer.readUtf8(byteCount);
    }

    @Nullable
    @Override
    public String readUtf8Line() throws IOException {
        return buffer.readUtf8Line();
    }

    @Override
    public String readUtf8LineStrict() throws IOException {
        return buffer.readUtf8LineStrict();
    }

    @Override
    public String readUtf8LineStrict(long limit) throws IOException {
        return buffer.readUtf8LineStrict(limit);
    }

    @Override
    public int readUtf8CodePoint() throws IOException {
        return buffer.readUtf8CodePoint();
    }

    @Override
    public String readString(Charset charset) throws IOException {
        return buffer.readString(charset);
    }

    @Override
    public String readString(long byteCount, Charset charset) throws IOException {
        return buffer.readString(byteCount, charset);
    }

    @Override
    public long indexOf(byte b) throws IOException {
        return buffer.indexOf(b);
    }

    @Override
    public long indexOf(byte b, long fromIndex) throws IOException {
        return buffer.indexOf(b, fromIndex);
    }

    @Override
    public long indexOf(byte b, long fromIndex, long toIndex) throws IOException {
        return buffer.indexOf(b, fromIndex, toIndex);
    }

    @Override
    public long indexOf(ByteString bytes) throws IOException {
        return buffer.indexOf(bytes);
    }

    @Override
    public long indexOf(ByteString bytes, long fromIndex) throws IOException {
        return buffer.indexOf(bytes, fromIndex);
    }

    @Override
    public long indexOfElement(ByteString targetBytes) throws IOException {
        return buffer.indexOfElement(targetBytes);
    }

    @Override
    public long indexOfElement(ByteString targetBytes, long fromIndex) throws IOException {
        return buffer.indexOfElement(targetBytes, fromIndex);
    }

    @Override
    public boolean rangeEquals(long offset, ByteString bytes) throws IOException {
        return buffer.rangeEquals(offset, bytes);
    }

    @Override
    public boolean rangeEquals(long offset, ByteString bytes, int bytesOffset, int byteCount) throws IOException {
        return buffer.rangeEquals(offset, bytes, bytesOffset, byteCount);
    }

    @Override
    public InputStream inputStream() {
        return buffer.inputStream();
    }

    @Override
    public long read(Buffer sink, long byteCount) throws IOException {
        return buffer.read(sink, byteCount);
    }

    @Override
    public Timeout timeout() {
        return buffer.timeout();
    }

    @Override
    public void close() throws IOException {
        buffer.close();
    }
}
