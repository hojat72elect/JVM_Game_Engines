package com.badlogic.gdx.tools.headers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.FileProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class HeaderFixer {
    static int filesScanned;
    static int filesChanged;

    public static void process(String directory) throws Exception {
        HeaderFileProcessor processor = new HeaderFileProcessor();
        processor.process(new File(directory), new File(directory));
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            HeaderFixer.process("../../gdx/");
            HeaderFixer.process("../../backends/");
            HeaderFixer.process("../../tests/");
            HeaderFixer.process("../../extensions/");
        } else {
            HeaderFixer.process(args[0]);
        }
        System.out.println("Changed " + filesChanged + " / " + filesScanned + " files.");
    }

    static class HeaderFileProcessor extends FileProcessor {
        final String header;

        public HeaderFileProcessor() {
            header = new FileHandle("assets/licence-header.txt").readString();
            addInputSuffix(".java");
            setFlattenOutput(false);
            setRecursive(true);
        }

        @Override
        protected void processFile(Entry inputFile) throws Exception {
            filesScanned++;
            String content = new FileHandle(inputFile.inputFile).readString();
            if (content.trim().startsWith("package")) {
                System.out.println("File '" + inputFile.inputFile + "' header fixed");
                filesChanged++;
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileHandle(inputFile.outputFile).write(false)));

                writer.write(header + "\n\n" + content);
                writer.close();
            }
        }

        @Override
        protected void processDir(Entry inputDir, ArrayList<Entry> value) throws Exception {
        }
    }
}
