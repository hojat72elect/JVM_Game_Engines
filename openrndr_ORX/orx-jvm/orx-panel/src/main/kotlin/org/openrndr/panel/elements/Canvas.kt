package org.openrndr.panel.elements

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.RenderTarget
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.math.Matrix44

class Canvas : Element(ElementType("canvas")) {
    var userDraw: ((Drawer) -> Unit)? = null
    private var renderTarget: RenderTarget? = null

    override fun draw(drawer: Drawer) {
        val width = screenArea.width.toInt()
        val height = screenArea.height.toInt()

        if (renderTarget != null) {
            if (renderTarget?.width != width || renderTarget?.height != height) {
                renderTarget?.colorBuffer(0)?.destroy()
                renderTarget?.destroy()
                renderTarget = null
            }
        }

        if (screenArea.width >= 1 && screenArea.height >= 1) {
            if (renderTarget == null) {
                renderTarget = renderTarget(screenArea.width.toInt(), screenArea.height.toInt(), drawer.context.contentScale) {
                    colorBuffer()
                    depthBuffer()
                }
            }

            renderTarget?.let { rt ->
                drawer.isolatedWithTarget(rt) {
                    model = Matrix44.IDENTITY
                    view = Matrix44.IDENTITY
                    clear(ColorRGBa.TRANSPARENT)
                    ortho(rt)
                    userDraw?.invoke(this)
                }
                drawer.image(rt.colorBuffer(0), 0.0, 0.0)
            }
        }
    }
}