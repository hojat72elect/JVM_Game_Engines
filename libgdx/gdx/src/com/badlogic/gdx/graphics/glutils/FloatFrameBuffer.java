package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * This is a {@link FrameBuffer} variant backed by a float texture.
 */
public class FloatFrameBuffer extends FrameBuffer {

    FloatFrameBuffer() {
        checkExtensions();
    }

    /**
     * Creates a GLFrameBuffer from the specifications provided by bufferBuilder
     *
     * @param bufferBuilder
     **/
    protected FloatFrameBuffer(GLFrameBufferBuilder<? extends GLFrameBuffer<Texture>> bufferBuilder) {
        super(bufferBuilder);
        checkExtensions();
    }

    /**
     * Creates a new FrameBuffer with a float backing texture, having the given dimensions and potentially a depth buffer
     * attached.
     *
     * @param width    the width of the framebuffer in pixels
     * @param height   the height of the framebuffer in pixels
     * @param hasDepth whether to attach a depth buffer
     * @throws GdxRuntimeException in case the FrameBuffer could not be created
     */
    public FloatFrameBuffer(int width, int height, boolean hasDepth) {
        checkExtensions();
        FloatFrameBufferBuilder bufferBuilder = new FloatFrameBufferBuilder(width, height);
        bufferBuilder.addFloatAttachment(GL30.GL_RGBA32F, GL30.GL_RGBA, GL30.GL_FLOAT, false);
        if (hasDepth) bufferBuilder.addBasicDepthRenderBuffer();
        this.bufferBuilder = bufferBuilder;

        build();
    }

    @Override
    protected Texture createTexture(FrameBufferTextureAttachmentSpec attachmentSpec) {
        FloatTextureData data = new FloatTextureData(bufferBuilder.width, bufferBuilder.height, attachmentSpec.internalFormat,
                attachmentSpec.format, attachmentSpec.type, attachmentSpec.isGpuOnly);
        Texture result = new Texture(data);
        if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.Applet)
            result.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        else
            // no filtering for float textures in OpenGL ES
            result.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        result.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
        return result;
    }

    /**
     * Check for support for any required extensions on the current platform.
     */
    private void checkExtensions() {
        if (Gdx.graphics.isGL30Available() && Gdx.app.getType() == ApplicationType.WebGL) {
            // For WebGL2, Rendering to a Floating Point Texture requires this extension
            if (!Gdx.graphics.supportsExtension("EXT_color_buffer_float"))
                throw new GdxRuntimeException("Extension EXT_color_buffer_float not supported!");
        }
    }
}
