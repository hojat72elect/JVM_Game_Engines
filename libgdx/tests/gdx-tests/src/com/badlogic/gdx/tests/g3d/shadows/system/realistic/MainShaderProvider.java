package com.badlogic.gdx.tests.g3d.shadows.system.realistic;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 *
 */
public class MainShaderProvider extends BaseShaderProvider {
    public final MainShader.Config config;

    public MainShaderProvider(final MainShader.Config config) {
        if (config == null) throw new GdxRuntimeException("MainShaderProvider needs config");
        this.config = config;
    }

    @Override
    protected Shader createShader(final Renderable renderable) {
        return new MainShader(renderable, config);
    }
}
