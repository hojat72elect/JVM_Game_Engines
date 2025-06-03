package org.oreon.gl.components.filter.lensflare;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.util.ResourceLoader;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class LensFlareShader extends GLShaderProgram {

    private static LensFlareShader instance = null;

    protected LensFlareShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("shaders/filter/lens_flare/LensFlare_VS.glsl"));
        addFragmentShader(ResourceLoader.loadShader("shaders/filter/lens_flare/LensFlare_FS.glsl"));
        compileShader();

        addUniform("orthographicMatrix");
        addUniform("texture");
        addUniform("transparency");
    }

    public static LensFlareShader getInstance() {
        if (instance == null) {
            instance = new LensFlareShader();
        }
        return instance;
    }

    public void updateUniforms(Matrix4f orthographicMatrix) {
        setUniform("orthographicMatrix", orthographicMatrix);
    }

    public void updateUniforms(GLTexture texture, float transparency) {
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        setUniformi("texture", 0);
        setUniformf("transparency", transparency);
    }
}
