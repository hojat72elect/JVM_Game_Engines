package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Returns a list of {@link Renderable} instances to be rendered by a {@link ModelBatch}.
 */
public interface RenderableProvider {
    /**
     * Returns {@link Renderable} instances. Renderables are obtained from the provided {@link Pool} and added to the provided
     * array. The Renderables obtained using {@link Pool#obtain()} will later be put back into the pool, do not store them
     * internally. The resulting array can be rendered via a {@link ModelBatch}.
     *
     * @param renderables the output array
     * @param pool        the pool to obtain Renderables from
     */
    void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool);
}
