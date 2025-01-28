package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Disposable;

/**
 * Returns {@link Shader} instances for a {@link Renderable} on request. Also responsible for disposing of any created
 * {@link Shader} instances on a call to {@link Disposable#dispose()}.
 *
 * 
 */
public interface ShaderProvider extends Disposable {
    /**
     * Returns a {@link Shader} for the given {@link Renderable}. The RenderInstance may already contain a Shader, in which case
     * the provider may decide to return that.
     *
     * @param renderable the Renderable
     * @return the Shader to be used for the RenderInstance
     */
    Shader getShader(Renderable renderable);

}
