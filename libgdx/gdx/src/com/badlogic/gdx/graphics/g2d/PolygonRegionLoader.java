package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegionLoader.PolygonRegionParameters;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * loads {@link PolygonRegion PolygonRegions} using a {@link com.badlogic.gdx.graphics.g2d.PolygonRegionLoader}
 */
public class PolygonRegionLoader extends SynchronousAssetLoader<PolygonRegion, PolygonRegionParameters> {

    private final PolygonRegionParameters defaultParameters = new PolygonRegionParameters();
    private final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    public PolygonRegionLoader() {
        this(new InternalFileHandleResolver());
    }

    public PolygonRegionLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public PolygonRegion load(AssetManager manager, String fileName, FileHandle file, PolygonRegionParameters parameter) {
        Texture texture = manager.get(manager.getDependencies(fileName).first());
        return load(new TextureRegion(texture), file);
    }

    /**
     * If the PSH file contains a line starting with {@link PolygonRegionParameters#texturePrefix params.texturePrefix}, an
     * {@link AssetDescriptor} for the file referenced on that line will be added to the returned Array. Otherwise a sibling of the
     * given file with the same name and the first found extension in {@link PolygonRegionParameters#textureExtensions
     * params.textureExtensions} will be used. If no suitable file is found, the returned Array will be empty.
     */
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, PolygonRegionParameters params) {
        if (params == null) params = defaultParameters;
        String image = null;
        try {
            BufferedReader reader = file.reader(params.readerBuffer);
            for (String line = reader.readLine(); line != null; line = reader.readLine())
                if (line.startsWith(params.texturePrefix)) {
                    image = line.substring(params.texturePrefix.length());
                    break;
                }
            reader.close();
        } catch (IOException e) {
            throw new GdxRuntimeException("Error reading " + fileName, e);
        }

        if (image == null && params.textureExtensions != null) for (String extension : params.textureExtensions) {
            FileHandle sibling = file.sibling(file.nameWithoutExtension().concat("." + extension));
            if (sibling.exists()) image = sibling.name();
        }

        if (image != null) {
            Array<AssetDescriptor> deps = new Array<AssetDescriptor>(1);
            deps.add(new AssetDescriptor<Texture>(file.sibling(image), Texture.class));
            return deps;
        }

        return null;
    }

    /**
     * Loads a PolygonRegion from a PSH (Polygon SHape) file. The PSH file format defines the polygon vertices before
     * triangulation:
     * <p>
     * s 200.0, 100.0, ...
     * <p>
     * Lines not prefixed with "s" are ignored. PSH files can be created with external tools, eg: <br>
     * https://code.google.com/p/libgdx-polygoneditor/ <br>
     * http://www.codeandweb.com/physicseditor/
     *
     * @param file file handle to the shape definition file
     */
    public PolygonRegion load(TextureRegion textureRegion, FileHandle file) {
        BufferedReader reader = file.reader(256);
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                if (line.startsWith("s")) {
                    // Read shape.
                    String[] polygonStrings = line.substring(1).trim().split(",");
                    float[] vertices = new float[polygonStrings.length];
                    for (int i = 0, n = vertices.length; i < n; i++)
                        vertices[i] = Float.parseFloat(polygonStrings[i]);
                    // It would probably be better if PSH stored the vertices and triangles, then we don't have to triangulate here.
                    return new PolygonRegion(textureRegion, vertices, triangulator.computeTriangles(vertices).toArray());
                }
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading polygon shape file: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
        throw new GdxRuntimeException("Polygon shape not found: " + file);
    }

    public static class PolygonRegionParameters extends AssetLoaderParameters<PolygonRegion> {

        /**
         * what the line starts with that contains the file name of the texture for this {@code PolygonRegion}
         */
        public String texturePrefix = "i ";

        /**
         * what buffer size of the reader should be used to read the {@link #texturePrefix} line
         *
         * @see FileHandle#reader(int)
         */
        public int readerBuffer = 1024;

        /**
         * the possible file name extensions of the texture file
         */
        public String[] textureExtensions = new String[]{"png", "PNG", "jpeg", "JPEG", "jpg", "JPG", "cim", "CIM", "etc1", "ETC1",
                "ktx", "KTX", "zktx", "ZKTX"};
    }
}
