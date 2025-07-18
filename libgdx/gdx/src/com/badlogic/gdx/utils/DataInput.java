package com.badlogic.gdx.utils;

import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Extends {@link DataInputStream} with additional convenience methods.
 */
public class DataInput extends DataInputStream {
    private char[] chars = new char[32];

    public DataInput(InputStream in) {
        super(in);
    }

    /**
     * Reads a 1-5 byte int.
     */
    public int readInt(boolean optimizePositive) throws IOException {
        int b = readByte();
        int result = b & 0x7F;
        if ((b & 0x80) != 0) {
            b = readByte();
            result |= (b & 0x7F) << 7;
            if ((b & 0x80) != 0) {
                b = readByte();
                result |= (b & 0x7F) << 14;
                if ((b & 0x80) != 0) {
                    b = readByte();
                    result |= (b & 0x7F) << 21;
                    if ((b & 0x80) != 0) {
                        b = readByte();
                        result |= (b & 0x7F) << 28;
                    }
                }
            }
        }
        return optimizePositive ? result : ((result >>> 1) ^ -(result & 1));
    }

    /**
     * Reads the length and string of UTF8 characters, or null.
     *
     * @return May be null.
     */
    public @Nullable String readString() throws IOException {
        int charCount = readInt(true);
        switch (charCount) {
            case 0:
                return null;
            case 1:
                return "";
        }
        charCount--;
        if (chars.length < charCount) chars = new char[charCount];
        char[] chars = this.chars;
        // Try to read 7 bit ASCII chars.
        int charIndex = 0;
        int b = 0;
        while (charIndex < charCount) {
            b = readByte();
            if (b < 0) break;
            chars[charIndex++] = (char) b;
        }
        // If a char was not ASCII, finish with slow path.
        if (charIndex < charCount) readUtf8_slow(charCount, charIndex, b & 0xFF);
        return new String(chars, 0, charCount);
    }

    private void readUtf8_slow(int charCount, int charIndex, int b) throws IOException {
        char[] chars = this.chars;
        while (true) {
            switch (b >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    chars[charIndex] = (char) b;
                    break;
                case 12:
                case 13:
                    chars[charIndex] = (char) ((b & 0x1F) << 6 | readByte() & 0x3F);
                    break;
                case 14:
                    chars[charIndex] = (char) ((b & 0x0F) << 12 | (readByte() & 0x3F) << 6 | readByte() & 0x3F);
                    break;
            }
            if (++charIndex >= charCount) break;
            b = readByte() & 0xFF;
        }
    }
}
