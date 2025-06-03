package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;

import java.io.InputStream;

public interface BaseJsonReader {
    JsonValue parse(InputStream input);

    JsonValue parse(FileHandle file);
}
