package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.math.Rectangle;

import org.jetbrains.annotations.Nullable;

/**
 * Allows a parent to set the area that is visible on a child actor to allow the child to cull when drawing itself. This must
 * only be used for actors that are not rotated or scaled.
 */
public interface Cullable {
    /**
     * @param cullingArea The culling area in the child actor's coordinates.
     */
    void setCullingArea(@Nullable Rectangle cullingArea);
}
