package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Lightweight event-based JSON parser. All values are provided as strings to reduce work when many values are ignored.
 */
public class JsonSkimmer {
    static final int json_start = 1;
    static final int json_first_final = 35;
    static final int json_error = 0;
    static final int json_en_object = 5;
    static final int json_en_array = 23;
    static final int json_en_main = 1;
    private static final byte[] _json_actions = init__json_actions_0();
    private static final short[] _json_key_offsets = init__json_key_offsets_0();
    private static final char[] _json_trans_keys = init__json_trans_keys_0();
    private static final byte[] _json_single_lengths = init__json_single_lengths_0();
    private static final byte[] _json_range_lengths = init__json_range_lengths_0();
    private static final short[] _json_index_offsets = init__json_index_offsets_0();
    private static final byte[] _json_indicies = init__json_indicies_0();
    private static final byte[] _json_trans_targs = init__json_trans_targs_0();
    private static final byte[] _json_trans_actions = init__json_trans_actions_0();
    private static final byte[] _json_eof_actions = init__json_eof_actions_0();
    private boolean stop;

    // line 413 "../../../../../src/com/badlogic/gdx/utils/JsonSkimmer.java"
    private static byte[] init__json_actions_0() {
        return new byte[]{0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 1, 8, 2, 0, 7, 2, 0, 8, 2, 1, 3, 2, 1, 5};
    }

    private static short[] init__json_key_offsets_0() {
        return new short[]{0, 0, 11, 13, 14, 16, 25, 31, 37, 39, 50, 57, 64, 73, 74, 83, 85, 87, 96, 98, 100, 101, 103, 105, 116,
                123, 130, 141, 142, 153, 155, 157, 168, 170, 172, 174, 179, 184, 184};
    }

    private static char[] init__json_trans_keys_0() {
        return new char[]{13, 32, 34, 44, 47, 58, 91, 93, 123, 9, 10, 42, 47, 34, 42, 47, 13, 32, 34, 44, 47, 58, 125, 9, 10, 13,
                32, 47, 58, 9, 10, 13, 32, 47, 58, 9, 10, 42, 47, 13, 32, 34, 44, 47, 58, 91, 93, 123, 9, 10, 9, 10, 13, 32, 44, 47, 125,
                9, 10, 13, 32, 44, 47, 125, 13, 32, 34, 44, 47, 58, 125, 9, 10, 34, 13, 32, 34, 44, 47, 58, 125, 9, 10, 42, 47, 42, 47,
                13, 32, 34, 44, 47, 58, 125, 9, 10, 42, 47, 42, 47, 34, 42, 47, 42, 47, 13, 32, 34, 44, 47, 58, 91, 93, 123, 9, 10, 9,
                10, 13, 32, 44, 47, 93, 9, 10, 13, 32, 44, 47, 93, 13, 32, 34, 44, 47, 58, 91, 93, 123, 9, 10, 34, 13, 32, 34, 44, 47,
                58, 91, 93, 123, 9, 10, 42, 47, 42, 47, 13, 32, 34, 44, 47, 58, 91, 93, 123, 9, 10, 42, 47, 42, 47, 42, 47, 13, 32, 47,
                9, 10, 13, 32, 47, 9, 10, 0};
    }

    private static byte[] init__json_single_lengths_0() {
        return new byte[]{0, 9, 2, 1, 2, 7, 4, 4, 2, 9, 7, 7, 7, 1, 7, 2, 2, 7, 2, 2, 1, 2, 2, 9, 7, 7, 9, 1, 9, 2, 2, 9, 2, 2, 2,
                3, 3, 0, 0};
    }

    private static byte[] init__json_range_lengths_0() {
        return new byte[]{0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0,
                1, 1, 0, 0};
    }

    private static short[] init__json_index_offsets_0() {
        return new short[]{0, 0, 11, 14, 16, 19, 28, 34, 40, 43, 54, 62, 70, 79, 81, 90, 93, 96, 105, 108, 111, 113, 116, 119, 130,
                138, 146, 157, 159, 170, 173, 176, 187, 190, 193, 196, 201, 206, 207};
    }

    private static byte[] init__json_indicies_0() {
        return new byte[]{1, 1, 2, 3, 4, 3, 5, 3, 6, 1, 0, 7, 7, 3, 8, 3, 9, 9, 3, 11, 11, 12, 13, 14, 3, 15, 11, 10, 16, 16, 17,
                18, 16, 3, 19, 19, 20, 21, 19, 3, 22, 22, 3, 21, 21, 24, 3, 25, 3, 26, 3, 27, 21, 23, 28, 29, 29, 28, 30, 31, 32, 3, 33,
                34, 34, 33, 13, 35, 15, 3, 34, 34, 12, 36, 37, 3, 15, 34, 10, 16, 3, 36, 36, 12, 3, 38, 3, 3, 36, 10, 39, 39, 3, 40, 40,
                3, 13, 13, 12, 3, 41, 3, 15, 13, 10, 42, 42, 3, 43, 43, 3, 28, 3, 44, 44, 3, 45, 45, 3, 47, 47, 48, 49, 50, 3, 51, 52,
                53, 47, 46, 54, 55, 55, 54, 56, 57, 58, 3, 59, 60, 60, 59, 49, 61, 52, 3, 60, 60, 48, 62, 63, 3, 51, 52, 53, 60, 46, 54,
                3, 62, 62, 48, 3, 64, 3, 51, 3, 53, 62, 46, 65, 65, 3, 66, 66, 3, 49, 49, 48, 3, 67, 3, 51, 52, 53, 49, 46, 68, 68, 3,
                69, 69, 3, 70, 70, 3, 8, 8, 71, 8, 3, 72, 72, 73, 72, 3, 3, 3, 0};
    }

    private static byte[] init__json_trans_targs_0() {
        return new byte[]{35, 1, 3, 0, 4, 36, 36, 36, 36, 1, 6, 5, 13, 17, 22, 37, 7, 8, 9, 7, 8, 9, 7, 10, 20, 21, 11, 11, 11, 12,
                17, 19, 37, 11, 12, 19, 14, 16, 15, 14, 12, 18, 17, 11, 9, 5, 24, 23, 27, 31, 34, 25, 38, 25, 25, 26, 31, 33, 38, 25, 26,
                33, 28, 30, 29, 28, 26, 32, 31, 25, 23, 2, 36, 2};
    }

    private static byte[] init__json_trans_actions_0() {
        return new byte[]{13, 0, 15, 0, 0, 7, 3, 11, 1, 11, 17, 0, 20, 0, 0, 5, 1, 1, 1, 0, 0, 0, 11, 13, 15, 0, 7, 3, 1, 1, 1, 1,
                23, 0, 0, 0, 0, 0, 0, 11, 11, 0, 11, 11, 11, 11, 13, 0, 15, 0, 0, 7, 9, 3, 1, 1, 1, 1, 26, 0, 0, 0, 0, 0, 0, 11, 11, 0,
                11, 11, 11, 1, 0, 0};
    }

    private static byte[] init__json_eof_actions_0() {
        return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 0, 0, 0};
    }

    public void parse(String json) {
        char[] data = json.toCharArray();
        parse(data, 0, data.length);
    }

    public void parse(Reader reader) {
        char[] data = new char[1024];
        int offset = 0;
        try {
            while (true) {
                int length = reader.read(data, offset, data.length - offset);
                if (length == -1) break;
                if (length == 0) {
                    char[] newData = new char[data.length * 2];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    data = newData;
                } else
                    offset += length;
            }
        } catch (IOException ex) {
            throw new SerializationException("Error reading input.", ex);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
        parse(data, 0, offset);
    }

    public void parse(InputStream input) {
        Reader reader;
        try {
            reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new SerializationException("Error reading stream.", ex);
        }
        parse(reader);
    }

    public void parse(FileHandle file) {
        Reader reader;
        try {
            reader = file.reader("UTF-8");
        } catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
        try {
            parse(reader);
        } catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }

    // line 269 "JsonSkimmer.rl"

    public void parse(char[] data, int offset, int length) {
        stop = false;
        int cs, p = offset, top = 0;
        int[] stack = new int[4];

        int s = 0;
        String name = null;
        boolean needsUnescape = false, stringIsName = false, stringIsUnquoted = false;
        RuntimeException parseRuntimeEx = null;

        boolean debug = false;

        try {
            {
                cs = json_start;
            }

            {
                int _klen;
                int _trans;
                int _acts;
                int _nacts;
                int _keys;
                int _goto_targ = 0;

                _goto:
                while (true) {
                    switch (_goto_targ) {
                        case 0:
                            if (p == length) {
                                _goto_targ = 4;
                                continue;
                            }
                        case 1:
                            _match:
                            do {
                                _keys = _json_key_offsets[cs];
                                _trans = _json_index_offsets[cs];
                                _klen = _json_single_lengths[cs];
                                if (_klen > 0) {
                                    int _lower = _keys;
                                    int _mid;
                                    int _upper = _keys + _klen - 1;
                                    while (_upper >= _lower) {

                                        _mid = _lower + ((_upper - _lower) >> 1);
                                        if (data[p] < _json_trans_keys[_mid])
                                            _upper = _mid - 1;
                                        else if (data[p] > _json_trans_keys[_mid])
                                            _lower = _mid + 1;
                                        else {
                                            _trans += (_mid - _keys);
                                            break _match;
                                        }
                                    }
                                    _keys += _klen;
                                    _trans += _klen;
                                }

                                _klen = _json_range_lengths[cs];
                                if (_klen > 0) {
                                    int _lower = _keys;
                                    int _mid;
                                    int _upper = _keys + (_klen << 1) - 2;
                                    while (_upper >= _lower) {

                                        _mid = _lower + (((_upper - _lower) >> 1) & ~1);
                                        if (data[p] < _json_trans_keys[_mid])
                                            _upper = _mid - 2;
                                        else if (data[p] > _json_trans_keys[_mid + 1])
                                            _lower = _mid + 2;
                                        else {
                                            _trans += ((_mid - _keys) >> 1);
                                            break _match;
                                        }
                                    }
                                    _trans += _klen;
                                }
                            } while (false);

                            _trans = _json_indicies[_trans];
                            cs = _json_trans_targs[_trans];

                            if (_json_trans_actions[_trans] != 0) {
                                _acts = _json_trans_actions[_trans];
                                _nacts = _json_actions[_acts++];
                                while (_nacts-- > 0) {
                                    switch (_json_actions[_acts++]) {
                                        case 0:
                                            // line 104 "JsonSkimmer.rl"
                                        {
                                            stringIsName = true;
                                        }
                                        break;
                                        case 1:
                                            // line 107 "JsonSkimmer.rl"
                                        {
                                            String value = new String(data, s, p - s);
                                            if (needsUnescape) value = unescape(value);
                                            if (stringIsName) {
                                                stringIsName = false;
                                                name = value;
                                            } else {
                                                value(name, value, stringIsUnquoted);
                                                name = null;
                                            }
                                            if (stop) return;
                                            stringIsUnquoted = false;
                                            s = p;
                                        }
                                        break;
                                        case 2: {
                                            push(name, true);
                                            if (stop) return;
                                            name = null;
                                            {
                                                if (top == stack.length) stack = Arrays.copyOf(stack, stack.length * 2);
                                                {
                                                    stack[top++] = cs;
                                                    cs = 5;
                                                    _goto_targ = 2;
                                                    continue _goto;
                                                }
                                            }
                                        }
                                        case 3:
                                        case 5: {
                                            pop();
                                            if (stop) return;
                                            {
                                                cs = stack[--top];
                                                _goto_targ = 2;
                                                continue _goto;
                                            }
                                        }
                                        case 4: {
                                            push(name, false);
                                            if (stop) return;
                                            name = null;
                                            {
                                                if (top == stack.length) stack = Arrays.copyOf(stack, stack.length * 2);
                                                {
                                                    stack[top++] = cs;
                                                    cs = 23;
                                                    _goto_targ = 2;
                                                    continue _goto;
                                                }
                                            }
                                        }
                                        case 6: {
                                            int start = p - 1;
                                            if (data[p++] == '/') {
                                                while (p != length && data[p] != '\n')
                                                    p++;
                                                p--;
                                            } else {
                                                while (p + 1 < length && data[p] != '*' || data[p + 1] != '/')
                                                    p++;
                                                p++;
                                            }
                                        }
                                        break;
                                        case 7: {
                                            s = p;
                                            needsUnescape = false;
                                            stringIsUnquoted = true;
                                            if (stringIsName) {
                                                outer:
                                                while (true) {
                                                    switch (data[p]) {
                                                        case '\\':
                                                            needsUnescape = true;
                                                            break;
                                                        case '/':
                                                            if (p + 1 == length) break;
                                                            char c = data[p + 1];
                                                            if (c == '/' || c == '*') break outer;
                                                            break;
                                                        case ':':
                                                        case '\r':
                                                        case '\n':
                                                            break outer;
                                                    }
                                                    if (debug)
                                                        System.out.println("unquotedChar (name): '" + data[p] + "'");
                                                    p++;
                                                    if (p == length) break;
                                                }
                                            } else {
                                                outer:
                                                while (true) {
                                                    switch (data[p]) {
                                                        case '\\':
                                                            needsUnescape = true;
                                                            break;
                                                        case '/':
                                                            if (p + 1 == length) break;
                                                            char c = data[p + 1];
                                                            if (c == '/' || c == '*') break outer;
                                                            break;
                                                        case '}':
                                                        case ']':
                                                        case ',':
                                                        case '\r':
                                                        case '\n':
                                                            break outer;
                                                    }
                                                    p++;
                                                    if (p == length) break;
                                                }
                                            }
                                            do p--;
                                            while (Character.isSpace(data[p]));
                                        }
                                        break;
                                        case 8: {
                                            s = ++p;
                                            needsUnescape = false;
                                            outer:
                                            while (true) {
                                                switch (data[p]) {
                                                    case '\\':
                                                        needsUnescape = true;
                                                        p++;
                                                        break;
                                                    case '"':
                                                        break outer;
                                                }
                                                p++;
                                                if (p == length) break;
                                            }
                                            p--;
                                        }
                                        break;
                                    }
                                }
                            }

                        case 2:
                            if (cs == 0) {
                                _goto_targ = 5;
                                continue;
                            }
                            if (++p != length) {
                                _goto_targ = 1;
                                continue;
                            }
                        case 4:
                            if (p == length) {
                                int __acts = _json_eof_actions[cs];
                                int __nacts = _json_actions[__acts++];
                                while (__nacts-- > 0) {
                                    if (_json_actions[__acts++] == 1) {
                                        String value = new String(data, s, p - s);
                                        if (needsUnescape) value = unescape(value);
                                        if (stringIsName) {
                                            stringIsName = false;
                                            name = value;
                                        } else {
                                            value(name, value, stringIsUnquoted);
                                            name = null;
                                        }
                                        if (stop) return;
                                        stringIsUnquoted = false;
                                        s = p;
                                    }
                                }
                            }

                        case 5:
                    }
                    break;
                }
            }

            // line 252 "JsonSkimmer.rl"

        } catch (RuntimeException ex) {
            parseRuntimeEx = ex;
        }

        if (p < length) {
            int lineNumber = 1;
            for (int i = 0; i < p; i++)
                if (data[i] == '\n') lineNumber++;
            int start = Math.max(0, p - 32);
            throw new SerializationException("Error parsing JSON on line " + lineNumber + " near: "
                    + new String(data, start, p - start) + "*ERROR*" + new String(data, p, Math.min(64, length - p)), parseRuntimeEx);
        }
        if (parseRuntimeEx != null)
            throw new SerializationException("Error parsing JSON: " + new String(data), parseRuntimeEx);
    }

    /**
     * Causes parsing to stop after the current or next object, array, or value.
     */
    public void stop() {
        stop = true;
    }

    public boolean isStopped() {
        return stop;
    }

    /**
     * Called to unescape string values. The default implementation does standard JSON unescaping.
     */
    protected String unescape(String value) {
        int length = value.length();
        StringBuilder buffer = new StringBuilder(length + 16);
        for (int i = 0; i < length; ) {
            char c = value.charAt(i++);
            if (c != '\\') {
                buffer.append(c);
                continue;
            }
            if (i == length) break;
            c = value.charAt(i++);
            if (c == 'u') {
                buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
                i += 4;
                continue;
            }
            switch (c) {
                case '"':
                case '\\':
                case '/':
                    break;
                case 'b':
                    c = '\b';
                    break;
                case 'f':
                    c = '\f';
                    break;
                case 'n':
                    c = '\n';
                    break;
                case 'r':
                    c = '\r';
                    break;
                case 't':
                    c = '\t';
                    break;
                default:
                    throw new SerializationException("Illegal escaped character: \\" + c);
            }
            buffer.append(c);
        }
        return buffer.toString();
    }

    /**
     * Called when an object or array is encountered in the JSON.
     *
     * @param object True when an object was encountered, else it was an array.
     */
    protected void push(@Nullable String name, boolean object) {
    }

    /**
     * Called when the end of an object or array is encountered in the JSON.
     */
    protected void pop() {
    }

    /**
     * Called when a value is encountered in the JSON.
     */
    protected void value(@Nullable String name, String value, boolean unquoted) {
    }
}
