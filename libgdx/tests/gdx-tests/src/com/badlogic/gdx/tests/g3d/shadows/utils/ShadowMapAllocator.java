package com.badlogic.gdx.tests.g3d.shadows.utils;

import com.badlogic.gdx.graphics.g3d.environment.BaseLight;

/**
 * Shadow map allocator return texture region for each light.
 */
public interface ShadowMapAllocator {

    /**
     * Begin the texture allocation
     */
    void begin();

    /**
     * End the texture allocation
     */
    void end();

    /**
     * Find the next texture region for the current light
     *
     * @param light Current light
     * @return ShadowMapRegion or null if no more space on texture
     */
    ShadowMapRegion nextResult(BaseLight light);

    /**
     * Return shadow map width.
     *
     * @return int
     */
    int getWidth();

    /**
     * Return shadow map height.
     *
     * @return int
     */
    int getHeight();

    /**
     * Result of the allocator analyze
     */
    class ShadowMapRegion {
        public int x, y, width, height;
    }
}
