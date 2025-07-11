package org.openrndr.draw

import org.openrndr.color.ColorRGBa
import org.openrndr.internal.Driver
import org.openrndr.shape.Rectangle

private val shadeStyleManager by lazy {
    ShadeStyleManager.fromGenerators(
        "shade-style-filter",
        vsGenerator = Driver.instance.shaderGenerators::filterVertexShader,
        fsGenerator = Driver.instance.shaderGenerators::filterFragmentShader
    )
}

/**
 * A [Filter] that uses the [ShadeStyle] interface and language
 * @param shadeStyle the [ShadeStyle] that is applied as a filter
 */
class ShadeStyleFilter(val shadeStyle: ShadeStyle) : Filter(), StyleParameters by shadeStyle {
    var fill: ColorRGBa by parameters

    init {
        fill = ColorRGBa.WHITE
        shadeStyle.parameter("fill", fill)
    }
    override fun apply(source: Array<ColorBuffer>, target: Array<ColorBuffer>, clip: Rectangle?) {
        shadeStyle.parameter("fill", fill)
        shadeStyle.textureBaseIndex = source.size

        for ((index, buffer) in source.withIndex()) {
            shadeStyle.parameter("input$index", buffer)
        }
        val shader = shadeStyleManager.shader(shadeStyle, format)
        val realFilter = Filter(shader)
        realFilter.apply(source, target, clip)
    }
}


class ShadeStyleFilter1to1(val shadeStyle: ShadeStyle) : Filter1to1(), StyleParameters by shadeStyle {
    var fill: ColorRGBa by parameters

    init {
        fill = ColorRGBa.WHITE
        shadeStyle.parameter("fill", fill)
    }
    override fun apply(source: Array<ColorBuffer>, target: Array<ColorBuffer>, clip: Rectangle?) {
        shadeStyle.parameter("fill", fill)
        shadeStyle.textureBaseIndex = source.size

        for ((index, buffer) in source.withIndex()) {
            shadeStyle.parameter("input$index", buffer)
        }
        val shader = shadeStyleManager.shader(shadeStyle, format)
        val realFilter = Filter1to1(shader)
        realFilter.apply(source, target, clip)
    }
}


class ShadeStyleFilter2to1(val shadeStyle: ShadeStyle) : Filter2to1(), StyleParameters by shadeStyle {
    var fill: ColorRGBa by parameters

    init {
        fill = ColorRGBa.WHITE
        shadeStyle.parameter("fill", fill)
    }
    override fun apply(source: Array<ColorBuffer>, target: Array<ColorBuffer>, clip: Rectangle?) {
        shadeStyle.parameter("fill", fill)
        shadeStyle.textureBaseIndex = source.size
        for ((index, buffer) in source.withIndex()) {
            shadeStyle.parameter("input$index", buffer)
        }
        val shader = shadeStyleManager.shader(shadeStyle, format)
        val realFilter = Filter1to1(shader)
        realFilter.apply(source, target, clip)
    }
}

