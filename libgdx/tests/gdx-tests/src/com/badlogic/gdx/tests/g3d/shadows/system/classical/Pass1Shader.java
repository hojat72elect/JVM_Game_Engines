package com.badlogic.gdx.tests.g3d.shadows.system.classical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * This shader pack the depth data into the texture
 */
public class Pass1Shader extends DefaultShader {
    private static String defaultVertexShader = null;
    private static String defaultFragmentShader = null;

    public Pass1Shader(final Renderable renderable) {
        this(renderable, new Config());
    }

    public Pass1Shader(final Renderable renderable, final Config config) {
        this(renderable, config, createPrefix(renderable, config));
    }

    public Pass1Shader(final Renderable renderable, final Config config, final String prefix) {
        this(renderable, config, prefix, config.vertexShader != null ? config.vertexShader : getDefaultVertexShader(),
                config.fragmentShader != null ? config.fragmentShader : getDefaultFragmentShader());
    }

    public Pass1Shader(final Renderable renderable, final Config config, final String prefix, final String vertexShader,
                       final String fragmentShader) {
        this(renderable, config, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
    }

    public Pass1Shader(final Renderable renderable, final Config config, final ShaderProgram shaderProgram) {
        super(renderable, config, shaderProgram);
    }

    public static String getDefaultVertexShader() {
        if (defaultVertexShader == null) defaultVertexShader = Gdx.files
                .classpath("com/badlogic/gdx/tests/g3d/shadows/system/classical/pass1.vertex.glsl").readString();
        return defaultVertexShader;
    }

    public static String getDefaultFragmentShader() {
        if (defaultFragmentShader == null) defaultFragmentShader = Gdx.files
                .classpath("com/badlogic/gdx/tests/g3d/shadows/system/classical/pass1.fragment.glsl").readString();
        return defaultFragmentShader;
    }
}
