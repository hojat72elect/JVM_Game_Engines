package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;


public class PolygonSprite {
    private final Color color = new Color(1f, 1f, 1f, 1f);
    PolygonRegion region;
    private float x, y;
    private float width, height;
    private float scaleX = 1f, scaleY = 1f;
    private float rotation;
    private float originX, originY;
    private float[] vertices;
    private boolean dirty;
    private final Rectangle bounds = new Rectangle();

    public PolygonSprite(PolygonRegion region) {
        setRegion(region);
        setSize(region.region.regionWidth, region.region.regionHeight);
        setOrigin(width / 2, height / 2);
    }

    /**
     * Creates a sprite that is a copy in every way of the specified sprite.
     */
    public PolygonSprite(PolygonSprite sprite) {
        set(sprite);
    }

    public void set(PolygonSprite sprite) {
        if (sprite == null) throw new IllegalArgumentException("sprite cannot be null.");

        setRegion(sprite.region);

        x = sprite.x;
        y = sprite.y;
        width = sprite.width;
        height = sprite.height;
        originX = sprite.originX;
        originY = sprite.originY;
        rotation = sprite.rotation;
        scaleX = sprite.scaleX;
        scaleY = sprite.scaleY;
        color.set(sprite.color);
    }

    /**
     * Sets the position and size of the sprite when drawn, before scaling and rotation are applied. If origin, rotation, or scale
     * are changed, it is slightly more efficient to set the bounds after those operations.
     */
    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        dirty = true;
    }

    /**
     * Sets the size of the sprite when drawn, before scaling and rotation are applied. If origin, rotation, or scale are changed,
     * it is slightly more efficient to set the size after those operations. If both position and size are to be changed, it is
     * better to use {@link #setBounds(float, float, float, float)}.
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;

        dirty = true;
    }

    /**
     * Sets the position where the sprite will be drawn. If origin, rotation, or scale are changed, it is slightly more efficient
     * to set the position after those operations. If both position and size are to be changed, it is better to use
     * {@link #setBounds(float, float, float, float)}.
     */
    public void setPosition(float x, float y) {
        translate(x - this.x, y - this.y);
    }

    /**
     * Sets the x position relative to the current position where the sprite will be drawn. If origin, rotation, or scale are
     * changed, it is slightly more efficient to translate after those operations.
     */
    public void translateX(float xAmount) {
        this.x += xAmount;

        if (dirty) return;

        final float[] vertices = this.vertices;
        for (int i = 0; i < vertices.length; i += Sprite.VERTEX_SIZE)
            vertices[i] += xAmount;
    }

    /**
     * Sets the y position relative to the current position where the sprite will be drawn. If origin, rotation, or scale are
     * changed, it is slightly more efficient to translate after those operations.
     */
    public void translateY(float yAmount) {
        y += yAmount;

        if (dirty) return;

        final float[] vertices = this.vertices;
        for (int i = 1; i < vertices.length; i += Sprite.VERTEX_SIZE)
            vertices[i] += yAmount;
    }

    /**
     * Sets the position relative to the current position where the sprite will be drawn. If origin, rotation, or scale are
     * changed, it is slightly more efficient to translate after those operations.
     */
    public void translate(float xAmount, float yAmount) {
        x += xAmount;
        y += yAmount;

        if (dirty) return;

        final float[] vertices = this.vertices;
        for (int i = 0; i < vertices.length; i += Sprite.VERTEX_SIZE) {
            vertices[i] += xAmount;
            vertices[i + 1] += yAmount;
        }
    }

    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
        float packedColor = color.toFloatBits();
        final float[] vertices = this.vertices;
        for (int i = 2; i < vertices.length; i += Sprite.VERTEX_SIZE)
            vertices[i] = packedColor;
    }

    /**
     * Sets the origin in relation to the sprite's position for scaling and rotation.
     */
    public void setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        dirty = true;
    }

    /**
     * Sets the sprite's rotation relative to the current rotation.
     */
    public void rotate(float degrees) {
        rotation += degrees;
        dirty = true;
    }

    public void setScale(float scaleXY) {
        this.scaleX = scaleXY;
        this.scaleY = scaleXY;
        dirty = true;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        dirty = true;
    }

    /**
     * Sets the sprite's scale relative to the current scale.
     */
    public void scale(float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        dirty = true;
    }

    /**
     * Returns the packed vertices, colors, and texture coordinates for this sprite.
     */
    public float[] getVertices() {
        if (!dirty) return vertices;
        dirty = false;

        final float originX = this.originX;
        final float originY = this.originY;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final PolygonRegion region = this.region;
        final float[] vertices = this.vertices;
        final float[] regionVertices = region.vertices;

        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        final float sX = width / region.region.getRegionWidth();
        final float sY = height / region.region.getRegionHeight();
        final float cos = MathUtils.cosDeg(rotation);
        final float sin = MathUtils.sinDeg(rotation);

        float fx, fy;
        for (int i = 0, v = 0, n = regionVertices.length; i < n; i += 2, v += 5) {
            fx = (regionVertices[i] * sX - originX) * scaleX;
            fy = (regionVertices[i + 1] * sY - originY) * scaleY;
            vertices[v] = cos * fx - sin * fy + worldOriginX;
            vertices[v + 1] = sin * fx + cos * fy + worldOriginY;
        }
        return vertices;
    }

    /**
     * Returns the bounding axis aligned {@link Rectangle} that bounds this sprite. The rectangles x and y coordinates describe
     * its bottom left corner. If you change the position or size of the sprite, you have to fetch the triangle again for it to be
     * recomputed.
     *
     * @return the bounding Rectangle
     */
    public Rectangle getBoundingRectangle() {
        final float[] vertices = getVertices();

        float minx = vertices[0];
        float miny = vertices[1];
        float maxx = vertices[0];
        float maxy = vertices[1];

        for (int i = 5; i < vertices.length; i += 5) {
            float x = vertices[i];
            float y = vertices[i + 1];
            minx = minx > x ? x : minx;
            maxx = maxx < x ? x : maxx;
            miny = miny > y ? y : miny;
            maxy = maxy < y ? y : maxy;
        }

        bounds.x = minx;
        bounds.y = miny;
        bounds.width = maxx - minx;
        bounds.height = maxy - miny;
        return bounds;
    }

    public void draw(PolygonSpriteBatch spriteBatch) {
        final PolygonRegion region = this.region;
        spriteBatch.draw(region.region.texture, getVertices(), 0, vertices.length, region.triangles, 0, region.triangles.length);
    }

    public void draw(PolygonSpriteBatch spriteBatch, float alphaModulation) {
        Color color = getColor();
        float oldAlpha = color.a;
        color.a *= alphaModulation;
        setColor(color);
        draw(spriteBatch);
        color.a = oldAlpha;
        setColor(color);
    }

    public float getX() {
        return x;
    }

    /**
     * Sets the x position where the sprite will be drawn. If origin, rotation, or scale are changed, it is slightly more
     * efficient to set the position after those operations. If both position and size are to be changed, it is better to use
     * {@link #setBounds(float, float, float, float)}.
     */
    public void setX(float x) {
        translateX(x - this.x);
    }

    public float getY() {
        return y;
    }

    /**
     * Sets the y position where the sprite will be drawn. If origin, rotation, or scale are changed, it is slightly more
     * efficient to set the position after those operations. If both position and size are to be changed, it is better to use
     * {@link #setBounds(float, float, float, float)}.
     */
    public void setY(float y) {
        translateY(y - this.y);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float degrees) {
        this.rotation = degrees;
        dirty = true;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    /**
     * Returns the color of this sprite. Modifying the returned color will have unexpected effects unless {@link #setColor(Color)}
     * or {@link #setColor(float, float, float, float)} is subsequently called before drawing this sprite.
     */
    public Color getColor() {
        return color;
    }

    public void setColor(Color tint) {
        color.set(tint);
        float color = tint.toFloatBits();

        final float[] vertices = this.vertices;
        for (int i = 2; i < vertices.length; i += Sprite.VERTEX_SIZE)
            vertices[i] = color;
    }

    /**
     * Returns the actual color used in the vertices of this sprite. Modifying the returned color will have unexpected effects
     * unless {@link #setColor(Color)} or {@link #setColor(float, float, float, float)} is subsequently called before drawing this
     * sprite.
     */
    public Color getPackedColor() {
        Color.abgr8888ToColor(color, vertices[2]);
        return color;
    }

    public PolygonRegion getRegion() {
        return region;
    }

    public void setRegion(PolygonRegion region) {
        this.region = region;

        float[] regionVertices = region.vertices;
        float[] textureCoords = region.textureCoords;

        int verticesLength = (regionVertices.length / 2) * 5;
        if (vertices == null || vertices.length != verticesLength) vertices = new float[verticesLength];

        // Set the color and UVs in this sprite's vertices.
        float floatColor = color.toFloatBits();
        float[] vertices = this.vertices;
        for (int i = 0, v = 2; v < verticesLength; i += 2, v += 5) {
            vertices[v] = floatColor;
            vertices[v + 1] = textureCoords[i];
            vertices[v + 2] = textureCoords[i + 1];
        }

        dirty = true;
    }
}
