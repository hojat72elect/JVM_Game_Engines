/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.app.scene.Viewport
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component

/**
 * Removes an entity if it is outside of the visible area of the viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class OffscreenCleanComponent
@JvmOverloads constructor(val viewport: Viewport = FXGL.getGameScene().viewport) : Component() {

    override fun onUpdate(tpf: Double) {
        if (entity.boundingBoxComponent.isOutside(viewport.visibleArea)) {
            entity.removeFromWorld()
        }
    }

    override fun isComponentInjectionRequired(): Boolean = false
}