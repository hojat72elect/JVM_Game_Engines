@file:Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")

package org.openrndr.extra.fx.edges

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Filter1to1
import org.openrndr.extra.fx.fx_contour
import org.openrndr.extra.fx.mppFilterShader
import org.openrndr.extra.parameters.*

@Description("Contour")
class Contour : Filter1to1(mppFilterShader(fx_contour, "contour")) {
    @DoubleParameter("levels", 0.0, 16.0)
    var levels: Double by parameters

    @DoubleParameter("contour width", 0.0, 4.0)
    var contourWidth: Double by parameters

    @DoubleParameter("contour opacity", 0.0, 1.0)
    var contourOpacity: Double by parameters

    @DoubleParameter("background opacity", 0.0, 1.0)
    var backgroundOpacity: Double by parameters

    @DoubleParameter("bias", -1.0, 1.0)
    var bias: Double by parameters

    @ColorParameter("contour color")
    var contourColor: ColorRGBa by parameters

    @IntParameter("window", 0, 10)
    var window: Int by parameters

    @BooleanParameter("output bands", order = 100)
    var outputBands: Boolean by parameters

    @DoubleParameter("fade", 0.0, 1.0, order = 200)
    var fade: Double by parameters

    init {
        levels = 6.0
        contourWidth = 0.4
        contourColor = ColorRGBa.BLACK
        backgroundOpacity = 1.0
        contourOpacity = 1.0
        window = 1
        bias = 0.0
        outputBands = false
        fade = 1.0
    }
}
