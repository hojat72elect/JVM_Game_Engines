package com.badlogic.gdx.utils

import com.badlogic.gdx.files.FileHandle
import java.io.InputStream

interface BaseJsonReader {
    /**
     * If this function failed to read a JSON out of the given InputStream, a Null will be returned.
     */
    fun parse(input: InputStream): JsonValue?

    /**
     * If this function failed to read a JSON out of the given FileHandle, a Null will be returned.
     */
    fun parse(file: FileHandle): JsonValue?
}