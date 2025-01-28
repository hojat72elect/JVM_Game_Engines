package com.badlogic.gdx.graphics.g2d;

/**
 * Defines a polygon shape on top of a texture region to avoid drawing transparent pixels.
 *
 * @see PolygonRegionLoader
 */
public class PolygonRegion {
    final float[] textureCoords; // texture coordinates in atlas coordinates
    final float[] vertices; // pixel coordinates relative to source image.
    final short[] triangles;
    final TextureRegion region;

    /**
     * Creates a PolygonRegion by triangulating the polygon coordinates in vertices and calculates uvs based on that.
     * TextureRegion can come from an atlas.
     *
     * @param region   the region used for drawing
     * @param vertices contains 2D polygon coordinates in pixels relative to source region
     */
    public PolygonRegion(TextureRegion region, float[] vertices, short[] triangles) {
        this.region = region;
        this.vertices = vertices;
        this.triangles = triangles;

        float[] textureCoords = this.textureCoords = new float[vertices.length];
        float u = region.u, v = region.v;
        float uvWidth = region.u2 - u;
        float uvHeight = region.v2 - v;
        int width = region.regionWidth;
        int height = region.regionHeight;
        for (int i = 0, n = vertices.length; i < n; i += 2) {
            textureCoords[i] = u + uvWidth * (vertices[i] / width);
            textureCoords[i + 1] = v + uvHeight * (1 - (vertices[i + 1] / height));
        }
    }

    /**
     * Returns the vertices in local space.
     */
    public float[] getVertices() {
        return vertices;
    }

    public short[] getTriangles() {
        return triangles;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public TextureRegion getRegion() {
        return region;
    }
}
