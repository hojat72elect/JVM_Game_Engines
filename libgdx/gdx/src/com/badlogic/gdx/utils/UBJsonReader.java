package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Lightweight UBJSON parser.<br>
 * <br>
 * The default behavior is to parse the JSON into a DOM containing {@link JsonValue} objects. Extend this class and override
 * methods to perform event driven parsing. When this is done, the parse methods will return null. <br>
 */
public class UBJsonReader implements BaseJsonReader {
    public boolean oldFormat = true;

    /**
     * Parses the UBJSON from the given stream. <br>
     * For best performance you should provide buffered streams to this method!
     */
    @Override
    public JsonValue parse(@NotNull InputStream input) {
        DataInputStream din = null;
        try {
            din = new DataInputStream(input);
            return parse(din);
        } catch (IOException ex) {
            throw new SerializationException(ex);
        } finally {
            StreamUtils.closeQuietly(din);
        }
    }

    @Override
    public JsonValue parse(@NotNull FileHandle file) {
        try {
            return parse(file.read(8192));
        } catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }

    public JsonValue parse(final DataInputStream din) throws IOException {
        try {
            return parse(din, din.readByte());
        } finally {
            StreamUtils.closeQuietly(din);
        }
    }

    protected JsonValue parse(final DataInputStream din, final byte type) throws IOException {
        if (type == '[')
            return parseArray(din);
        else if (type == '{')
            return parseObject(din);
        else if (type == 'Z')
            return new JsonValue(JsonValue.ValueType.nullValue);
        else if (type == 'T')
            return new JsonValue(true);
        else if (type == 'F')
            return new JsonValue(false);
        else if (type == 'B')
            return new JsonValue(readUChar(din));
        else if (type == 'U')
            return new JsonValue(readUChar(din));
        else if (type == 'i')
            return new JsonValue(oldFormat ? (long) din.readShort() : (long) din.readByte());
        else if (type == 'I')
            return new JsonValue(oldFormat ? (long) din.readInt() : (long) din.readShort());
        else if (type == 'l')
            return new JsonValue(din.readInt());
        else if (type == 'L')
            return new JsonValue(din.readLong());
        else if (type == 'd')
            return new JsonValue(din.readFloat());
        else if (type == 'D')
            return new JsonValue(din.readDouble());
        else if (type == 's' || type == 'S')
            return new JsonValue(parseString(din, type));
        else if (type == 'a' || type == 'A')
            return parseData(din, type);
        else if (type == 'C')
            return new JsonValue(din.readChar());
        else
            throw new GdxRuntimeException("Unrecognized data type");
    }

    protected JsonValue parseArray(final DataInputStream din) throws IOException {
        JsonValue result = new JsonValue(JsonValue.ValueType.array);
        byte type = din.readByte();
        byte valueType = 0;
        if (type == '$') {
            valueType = din.readByte();
            type = din.readByte();
        }
        long size = -1;
        if (type == '#') {
            size = parseSize(din, false, -1);
            if (size < 0) throw new GdxRuntimeException("Unrecognized data type");
            if (size == 0) return result;
            type = valueType == 0 ? din.readByte() : valueType;
        }
        JsonValue prev = null;
        long c = 0;
        while (din.available() > 0 && type != ']') {
            final JsonValue val = parse(din, type);
            val.parent = result;
            if (prev != null) {
                val.prev = prev;
                prev.next = val;
                result.size++;
            } else {
                result.child = val;
                result.size = 1;
            }
            prev = val;
            if (size > 0 && ++c >= size) break;
            type = valueType == 0 ? din.readByte() : valueType;
        }
        return result;
    }

    protected JsonValue parseObject(final DataInputStream din) throws IOException {
        JsonValue result = new JsonValue(JsonValue.ValueType.object);
        byte type = din.readByte();
        byte valueType = 0;
        if (type == '$') {
            valueType = din.readByte();
            type = din.readByte();
        }
        long size = -1;
        if (type == '#') {
            size = parseSize(din, false, -1);
            if (size < 0) throw new GdxRuntimeException("Unrecognized data type");
            if (size == 0) return result;
            type = din.readByte();
        }
        JsonValue prev = null;
        long c = 0;
        while (din.available() > 0 && type != '}') {
            final String key = parseString(din, true, type);
            final JsonValue child = parse(din, valueType == 0 ? din.readByte() : valueType);
            child.setName(key);
            child.parent = result;
            if (prev != null) {
                child.prev = prev;
                prev.next = child;
                result.size++;
            } else {
                result.child = child;
                result.size = 1;
            }
            prev = child;
            if (size > 0 && ++c >= size) break;
            type = din.readByte();
        }
        return result;
    }

    protected JsonValue parseData(final DataInputStream din, final byte blockType) throws IOException {
        // FIXME: a/A is currently not following the specs because it lacks strong typed, fixed sized containers,
        // see: https://github.com/thebuzzmedia/universal-binary-json/issues/27
        final byte dataType = din.readByte();
        final long size = blockType == 'A' ? readUInt(din) : (long) readUChar(din);
        final JsonValue result = new JsonValue(JsonValue.ValueType.array);
        JsonValue prev = null;
        for (long i = 0; i < size; i++) {
            final JsonValue val = parse(din, dataType);
            val.parent = result;
            if (prev != null) {
                prev.next = val;
                result.size++;
            } else {
                result.child = val;
                result.size = 1;
            }
            prev = val;
        }
        return result;
    }

    protected String parseString(final DataInputStream din, final byte type) throws IOException {
        return parseString(din, false, type);
    }

    protected String parseString(final DataInputStream din, final boolean sOptional, final byte type) throws IOException {
        long size = -1;
        if (type == 'S') {
            size = parseSize(din, true, -1);
        } else if (type == 's')
            size = readUChar(din);
        else if (sOptional) size = parseSize(din, type, false, -1);
        if (size < 0) throw new GdxRuntimeException("Unrecognized data type, string expected");
        return size > 0 ? readString(din, size) : "";
    }

    protected long parseSize(final DataInputStream din, final boolean useIntOnError, final long defaultValue) throws IOException {
        return parseSize(din, din.readByte(), useIntOnError, defaultValue);
    }

    protected long parseSize(final DataInputStream din, final byte type, final boolean useIntOnError, final long defaultValue)
            throws IOException {
        if (type == 'i') return readUChar(din);
        if (type == 'I') return readUShort(din);
        if (type == 'l') return readUInt(din);
        if (type == 'L') return din.readLong();
        if (useIntOnError) {
            long result = (long) ((short) type & 0xFF) << 24;
            result |= (long) ((short) din.readByte() & 0xFF) << 16;
            result |= (long) ((short) din.readByte() & 0xFF) << 8;
            result |= (short) din.readByte() & 0xFF;
            return result;
        }
        return defaultValue;
    }

    protected short readUChar(final DataInputStream din) throws IOException {
        return (short) ((short) din.readByte() & 0xFF);
    }

    protected int readUShort(final DataInputStream din) throws IOException {
        return ((int) din.readShort() & 0xFFFF);
    }

    protected long readUInt(final DataInputStream din) throws IOException {
        return ((long) din.readInt() & 0xFFFFFFFF);
    }

    protected String readString(final DataInputStream din, final long size) throws IOException {
        final byte[] data = new byte[(int) size];
        din.readFully(data);
        return new String(data, StandardCharsets.UTF_8);
    }
}
