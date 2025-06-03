package org.oreon.core.gl.wrapper.texture;

import org.oreon.core.gl.texture.GLTexture;

import static org.lwjgl.opengl.GL30.*;

public class TextureImage2DArrray extends GLTexture {

    public TextureImage2DArrray(int width, int height, int layers, ImageFormat imageFormat) {

        super(GL_TEXTURE_2D_ARRAY, width, height);

        bind();

        switch (imageFormat) {
            case RGBA16FLOAT:
                allocateStorage3D(1, layers, GL_RGBA16F);
                break;
            case RGBA32FLOAT:
                allocateStorage3D(1, layers, GL_RGBA32F);
                break;
            case DEPTH32FLOAT:
                allocateStorage3D(1, layers, GL_DEPTH_COMPONENT32F);
                break;
            case R16FLOAT:
                allocateStorage3D(1, layers, GL_R16F);
                break;
            case R32FLOAT:
                allocateStorage3D(1, layers, GL_R32F);
                break;
            default:
                throw new IllegalArgumentException("Format not supported yet");
        }

        unbind();
    }

    public TextureImage2DArrray(int width, int height, int layers,
                                ImageFormat imageFormat, SamplerFilter samplerFilter) {

        this(width, height, layers, imageFormat);

        bind();

        switch (samplerFilter) {
            case Nearest:
                nearestFilter();
                break;
            case Bilinear:
                bilinearFilter();
                break;
            case Trilinear:
                trilinearFilter();
                break;
            case Anistropic:
                anisotropicFilter();
                break;
        }

        unbind();
    }

    public TextureImage2DArrray(int width, int height, int layers,
                                ImageFormat imageFormat, SamplerFilter samplerFilter, TextureWrapMode textureWrapMode) {

        this(width, height, layers, imageFormat, samplerFilter);

        bind();

        switch (textureWrapMode) {
            case ClampToBorder:
                clampToBorder();
                break;
            case ClampToEdge:
                clampToEdge();
                break;
            case MirrorRepeat:
                mirrorRepeat();
                break;
            case Repeat:
                repeat();
                break;
        }

        unbind();
    }

}
