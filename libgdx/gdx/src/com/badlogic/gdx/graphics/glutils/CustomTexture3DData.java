package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture3DData;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.ByteBuffer;

/**
 * A {@link Texture3DData} implementation that addresses 2 use cases :
 * <p>
 * You can use it as a GL only texture (feed by a compute shader). In this case the texture is not managed.
 * <p>
 * Or you can use it to upload pixels to GPU. In this case you should call {@link #getPixels()} to fill the buffer prior to
 * consuming it (eg. before new Texture3D(data)).
 */
public class CustomTexture3DData implements Texture3DData {

    private final int width;
    private final int height;
    private final int depth;
    private final int mipMapLevel;
    private final int glFormat;
    private final int glInternalFormat;
    private final int glType;
    private ByteBuffer pixels;

    /**
     * @see "https://registry.khronos.org/OpenGL-Refpages/es3.0/html/glTexImage3D.xhtml"
     */
    public CustomTexture3DData(int width, int height, int depth, int mipMapLevel, int glFormat, int glInternalFormat,
                               int glType
    ) {
        super();
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.glFormat = glFormat;
        this.glInternalFormat = glInternalFormat;
        this.glType = glType;
        this.mipMapLevel = mipMapLevel;
    }

    @Override
    public boolean isPrepared() {
        return true;
    }

    @Override
    public void prepare() {
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean useMipMaps() {
        return false;
    }

    @Override
    public boolean isManaged() {
        return pixels != null;
    }

    public int getInternalFormat() {
        return glInternalFormat;
    }

    public int getGLType() {
        return glType;
    }

    public int getGLFormat() {
        return glFormat;
    }

    public int getMipMapLevel() {
        return mipMapLevel;
    }

    public ByteBuffer getPixels() {
        if (pixels == null) {

            int numChannels;
            if (glFormat == GL30.GL_RED || glFormat == GL30.GL_RED_INTEGER || glFormat == GL30.GL_LUMINANCE
                    || glFormat == GL30.GL_ALPHA) {
                numChannels = 1;
            } else if (glFormat == GL30.GL_RG || glFormat == GL30.GL_RG_INTEGER || glFormat == GL30.GL_LUMINANCE_ALPHA) {
                numChannels = 2;
            } else if (glFormat == GL30.GL_RGB || glFormat == GL30.GL_RGB_INTEGER) {
                numChannels = 3;
            } else if (glFormat == GL30.GL_RGBA || glFormat == GL30.GL_RGBA_INTEGER) {
                numChannels = 4;
            } else {
                throw new GdxRuntimeException("unsupported glFormat: " + glFormat);
            }

            int bytesPerChannel;
            if (glType == GL30.GL_UNSIGNED_BYTE || glType == GL30.GL_BYTE) {
                bytesPerChannel = 1;
            } else if (glType == GL30.GL_UNSIGNED_SHORT || glType == GL30.GL_SHORT || glType == GL30.GL_HALF_FLOAT) {
                bytesPerChannel = 2;
            } else if (glType == GL30.GL_UNSIGNED_INT || glType == GL30.GL_INT || glType == GL30.GL_FLOAT) {
                bytesPerChannel = 4;
            } else {
                throw new GdxRuntimeException("unsupported glType: " + glType);
            }

            int bytesPerPixel = numChannels * bytesPerChannel;

            pixels = BufferUtils.newByteBuffer(width * height * depth * bytesPerPixel);
        }
        return pixels;
    }

    @Override
    public void consume3DData() {
        Gdx.gl30.glTexImage3D(GL30.GL_TEXTURE_3D, mipMapLevel, glInternalFormat, width, height, depth, 0, glFormat, glType, pixels);
    }
}
