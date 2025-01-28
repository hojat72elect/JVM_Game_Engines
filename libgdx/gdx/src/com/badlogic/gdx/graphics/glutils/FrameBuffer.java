package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

/**
 * <p>
 * Encapsulates OpenGL ES 2.0 frame buffer objects. This is a simple helper class which should cover most FBO uses. It will
 * automatically create a texture for the color attachment and a renderbuffer for the depth buffer. You can get a hold of the
 * texture by {@link FrameBuffer#getColorBufferTexture()}. This class will only work with OpenGL ES 2.0.
 * </p>
 *
 * <p>
 * FrameBuffers are managed. In case of an OpenGL context loss, which only happens on Android when a user switches to another
 * application or receives an incoming call, the framebuffer will be automatically recreated.
 * </p>
 *
 * <p>
 * A FrameBuffer must be disposed if it is no longer needed
 * </p>
 * <p>
 * , realitix
 */
public class FrameBuffer extends GLFrameBuffer<Texture> {

    FrameBuffer() {
    }

    /**
     * Creates a GLFrameBuffer from the specifications provided by bufferBuilder
     *
     * @param bufferBuilder
     **/
    protected FrameBuffer(GLFrameBufferBuilder<? extends GLFrameBuffer<Texture>> bufferBuilder) {
        super(bufferBuilder);
    }

    /**
     * Creates a new FrameBuffer having the given dimensions and potentially a depth buffer attached.
     */
    public FrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth) {
        this(format, width, height, hasDepth, false);
    }

    /**
     * Creates a new FrameBuffer having the given dimensions and potentially a depth and a stencil buffer attached.
     *
     * @param format   the format of the color buffer; according to the OpenGL ES 2.0 spec, only RGB565, RGBA4444 and RGB5_A1 are
     *                 color-renderable
     * @param width    the width of the framebuffer in pixels
     * @param height   the height of the framebuffer in pixels
     * @param hasDepth whether to attach a depth buffer
     * @throws com.badlogic.gdx.utils.GdxRuntimeException in case the FrameBuffer could not be created
     */
    public FrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        FrameBufferBuilder frameBufferBuilder = new FrameBufferBuilder(width, height);
        frameBufferBuilder.addBasicColorTextureAttachment(format);
        if (hasDepth) frameBufferBuilder.addBasicDepthRenderBuffer();
        if (hasStencil) frameBufferBuilder.addBasicStencilRenderBuffer();
        this.bufferBuilder = frameBufferBuilder;

        build();
    }

    /**
     * See {@link GLFrameBuffer#unbind()}
     */
    public static void unbind() {
        GLFrameBuffer.unbind();
    }

    @Override
    protected Texture createTexture(FrameBufferTextureAttachmentSpec attachmentSpec) {
        GLOnlyTextureData data = new GLOnlyTextureData(bufferBuilder.width, bufferBuilder.height, 0, attachmentSpec.internalFormat,
                attachmentSpec.format, attachmentSpec.type);
        Texture result = new Texture(data);
        // Filtering support for depth textures on WebGL is spotty https://github.com/KhronosGroup/OpenGL-API/issues/84
        boolean webGLDepth = attachmentSpec.isDepth && Gdx.app.getType() == Application.ApplicationType.WebGL;
        if (!webGLDepth) {
            result.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }
        result.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
        return result;
    }

    @Override
    protected void disposeColorTexture(Texture colorTexture) {
        colorTexture.dispose();
    }

    @Override
    protected void attachFrameBufferColorTexture(Texture texture) {
        Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D,
                texture.getTextureObjectHandle(), 0);
    }
}
