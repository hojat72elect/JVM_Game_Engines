package com.badlogic.gdx.tests.g3d.shadows.system.classical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * This shader is used by the classical shadow system. This shader supports normal mapping and specular mapping
 */
public class MainShader extends DefaultShader {
    protected static ClassicalShadowSystem shadowSystem;
    private static String defaultVertexShader = null;
    private static String defaultFragmentShader = null;

    public MainShader(final Renderable renderable, final Config config) {
        this(renderable, config, createPrefix(renderable, config));
    }

    public MainShader(final Renderable renderable, final Config config, final String prefix) {
        this(renderable, config, prefix, getDefaultVertexShader(), getDefaultFragmentShader());
    }

    public MainShader(final Renderable renderable, final Config config, final String prefix, final String vertexShader,
                      final String fragmentShader) {
        this(renderable, config, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
    }

    public MainShader(final Renderable renderable, final Config config, final ShaderProgram shaderProgram) {
        super(renderable, config, shaderProgram);

        shadowSystem = config.shadowSystem;
        register(Inputs.shadowTexture, Setters.shadowTexture);
        register(Inputs.resolution, Setters.resolution);
    }

    public static String getDefaultVertexShader() {
        if (defaultVertexShader == null) defaultVertexShader = Gdx.files
                .classpath("com/badlogic/gdx/tests/g3d/shadows/system/classical/main.vertex.glsl").readString();
        return defaultVertexShader;
    }

    public static String getDefaultFragmentShader() {
        if (defaultFragmentShader == null) defaultFragmentShader = Gdx.files
                .classpath("com/badlogic/gdx/tests/g3d/shadows/system/classical/main.fragment.glsl").readString();
        return defaultFragmentShader;
    }

    public static String createPrefix(final Renderable renderable, final Config config) {
        return DefaultShader.createPrefix(renderable, config);
    }

    public static class Config extends DefaultShader.Config {
        public ClassicalShadowSystem shadowSystem;

        public Config(ClassicalShadowSystem shadowSystem) {
            super();
            numBones = 12;
            numPointLights = 2;
            numSpotLights = 5;
            numDirectionalLights = 2;
            this.shadowSystem = shadowSystem;
        }
    }

    public static class Inputs extends DefaultShader.Inputs {
        public final static Uniform shadowTexture = new Uniform("u_shadowTexture");
        public final static Uniform resolution = new Uniform("u_resolution");
    }

    public static class Setters extends DefaultShader.Setters {
        public final static Setter shadowTexture = new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shadowSystem.getMainTexture());
            }
        };
        public final static Setter resolution = new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                // Value must be float type to work !
                shader.set(inputID, (float) Gdx.graphics.getBackBufferWidth(), (float) Gdx.graphics.getBackBufferHeight());
            }
        };
    }
}
