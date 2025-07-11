package org.openrndr.math

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmRecord
import kotlin.math.*

/** Double-precision 3D vector. */
@Serializable
@JvmRecord
data class Vector3(val x: Double, val y: Double, val z: Double) : LinearType<Vector3>, EuclideanVector<Vector3> {

    enum class Axis(val direction: Vector3) {
        X(UNIT_X),
        Y(UNIT_Y),
        Z(UNIT_Y)
    }

    constructor(x: Double) : this(x, x, x)

    override val zero: Vector3 get() = ZERO

    companion object {
        val ZERO = Vector3(0.0, 0.0, 0.0)
        val ONE = Vector3(1.0, 1.0, 1.0)
        val UNIT_XYZ = ONE.normalized
        val UNIT_X = Vector3(1.0, 0.0, 0.0)
        val UNIT_Y = Vector3(0.0, 1.0, 0.0)
        val UNIT_Z = Vector3(0.0, 0.0, 1.0)
        val INFINITY = Vector3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)

        fun fromSpherical(s: Spherical): Vector3 {

            val phi = s.phi.asRadians
            val theta = s.theta.asRadians

            val sinPhiRadius = sin(phi) * s.radius
            return Vector3(
                sinPhiRadius * sin(theta),
                cos(phi) * s.radius,
                sinPhiRadius * cos(theta)
            )
        }
    }

    val xyz0 get() = Vector4(x, y, z, 0.0)
    val xyz1 get() = Vector4(x, y, z, 1.0)

    val xy: Vector2 get() = Vector2(x, y)
    val yx: Vector2 get() = Vector2(y, x)
    val zx: Vector2 get() = Vector2(z, x)

    val xz: Vector2 get() = Vector2(x, z)

    operator fun get(i: Int): Double {
        return when (i) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw RuntimeException("unsupported index")
        }
    }

    operator fun unaryMinus() = Vector3(-x, -y, -z)
    override operator fun plus(right: Vector3) = Vector3(x + right.x, y + right.y, z + right.z)
    operator fun plus(d: Double) = Vector3(x + d, y + d, z + d)
    override operator fun minus(right: Vector3) = Vector3(x - right.x, y - right.y, z - right.z)
    operator fun minus(d: Double) = Vector3(x - d, y - d, z - d)
    operator fun times(v: Vector3) = Vector3(x * v.x, y * v.y, z * v.z)
    override operator fun times(scale: Double) = Vector3(x * scale, y * scale, z * scale)
    override operator fun div(scale: Double) = Vector3(x / scale, y / scale, z / scale)
    operator fun div(v: Vector3) = Vector3(x / v.x, y / v.y, z / v.z)

    /** Calculates a dot product between this [Vector2] and [right]. */
    override infix fun dot(right: Vector3): Double = x * right.x + y * right.y + z * right.z

    /** Calculates a cross product between this [Vector2] and [v]. */
    infix fun cross(v: Vector3) = Vector3(
        y * v.z - z * v.y,
        -(x * v.z - z * v.x),
        x * v.y - y * v.x
    )


    /** The Euclidean length of the vector. */
    override val length: Double get() = sqrt(x * x + y * y + z * z)

    /** The squared Euclidean length of the vector. */
    override val squaredLength get() = x * x + y * y + z * z

    override fun map(function: (Double) -> Double): Vector3 {
        return Vector3(function(x), function(y), function(z))
    }

    /** Casts to [DoubleArray]. */
    fun toDoubleArray() = doubleArrayOf(x, y, z)

    override fun distanceTo(other: Vector3): Double {
        val dx = other.x - x
        val dy = other.y - y
        val dz = other.z - z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    override fun squaredDistanceTo(other: Vector3): Double {
        val dx = other.x - x
        val dy = other.y - y
        val dz = other.z - z
        return dx * dx + dy * dy + dz * dz
    }

    fun mix(o: Vector3, mix: Double): Vector3 = this * (1 - mix) + o * mix

    val spherical: Spherical
        get() {
            return Spherical.fromVector(this)
        }

    /** Casts to [IntVector3]. */
    fun toInt() = IntVector3(x.toInt(), y.toInt(), z.toInt())
}

operator fun Double.times(v: Vector3) = v * this

fun min(a: Vector3, b: Vector3) = Vector3(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))
fun max(a: Vector3, b: Vector3) = Vector3(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))

fun mix(a: Vector3, b: Vector3, mix: Double): Vector3 = a * (1 - mix) + b * mix

fun Iterable<Vector3>.sum(): Vector3 {
    var x = 0.0
    var y = 0.0
    var z = 0.0
    for (v in this) {
        x += v.x
        y += v.y
        z += v.z
    }
    return Vector3(x, y, z)
}

fun Iterable<Vector3>.average(): Vector3 {
    var x = 0.0
    var y = 0.0
    var z = 0.0
    var count = 0
    for (v in this) {
        x += v.x
        y += v.y
        z += v.z
        count++
    }
    return Vector3(x / count, y / count, z / count)
}