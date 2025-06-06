package com.badlogic.gdx.tools.texturepacker;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Unpacks a texture atlas into individual image files.
 */
public class TextureUnpacker {
    private static final String DEFAULT_OUTPUT_PATH = "output";
    private static final int NINEPATCH_PADDING = 1;
    private static final String OUTPUT_TYPE = "png";
    private static final String HELP = "Usage: atlasFile [imageDir] [outputDir]";
    private static final String ATLAS_FILE_EXTENSION = ".atlas";

    private boolean quiet;

    public static void main(String[] args) {
        TextureUnpacker unpacker = new TextureUnpacker();

        String atlasFile = null, imageDir = null, outputDir = null;

        // parse the arguments and display the help text if there is a problem with the command line arguments
        switch (unpacker.parseArguments(args)) {
            case 0:
                System.out.println(HELP);
                return;
            case 3:
                outputDir = args[2];
            case 2:
                imageDir = args[1];
            case 1:
                atlasFile = args[0];
        }

        File atlasFileHandle = new File(atlasFile).getAbsoluteFile();
        if (!atlasFileHandle.exists())
            throw new RuntimeException("Atlas file not found: " + atlasFileHandle.getAbsolutePath());
        String atlasParentPath = atlasFileHandle.getParentFile().getAbsolutePath();

        // Set the directory variables to a default when they weren't given in the variables
        if (imageDir == null) imageDir = atlasParentPath;
        if (outputDir == null) outputDir = (new File(atlasParentPath, DEFAULT_OUTPUT_PATH)).getAbsolutePath();

        // Opens the atlas file from the specified filename
        TextureAtlasData atlas = new TextureAtlasData(new FileHandle(atlasFile), new FileHandle(imageDir), false);
        unpacker.splitAtlas(atlas, outputDir);
    }

    /**
     * Checks the command line arguments for correctness.
     *
     * @return 0 If arguments are invalid, Number of arguments otherwise.
     */
    private int parseArguments(String[] args) {
        int numArgs = args.length;
        // check if number of args is right
        if (numArgs < 1) return 0;
        // check if the input file's extension is right
        boolean extension = args[0].endsWith(ATLAS_FILE_EXTENSION);
        // check if the directory names are valid
        boolean directory = true;
        if (numArgs >= 2) directory &= checkDirectoryValidity(args[1]);
        if (numArgs == 3) directory &= checkDirectoryValidity(args[2]);
        return extension && directory ? numArgs : 0;
    }

    private boolean checkDirectoryValidity(String directory) {
        File checkFile = new File(directory);
        boolean path = true;
        // try to get the canonical path, if this fails the path is not valid
        try {
            checkFile.getCanonicalPath();
        } catch (Exception e) {
            path = false;
        }
        return path;
    }

    /**
     * Splits an atlas into seperate image and ninepatch files.
     */
    public void splitAtlas(TextureAtlasData atlas, String outputDir) {
        // create the output directory if it did not exist yet
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
            if (!quiet) System.out.printf("Creating directory: %s%n", outputDirFile.getPath());
        }

        for (Page page : atlas.getPages()) {
            // load the image file belonging to this page as a Buffered Image
            File file = page.textureFile.file();
            if (!file.exists()) throw new RuntimeException("Unable to find atlas image: " + file.getAbsolutePath());
            BufferedImage img = null;
            try {
                img = ImageIO.read(file);
            } catch (IOException e) {
                printExceptionAndExit(e);
            }
            for (Region region : atlas.getRegions()) {
                if (!quiet)
                    System.out.printf("Processing image for %s: x[%s] y[%s] w[%s] h[%s], rotate[%s]%n",
                            region.name, region.left, region.top, region.width, region.height, region.rotate);

                // check if the page this region is in is currently loaded in a Buffered Image
                if (region.page == page) {
                    BufferedImage splitImage = null;
                    String extension = null;

                    // check if the region is a ninepatch or a normal image and delegate accordingly
                    if (region.findValue("split") == null) {
                        splitImage = extractImage(img, region, outputDirFile, 0);
                        if (region.width != region.originalWidth || region.height != region.originalHeight) {
                            BufferedImage originalImg = new BufferedImage(region.originalWidth, region.originalHeight, img.getType());
                            Graphics2D g2 = originalImg.createGraphics();
                            g2.drawImage(splitImage, (int) region.offsetX, (int) (region.originalHeight - region.height - region.offsetY),
                                    null);
                            g2.dispose();
                            splitImage = originalImg;
                        }
                        extension = OUTPUT_TYPE;
                    } else {
                        splitImage = extractNinePatch(img, region, outputDirFile);
                        extension = String.format("9.%s", OUTPUT_TYPE);
                    }

                    // check if the parent directories of this image file exist and create them if not
                    File imgOutput = new File(outputDirFile,
                            String.format("%s.%s", region.index == -1 ? region.name : region.name + "_" + region.index, extension));
                    File imgDir = imgOutput.getParentFile();
                    if (!imgDir.exists()) {
                        if (!quiet) System.out.printf("Creating directory: %s%n", imgDir.getPath());
                        imgDir.mkdirs();
                    }

                    // save the image
                    try {
                        ImageIO.write(splitImage, OUTPUT_TYPE, imgOutput);
                    } catch (Exception e) {
                        printExceptionAndExit(e);
                    }
                }
            }
        }
    }

    /**
     * Extract an image from a texture atlas.
     *
     * @param page          The image file related to the page the region is in
     * @param region        The region to extract
     * @param outputDirFile The output directory
     * @param padding       padding (in pixels) to apply to the image
     * @return The extracted image
     */
    private BufferedImage extractImage(BufferedImage page, Region region, File outputDirFile, int padding) {
        BufferedImage splitImage = null;

        // get the needed part of the page and rotate if needed
        if (region.rotate) {
            BufferedImage srcImage = page.getSubimage(region.left, region.top, region.height, region.width);
            splitImage = new BufferedImage(region.width, region.height, page.getType());

            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(90.0));
            transform.translate(0, -region.width);
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            op.filter(srcImage, splitImage);
        } else {
            splitImage = page.getSubimage(region.left, region.top, region.width, region.height);
        }

        // draw the image to a bigger one if padding is needed
        if (padding > 0) {
            BufferedImage paddedImage = new BufferedImage(splitImage.getWidth() + padding * 2, splitImage.getHeight() + padding * 2,
                    page.getType());
            Graphics2D g2 = paddedImage.createGraphics();
            g2.drawImage(splitImage, padding, padding, null);
            g2.dispose();
            return paddedImage;
        } else {
            return splitImage;
        }
    }

    /**
     * Extract a ninepatch from a texture atlas, according to the android specification.
     *
     * @param page   The image file related to the page the region is in
     * @param region The region to extract
     * @see <a href="http://developer.android.com/guide/topics/graphics/2d-graphics.html#nine-patch">ninepatch specification</a>
     */
    private BufferedImage extractNinePatch(BufferedImage page, Region region, File outputDirFile) {
        BufferedImage splitImage = extractImage(page, region, outputDirFile, NINEPATCH_PADDING);
        Graphics2D g2 = splitImage.createGraphics();
        g2.setColor(Color.BLACK);

        // Draw the four lines to save the ninepatch's padding and splits
        int[] splits = region.findValue("split");
        int startX = splits[0] + NINEPATCH_PADDING;
        int endX = region.width - splits[1] + NINEPATCH_PADDING - 1;
        int startY = splits[2] + NINEPATCH_PADDING;
        int endY = region.height - splits[3] + NINEPATCH_PADDING - 1;
        if (endX >= startX) g2.drawLine(startX, 0, endX, 0);
        if (endY >= startY) g2.drawLine(0, startY, 0, endY);
        int[] pads = region.findValue("pad");
        if (pads != null) {
            int padStartX = pads[0] + NINEPATCH_PADDING;
            int padEndX = region.width - pads[1] + NINEPATCH_PADDING - 1;
            int padStartY = pads[2] + NINEPATCH_PADDING;
            int padEndY = region.height - pads[3] + NINEPATCH_PADDING - 1;
            g2.drawLine(padStartX, splitImage.getHeight() - 1, padEndX, splitImage.getHeight() - 1);
            g2.drawLine(splitImage.getWidth() - 1, padStartY, splitImage.getWidth() - 1, padEndY);
        }
        g2.dispose();

        return splitImage;
    }

    private void printExceptionAndExit(Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }
}
