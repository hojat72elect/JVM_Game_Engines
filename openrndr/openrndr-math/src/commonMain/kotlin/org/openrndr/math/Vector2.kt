package org.openrndr.math

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmRecord
import kotlin.math.*

/**
 * YPolarity defines the orientation of the Y-axis in a coordinate system.
 *
 * CCW_POSITIVE_Y represents a counter-clockwise rotation where the Y-axis is positive upwards.
 * CW_NEGATIVE_Y represents a clockwise rotation where the Y-axis is negative downwards.
 */
enum class YPolarity {
    CCW_POSITIVE_Y,
    CW_NEGATIVE_Y
}


/**
 * A 2D vector representation in Cartesian coordinates with methods for mathematical operations
 * and conversions. Implements linear algebra functionalities and provides utility methods
 * for creating and manipulating 2D vectors.
 *
 * @property x The x-coordinate of the vector.
 * @property y The y-coordinate of the vector.
 */
@Serializable
@JvmRecord
data class Vector2(val x: Double, val y: Double) : LinearType<Vector2>, EuclideanVector<Vector2> {

    /**
     * Represents an axis in 2D space. Each axis is associated with a unit vector
     * defining its direction.
     *
     * @property direction The unit vector representing the direction of the axis.
     */
    enum class Axis(val direction: Vector2) {
        X(UNIT_X),
        Y(UNIT_Y)
    }

    constructor(x: Double) : this(x, x)

    /** The Euclidean length of the vector. */
    override val length: Double
        get() = sqrt(x * x + y * y)

    /** The squared Euclidean length of the vector. */
    override val squaredLength: Double
        get() = x * x + y * y

    override fun map(function: (Double) -> Double): Vector2 {
        return Vector2(function(x), function(y))
    }

    /**
     * Calculates a vector perpendicular to the current one.
     *
     * @param polarity The polarity of the new vector, default is [CW_NEGATIVE_Y][YPolarity.CW_NEGATIVE_Y].
     */
    fun perpendicular(polarity: YPolarity = YPolarity.CW_NEGATIVE_Y): Vector2 = when (polarity) {
        YPolarity.CCW_POSITIVE_Y -> Vector2(-y, x)
        YPolarity.CW_NEGATIVE_Y -> Vector2(y, -x)
    }

    override val zero:Vector2 get() = ZERO

    /**
     * Calculates a cross product between this [Vector2] and [right].
     *
     * Technically you cannot find the
     * [cross product of two 2D vectors](https://stackoverflow.com/a/243984)
     * but it is still possible with clever use of mathematics.
     */
    infix fun cross(right: Vector2) = x * right.y - y * right.x

    /** Calculates a dot product between this [Vector2] and [right]. */
    override infix fun dot(right: Vector2): Double = x * right.x + y * right.y

    infix fun reflect(surfaceNormal: Vector2): Vector2 = this - surfaceNormal * (this dot surfaceNormal) * 2.0

    /**
     * Creates a new [Vector2] with the given rotation and origin.
     *
     * @param degrees The rotation in degrees.
     * @param origin The point around which the vector is rotated, default is [Vector2.ZERO].
     */
    fun rotate(degrees: Double, origin: Vector2 = ZERO): Vector2 {
        val p = this - origin
        val a = degrees.asRadians

        val w = Vector2(
                p.x * cos(a) - p.y * sin(a),
                p.y * cos(a) + p.x * sin(a)
        )

        return w + origin
    }

    val yx:Vector2 get() = Vector2(y, x)
    val xx:Vector2 get() = Vector2(x, x)
    val yy: Vector2 get() = Vector2(y, y)
    val xy0 get() = Vector3(x, y, 0.0)
    val xy1 get() = Vector3(x, y, 1.0)
    val xy00 get() = Vector4(x, y, 0.0, 0.0)
    val xy01 get() = Vector4(x, y, 0.0, 1.0)

    /**
     * Upcasts to [Vector3].
     *
     * @param x The x component value, default is [x].
     * @param y The y component value, default is [y].
     * @param z The z component value, default is `0.0`.
     */
    fun vector3(x: Double = this.x, y: Double = this.y, z: Double = 0.0): Vector3 {
        return Vector3(x, y, z)
    }

    /**
     * Upcasts to [Vector4].
     *
     * @param x The x component value, default is [x].
     * @param y The y component value, default is [y].
     * @param z The z component value, default is `0.0`.
     * @param w The w component value, default is `0.0`.
     */
    fun vector4(x: Double = this.x, y: Double = this.y, z: Double = 0.0, w: Double = 0.0): Vector4 {
        return Vector4(x, y, z, w)
    }


    operator fun get(i: Int): Double {
        return when (i) {
            0 -> x
            1 -> y
            else -> throw RuntimeException("unsupported index")
        }
    }

    operator fun unaryMinus() = Vector2(-x, -y)

    override operator fun plus(right: Vector2) = Vector2(x + right.x, y + right.y)
    operator fun plus(d: Double) = Vector2(x + d, y + d)

    override operator fun minus(right: Vector2) = Vector2(x - right.x, y - right.y)
    operator fun minus(d: Double) = Vector2(x - d, y - d)

    override operator fun times(scale: Double) = Vector2(x * scale, y * scale)
    operator fun times(v: Vector2) = Vector2(x * v.x, y * v.y)

    override operator fun div(scale: Double) = Vector2(x / scale, y / scale)
    operator fun div(d: Vector2) = Vector2(x / d.x, y / d.y)

    /** Calculates the Euclidean distance to [other]. */
    override fun distanceTo(other: Vector2): Double {
        val dx = other.x - x
        val dy = other.y - y
        return sqrt(dx * dx + dy * dy)
    }

    /** Calculates the squared Euclidean distance to [other]. */
    override fun squaredDistanceTo(other: Vector2): Double {
        val dx = other.x - x
        val dy = other.y - y
        return dx * dx + dy * dy
    }

    override fun areaBetween(other: Vector2): Double {
        // here we override the default implementation of areaBetween to make it faster without square root
        return abs(x * other.y - y * other.x)
    }

    /**
     * Interpolates between the current vector and the given vector `o` by the specified mixing factor.
     *
     * @param o The target vector to interpolate towards.
     * @param mix A mixing factor between 0 and 1 where `0` results in the current vector and `1` results in the vector `o`.
     * @return A new vector that is the result of the interpolation.
     */
    fun mix(o: Vector2, mix: Double): Vector2 = this * (1 - mix) + o * mix

    companion object {
        val ZERO = Vector2(0.0, 0.0)
        val ONE = Vector2(1.0, 1.0)
        val UNIT_X = Vector2(1.0, 0.0)
        val UNIT_Y = Vector2(0.0, 1.0)

        /** A [Vector2] representation for infinite values. */
        val INFINITY = Vector2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)

        /**
         * Converts polar coordinates to a 2D cartesian vector.
         *
         * @param polar The polar coordinates, where `theta` represents the angle in radians and `radius` represents the distance from the origin.
         * @return A [Vector2] representing the cartesian coordinates.
         */
        fun fromPolar(polar: Polar): Vector2 {
            val theta = polar.theta.asRadians
            val x = cos(theta)
            val y = sin(theta)
            return Vector2(x, y) * polar.radius
        }
    }

    /** Casts to [DoubleArray]. */
    fun toDoubleArray() = doubleArrayOf(x, y)

    /** Casts to [IntVector2]. */
    fun toInt() = IntVector2(x.toInt(), y.toInt())
}

operator fun Double.times(v: Vector2) = v * this

/**
 * Computes the component-wise minimum of two 2D vectors.
 *
 * @param a The first vector.
 * @param b The second vector.
 * @return A new vector containing the minimum x and y components from the two input vectors.
 */
fun min(a: Vector2, b: Vector2): Vector2 = Vector2(min(a.x, b.x), min(a.y, b.y))
/**
 * Determines the component-wise maximum of two 2D vectors.
 *
 * @param a The first vector.
 * @param b The second vector.
 * @return A new vector where each component is the maximum value of the corresponding components of `a` and `b`.
 */
fun max(a: Vector2, b: Vector2): Vector2 = Vector2(max(a.x, b.x), max(a.y, b.y))

fun mix(a: Vector2, b: Vector2, mix: Double): Vector2 = a * (1 - mix) + b * mix

/**
 * Computes the sum of all vectors in the iterable.
 *
 * @return A [Vector2] representing the sum of all vectors in the iterable.
 * If the iterable is empty, the returned value is a zero vector.
 */
fun Iterable<Vector2>.sum() : Vector2 {
    var x = 0.0
    var y = 0.0
    for (v in this) {
        x += v.x
        y += v.y
    }
    return Vector2(x, y)
}

/**
 * Computes the average of all [Vector2] instances in the iterable.
 *
 * The result is a new [Vector2] with its `x` and `y` components being
 * the average of the corresponding components in the iterable.
 *
 * @return A [Vector2] representing the average of all vectors in the collection.
 */
fun Iterable<Vector2>.average() : Vector2 {
    var x = 0.0
    var y = 0.0
    var count = 0
    for (v in this) {
        x += v.x
        y += v.y
        count++
    }
    return Vector2(x / count, y / count)
}