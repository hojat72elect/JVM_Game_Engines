package org.openrndr.color

import kotlinx.serialization.Serializable
import org.openrndr.math.Vector4
import org.openrndr.math.asDegrees
import org.openrndr.math.asRadians
import org.openrndr.math.mixAngle
import kotlin.math.*

/**
 * The [CIELChUV color space](https://en.wikipedia.org/wiki/CIELUV#Cylindrical_representation_(CIELCh))
 * is the cylindrical representation of the CIELUV color space.
 *
 * @param l luminance, in a range of 0.0 (darkest) to 100.0 (brightest)
 * @param c chroma
 * @param h hue in degrees, where a full rotation is 360.0 degrees
 * @param alpha alpha as a percentage between 0.0 and 1.0
 * @param ref reference white against which the color values are calculated
 *
 * @see ColorLUVa
 */
@Serializable
data class ColorLCHUVa(
    val l: Double,
    val c: Double,
    val h: Double,
    override val alpha: Double = 1.0,
    override val ref: ColorXYZa = ColorXYZa.NEUTRAL
) :
    ColorModel<ColorLCHUVa>,
    ReferenceWhitePoint,
    ShadableColor<ColorLCHUVa>,
    ChromaColor<ColorLCHUVa>,
    HueShiftableColor<ColorLCHUVa>,
    LuminosityColor<ColorLCHUVa>,
    AlgebraicColor<ColorLCHUVa> {

    companion object {
        fun fromLUVa(luva: ColorLUVa): ColorLCHUVa {
            val l = luva.l
            val c = sqrt(luva.u * luva.u + luva.v * luva.v)
            var h = atan2(luva.v, luva.u)

            if (h < 0) {
                h += PI * 2
            }
            h = h.asDegrees
            return ColorLCHUVa(l, c, h, luva.alpha, luva.ref)
        }

        fun findMaxChroma(l: Double, h: Double, ref: ColorXYZa): Double {
            var left = 0.0
            var right = 2000.0
            var bestGuess = left
            while (true) {

                if (right - left < 0.0001) {
                    return bestGuess
                }

                val leftTry = ColorLCHUVa(l, left, h, 1.0, ref)
                val rightTry = ColorLCHUVa(l, right, h, 1.0, ref)
                val middle = (left + right) / 2
                val middleTry = ColorLCHUVa(l, middle, h, 1.0, ref)

                val leftValid = leftTry.toRGBa().let { it.minValue >= 0 && it.maxValue <= 1.0 }
                val rightValid = rightTry.toRGBa().let { it.minValue >= 0 && it.maxValue <= 1.0 }
                val middleValid = middleTry.toRGBa().let { it.minValue >= 0 && it.maxValue <= 1.0 }

                if (leftValid && middleValid && !rightValid) {
                    val newLeft = middle
                    val newRight = right
                    bestGuess = middle
                    left = newLeft
                    right = newRight
                }

                if (leftValid && !middleValid && !rightValid) {
                    val newLeft = left
                    val newRight = middle
                    left = newLeft
                    right = newRight
                }

                if (leftValid == middleValid && middleValid == rightValid) {
                    return bestGuess
                }
            }
        }
    }


    /**
     * Converts the current color in LCHUVa color space to its equivalent in the LUVa color space.
     *
     * The conversion involves computing the chromaticity coordinates U and V based on the
     * chroma (c) and hue (h) of the color, and using the luminance (l) and alpha (opacity) values
     * of the original color. The reference white point remains the same during the conversion.
     *
     * @return a [ColorLUVa] instance representing the color in the LUVa color space.
     */
    fun toLUVa(): ColorLUVa {
        val u = c * cos(h.asRadians)
        val v = c * sin(h.asRadians)
        return ColorLUVa(l, u, v, alpha, ref)
    }

    /**
     * Converts the current color in the LCHUVa color space to its equivalent in the LSHUVa color space.
     *
     * This transformation normalizes the chroma value relative to the maximum possible chroma at the given luminance
     * and hue in the specified reference color space. The luminance, hue, and alpha values remain consistent,
     * while the chroma value is expressed as a fraction of the maximum chroma.
     *
     * @return a [ColorLSHUVa] instance representing the color in the LSHUVa color space.
     */
    fun toLSHUVa() = ColorLSHUVa.fromLCHUVa(this)
    override fun toRGBa() = toLUVa().toRGBa()

    override fun opacify(factor: Double) = copy(alpha = alpha * factor)
    override fun shade(factor: Double) = copy(l = l * factor)

    override fun plus(right: ColorLCHUVa) =
        copy(l = l + right.l, c = c + right.c, h = h + right.h, alpha = alpha + right.alpha)

    override fun minus(right: ColorLCHUVa) =
        copy(l = l - right.l, c = c - right.c, h = h - right.h, alpha = alpha - right.alpha)

    override fun times(scale: Double) = copy(l = l * scale, c = c * scale, h = h * scale, alpha = alpha * scale)
    override fun mix(other: ColorLCHUVa, factor: Double) = mix(this, other, factor)

    override fun toVector4(): Vector4 = Vector4(l, c, h, alpha)
    override fun withChroma(chroma: Double) = copy(c = chroma)

    override val chroma: Double
        get() = c
    override fun withHue(hue: Double): ColorLCHUVa = copy(h = hue)

    override val hue: Double
        get() = h
    override fun withLuminosity(luminosity: Double): ColorLCHUVa = copy(l = luminosity)
    override val luminosity: Double
        get() = l
}

/**
 * Weighted mix between two colors in the LChUV color space.
 *
 * @param x the weighting of colors, a value 0.0 is equivalent to [left],
 * 1.0 is equivalent to [right] and at 0.5 both colors contribute to the result equally
 * @return a mix of [left] and [right] weighted by [x]
 */
fun mix(left: ColorLCHUVa, right: ColorLCHUVa, x: Double): ColorLCHUVa {
    val sx = x.coerceIn(0.0, 1.0)
    return ColorLCHUVa(
        (1.0 - sx) * left.l + sx * right.l,
        (1.0 - sx) * left.c + sx * right.c,
        mixAngle(left.h, right.h, sx),
        (1.0 - sx) * left.alpha + sx * right.alpha
    )
}
