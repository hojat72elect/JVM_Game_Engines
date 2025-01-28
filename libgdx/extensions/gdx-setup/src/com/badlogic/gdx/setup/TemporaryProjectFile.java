package com.badlogic.gdx.setup;

import java.io.File;

/**
 * A temporary file that wraps {@link ProjectFile} for use in a {@link Project}.
 */
public class TemporaryProjectFile extends ProjectFile {

    /**
     * The temporary file
     **/
    public File file;

    public TemporaryProjectFile(File file, String outputString, boolean isTemplate) {
        super(outputString, isTemplate);
        this.file = file;
    }

}
