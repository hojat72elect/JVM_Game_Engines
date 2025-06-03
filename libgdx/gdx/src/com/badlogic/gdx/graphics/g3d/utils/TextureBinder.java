package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;

/**
 * Responsible for binding textures, may implement a strategy to avoid binding a texture unnecessarily. A TextureBinder may
 * decide to which texture unit it binds a texture.
 * <p>
 * , Xoppa
 */
public interface TextureBinder {
    /**
     * Prepares the binder for operation, must be matched with a call to {@link #end()}.
     */
    void begin();

    /**
     * Disables all used texture units and unbinds textures. Resets the counts.
     */
    void end();

    /**
     * Binds the texture to an available unit and applies the filters in the descriptor.
     *
     * @param textureDescriptor the {@link TextureDescriptor}
     * @return the unit the texture was bound to
     */
    int bind(TextureDescriptor textureDescriptor);

    /**
     * Binds the texture to an available unit.
     *
     * @param texture the {@link Texture}
     * @return the unit the texture was bound to
     */
    int bind(GLTexture texture);

    /**
     * @return the number of binds actually executed since the last call to {@link #resetCounts()}
     */
    int getBindCount();

    /**
     * @return the number of binds that could be avoided by reuse
     */
    int getReuseCount();

    /**
     * Resets the bind/reuse counts
     */
    void resetCounts();
}
