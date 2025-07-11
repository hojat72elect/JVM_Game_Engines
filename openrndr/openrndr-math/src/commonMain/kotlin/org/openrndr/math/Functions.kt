@file:Suppress("unused")

package org.openrndr.math

import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min

fun Vector2.mod(b: Vector2) =
        Vector2(x.mod(b.x),
                y.mod(b.y))

fun Vector3.mod(b: Vector3) =
        Vector3(x.mod(b.x),
                y.mod(b.y),
                z.mod(b.z))

fun Vector4.mod(b: Vector4) =
        Vector4(x.mod(b.x),
                y.mod(b.y),
                z.mod(b.z),
                w.mod(b.w))

fun IntVector2.mod(b: IntVector2) =
        IntVector2(x.mod(b.x),
                y.mod(b.y))

fun IntVector3.mod(b: IntVector3) =
        IntVector3(x.mod(b.x),
                y.mod(b.y),
                z.mod(b.z))

fun IntVector4.mod(b: IntVector4) =
        IntVector4(x.mod(b.x),
                y.mod(b.y),
                z.mod(b.z),
                w.mod(b.w))

/** Returns number whose value is limited between [min] and [max]. */
fun clamp(value: Double, min: Double, max: Double) = max(min, min(max, value))
/** Returns number whose value is limited between [min] and [max]. */
fun clamp(value: Int, min: Int, max: Int) = max(min, min(max, value))

@JvmName("doubleClamp")
fun Double.clamp(min: Double, max: Double) = clamp(this, min, max)

@JvmName("intClamp")
fun Int.clamp(min: Int, max: Int) = clamp(this, min, max)

/** Returns [Vector2] whose value is limited between [min] and [max] per vector component. */
fun Vector2.clamp(min : Vector2, max : Vector2) =
        Vector2(x.clamp(min.x, max.x),
                y.clamp(min.y, max.y))

/** Returns [Vector3] whose value is limited between [min] and [max] per vector component. */
fun Vector3.clamp(min : Vector3, max : Vector3) =
        Vector3(x.clamp(min.x, max.x),
                y.clamp(min.y, max.y),
                z.clamp(min.y, max.z))

/** Returns [Vector4] whose value is limited between [min] and [max] per vector component. */
fun Vector4.clamp(min : Vector4, max : Vector4) =
        Vector4(x.clamp(min.x, max.x),
                y.clamp(min.y, max.y),
                z.clamp(min.z, max.z),
                w.clamp(min.w, max.w))

/** Returns [IntVector2] whose value is limited between [min] and [max] per vector component. */
fun IntVector2.clamp(min : IntVector2, max : IntVector2) =
        IntVector2(x.clamp(min.x, max.x),
                y.clamp(min.y, max.y))

/** Returns [IntVector3] whose value is limited between [min] and [max] per vector component. */
fun IntVector3.clamp(min : IntVector3, max : IntVector3) =
        IntVector3(x.clamp(min.x, max.x),
                y.clamp(min.y, max.y),
                z.clamp(min.y, max.z))

/** Returns [IntVector4] whose value is limited between [min] and [max] per vector component. */
fun IntVector4.clamp(min : IntVector4, max : IntVector4) =
        IntVector4(x.clamp(min.x, max.x),
                y.clamp(min.y, max.y),
                z.clamp(min.z, max.z),
                w.clamp(min.w, max.w))

inline val Double.asRadians: Double get() = this * 0.017453292519943295
inline val Double.asDegrees: Double get() = this * 57.29577951308232

val Double.asExponent: Int get() = (
        (this.toRawBits() and EXP_BIT_MASK shr SIGNIFICAND_WIDTH - 1) - EXP_BIAS
        ).toInt()

private const val EXP_BIT_MASK = 0x7FF0000000000000L
private const val SIGNIFICAND_WIDTH = 53
private const val EXP_BIAS = 1023
