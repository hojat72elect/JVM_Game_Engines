package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.JsonWriter.OutputType;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Builder API for emitting JSON to a string.
 */
public class JsonString {
    final StringBuilder buffer;
    private final Array<JsonObject> stack = new Array();
    private JsonObject current;
    private boolean named;
    private OutputType outputType = OutputType.json;
    private boolean quoteLongValues = false;

    public JsonString() {
        buffer = new StringBuilder();
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    /**
     * Sets the type of JSON output. Default is {@link OutputType#minimal}.
     */
    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    /**
     * When true, quotes long, double, BigInteger, BigDecimal types to prevent truncation in languages like JavaScript and PHP.
     * This is not necessary when using libgdx, which handles these types without truncation. Default is false.
     */
    public void setQuoteLongValues(boolean quoteLongValues) {
        this.quoteLongValues = quoteLongValues;
    }

    public JsonString name(String name) {
        if (current == null || current.array) throw new IllegalStateException("Current item must be an object.");
        if (!current.needsComma)
            current.needsComma = true;
        else
            buffer.append(',');
        buffer.append(outputType.quoteName(name));
        buffer.append(':');
        named = true;
        return this;
    }

    public JsonString object() {
        requireCommaOrName();
        stack.add(current = new JsonObject(false));
        return this;
    }

    public JsonString array() {
        requireCommaOrName();
        stack.add(current = new JsonObject(true));
        return this;
    }

    public JsonString value(@Nullable Object value) {
        if (quoteLongValues
                && (value instanceof Long || value instanceof Double || value instanceof BigDecimal || value instanceof BigInteger)) {
            value = value.toString();
        } else if (value instanceof Number) {
            Number number = (Number) value;
            long longValue = number.longValue();
            if (number.doubleValue() == longValue) value = longValue;
        }
        requireCommaOrName();
        buffer.append(outputType.quoteValue(value));
        return this;
    }

    /**
     * Writes the specified JSON value, without quoting or escaping.
     */
    public JsonString json(String json) {
        requireCommaOrName();
        buffer.append(json);
        return this;
    }

    private void requireCommaOrName() {
        if (current == null) return;
        if (current.array) {
            if (!current.needsComma)
                current.needsComma = true;
            else
                buffer.append(',');
        } else {
            if (!named) throw new IllegalStateException("Name must be set.");
            named = false;
        }
    }

    public JsonString object(String name) {
        return name(name).object();
    }

    public JsonString array(String name) {
        return name(name).array();
    }

    public JsonString set(String name, Object value) {
        return name(name).value(value);
    }

    /**
     * Writes the specified JSON value, without quoting or escaping.
     */
    public JsonString json(String name, String json) {
        return name(name).json(json);
    }

    public JsonString pop() {
        if (named) throw new IllegalStateException("Expected an object, array, or value since a name was set.");
        stack.pop().close();
        current = stack.size == 0 ? null : stack.peek();
        return this;
    }

    public JsonString close() {
        while (stack.size > 0)
            pop();
        return this;
    }

    public void reset() {
        buffer.clear();
        stack.clear();
        current = null;
        named = false;
    }

    public String toString() {
        return buffer.toString();
    }

    private class JsonObject {
        final boolean array;
        boolean needsComma;

        JsonObject(boolean array) {
            this.array = array;
            buffer.append(array ? '[' : '{');
        }

        void close() {
            buffer.append(array ? ']' : '}');
        }
    }
}
