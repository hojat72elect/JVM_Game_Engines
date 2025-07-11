package org.openrndr.extra.color.spaces

import kotlinx.serialization.Serializable
import org.openrndr.color.AlgebraicColor
import org.openrndr.color.ColorModel
import org.openrndr.color.ColorRGBa
import org.openrndr.color.Linearity
import org.openrndr.color.LuminosityColor
import org.openrndr.color.ShadableColor
import org.openrndr.math.Vector4
import kotlin.jvm.JvmRecord
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign


/**
 * Represents a color in the OKLAB color space with an optional alpha (transparency) value.
 * OKLAB is a perceptual color space designed to represent colors in a way that aligns
 * with human visual perception, offering an alternative to traditional color spaces like RGB.
 *
 * @property l The lightness component of the color. A value between 0 (black) and 1 (white).
 * @property a The 'a' component in the OKLAB color space, representing the first chromatic axis.
 * @property b The 'b' component in the OKLAB color space, representing the second chromatic axis.
 * @property alpha The alpha (opacity) value of the color. A value between 0.0 (completely transparent) and 1.0 (completely opaque).
 */
@Suppress("LocalVariableName")
@Serializable
@JvmRecord
data class ColorOKLABa(val l: Double, val a: Double, val b: Double, override val alpha: Double = 1.0) :
    ColorModel<ColorOKLABa>,
    ShadableColor<ColorOKLABa>,
    LuminosityColor<ColorOKLABa>,
    AlgebraicColor<ColorOKLABa> {

    companion object {
        fun fromRGBa(rgba: ColorRGBa): ColorOKLABa {
            // based on https://bottosson.github.io/posts/oklab/
            val c = rgba.toLinear()
            val l = 0.4122214708 * c.r + 0.5363325363 * c.g + 0.0514459929 * c.b
            val m = 0.2119034982 * c.r + 0.6806995451 * c.g + 0.1073969566 * c.b
            val s = 0.0883024619 * c.r + 0.2817188376 * c.g + 0.6299787005 * c.b

            val lnl = abs(l).pow(1.0 / 3.0) * sign(l)
            val mnl = abs(m).pow(1.0 / 3.0) * sign(m)
            val snl = abs(s).pow(1.0 / 3.0) * sign(s)


            val L = 0.2104542553 * lnl + 0.7936177850 * mnl - 0.0040720468 * snl
            val a = 1.9779984951 * lnl - 2.4285922050 * mnl + 0.4505937099 * snl
            val b = 0.0259040371 * lnl + 0.7827717662 * mnl - 0.8086757660 * snl

            return ColorOKLABa(L, a, b, c.alpha)
        }
    }

    override fun toRGBa(): ColorRGBa {
        // based on https://bottosson.github.io/posts/oklab/
        val lnl = l + 0.3963377774 * a + 0.2158037573 * b
        val mnl = l - 0.1055613458 * a - 0.0638541728 * b
        val snl = l - 0.0894841775 * a - 1.2914855480 * b

        val l = lnl * lnl * lnl
        val m = mnl * mnl * mnl
        val s = snl * snl * snl

        return ColorRGBa(
            4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
            -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
            -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s,
            alpha,
            Linearity.LINEAR
        )
    }

    fun toOKLCHa() = ColorOKLCHa.fromColorOKLABa(this)

    override fun shade(factor: Double) = ColorOKLABa(l * factor, a, b, alpha)
    override fun opacify(factor: Double) = ColorOKLABa(l, a, b, alpha * factor)
    override fun minus(right: ColorOKLABa) = ColorOKLABa(l - right.l, a - right.a, b - right.b, alpha - right.alpha)
    override fun plus(right: ColorOKLABa) = ColorOKLABa(l + right.l, a + right.a, b + right.b, alpha + right.alpha)
    override fun times(scale: Double) = ColorOKLABa(l * scale, a * scale, b * scale, alpha * scale)

    override fun toVector4() = Vector4(l, a, b, alpha)
    override val luminosity: Double
        get() = l * 100.0

    override fun withLuminosity(luminosity: Double): ColorOKLABa = copy(l = luminosity / 100.0)
}

fun ColorRGBa.toOKLABa() = ColorOKLABa.fromRGBa(this)