package org.openrndr.extra.color.spaces

import kotlinx.serialization.Serializable
import org.openrndr.color.AlgebraicColor
import org.openrndr.color.ColorModel
import org.openrndr.color.ColorRGBa
import org.openrndr.color.HueShiftableColor
import org.openrndr.color.SaturatableColor
import org.openrndr.color.ShadableColor
import org.openrndr.math.Vector4
import org.openrndr.math.mixAngle
import kotlin.jvm.JvmRecord
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a color in the OKHSVa color model.
 *
 * The OKHSVa color model is derived from OKLABa and provides a perceptually uniform representation
 * of colors using hue (h), saturation (s), value (v), and alpha (opacity).
 *
 * This class supports operations and transformations such as conversion to and from RGBa,
 * hue shifting, saturation adjustment, shading, and algebraic operations like addition, subtraction,
 * and scaling. It is ideal for working with colors in contexts requiring accurate color mixing
 * and perceptual results.
 *
 * @property h Hue value in degrees (0.0 - 360.0), representing the color's angle on the color wheel.
 * @property s Saturation value (0.0 - 1.0), representing the intensity or purity of the color.
 * @property v Value (0.0 - 1.0), representing the color's brightness.
 * @property alpha Opacity value (0.0 - 1.0), with 1.0 being fully opaque.
 */
@Suppress("LocalVariableName")
@Serializable
@JvmRecord
data class ColorOKHSVa(val h: Double, val s: Double, val v: Double, override val alpha: Double = 1.0) :
    ColorModel<ColorOKHSVa>,
    HueShiftableColor<ColorOKHSVa>,
    SaturatableColor<ColorOKHSVa>,
    ShadableColor<ColorOKHSVa>,
    AlgebraicColor<ColorOKHSVa> {

    companion object {
        fun fromColorRGBa(c: ColorRGBa): ColorOKHSVa {
            val lab = c.toOKLABa()
            var C = sqrt(lab.a * lab.a + lab.b * lab.b)
            val a_ = if (C != 0.0) lab.a / C else 0.0
            val b_ = if (C != 0.0) lab.b / C else 0.0

            var L = lab.l
            val h = 0.5 + 0.5 * atan2(-lab.b, -lab.a) / PI

            val ST_max = get_ST_max(a_, b_)
            val S_max = ST_max[0]
            val S_0 = 0.5
            val T = ST_max[1]
            val k = if (S_max != 0.0) (1 - S_0 / S_max) else 0.0

            val t = T / (C + L * T)
            val L_v = t * L
            val C_v = t * C

            val L_vt = toeInv(L_v)
            val C_vt = C_v * L_vt / L_v

            val rgb_scale = ColorOKLABa(L_vt, a_ * C_vt, b_ * C_vt, c.alpha).toRGBa().toLinear()
            val scale_L = (1.0 / (max(rgb_scale.r, rgb_scale.g, rgb_scale.b, 0.0))).pow(1.0 / 3.0)

            L /= scale_L
            C /= scale_L

            C = C * toe(L) / L
            L = toe(L)

            val v = L / L_v
            val s = (S_0 + T) * C_v / ((T * S_0) + T * k * C_v)

            return ColorOKHSVa(h * 360.0, if (s == s) s else 0.0, if (v == v) v else 0.0, c.alpha)
        }
    }

    override fun toRGBa(): ColorRGBa {
        val a_ = cos(2 * PI * h / 360.0)
        val b_ = sin(2 * PI * h / 360.0)

        val ST_max = get_ST_max(a_, b_)
        val S_max = ST_max[0]
        val S_0 = 0.5
        val T = ST_max[1]
        val k = 1 - S_0 / S_max

        val L_v = 1 - s * S_0 / (S_0 + T - T * k * s)
        val C_v = s * T * S_0 / (S_0 + T - T * k * s)

        var L = v * L_v
        var C = v * C_v

        // to present steps along the way
        //L = v;
        //C = v*s*S_max;
        //L = v*(1 - s*S_max/(S_max+T));
        //C = v*s*S_max*T/(S_max+T);

        val L_vt = toeInv(L_v)
        val C_vt = C_v * L_vt / L_v

        val L_new = toeInv(L) // * L_v/L_vt;
        C = C * L_new / L
        L = L_new

        val rgb_scale =
            ColorOKLABa(L_vt, a_ * C_vt, b_ * C_vt, alpha).toRGBa().toLinear()// oklab_to_linear_srgb(L_vt,a_*C_vt,b_*C_vt);
        val scale_L = (1.0 / (max(rgb_scale.r, rgb_scale.g, rgb_scale.b, 0.0))).pow(1.0 / 3.0)

        // remove to see effect without rescaling
        L *= scale_L
        C *= scale_L

        return ColorOKLABa(
            if (L == L) L else 0.0,
            if (C == C) C * a_ else 0.0,
            if (C == C) C * b_ else 0.0,
            alpha
        ).toRGBa().toSRGB()
    }

    override val hue: Double
        get() = h

    override fun withHue(hue: Double): ColorOKHSVa = copy(h = hue)

    override fun opacify(factor: Double): ColorOKHSVa = copy(alpha = alpha * factor)
    override val saturation: Double
        get() = s

    override fun withSaturation(saturation: Double): ColorOKHSVa = copy(s = saturation)

    override fun shade(factor: Double): ColorOKHSVa = copy(v = v * factor)
    override fun minus(right: ColorOKHSVa) =
        copy(h = h - right.h, s = s - right.s, v = v - right.v, alpha = alpha - right.alpha)

    override fun plus(right: ColorOKHSVa) =
        copy(h = h + right.h, s = s + right.s, v = v + right.v, alpha = alpha + right.alpha)

    override fun times(scale: Double): ColorOKHSVa = copy(h = h * scale, s = s * scale, v = v * scale, alpha = alpha * scale)

    override fun mix(other: ColorOKHSVa, factor: Double): ColorOKHSVa {
        val sx = factor.coerceIn(0.0, 1.0)
        return ColorOKHSVa(
            mixAngle(h, other.h, sx),
            (1.0 - sx) * s + sx * other.s,
            (1.0 - sx) * v + sx * other.v,
            (1.0 - sx) * alpha + sx * other.alpha
        )
    }

    override fun toVector4(): Vector4 = Vector4(h, s, v, alpha)
}

fun ColorRGBa.toOKHSVa(): ColorOKHSVa = ColorOKHSVa.fromColorRGBa(this)