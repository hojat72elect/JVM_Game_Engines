package org.openrndr.math

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmRecord

/** Boolean 4D vector */
@Suppress("unused")
@Serializable
@JvmRecord
data class BooleanVector4(val x: Boolean, val y: Boolean, val z: Boolean, val w: Boolean) {
    companion object {
        val FALSE = BooleanVector4(x = false, y = false, z = false, w = false)
        val TRUE = BooleanVector4(x = true, y = true, z = true, w = true)
        val UNIT_X = BooleanVector4(x = true, y = false, z = false, w = false)
        val UNIT_Y = BooleanVector4(x = false, y = true, z = false, w = false)
        val UNIT_Z = BooleanVector4(x = false, y = false, z = true, w = false)
        val UNIT_W = BooleanVector4(x = false, y = false, z = false, w = true)
    }

    /** Casts to [Vector4]. */
    fun toVector4(
        x: Double = if (this.x) 1.0 else 0.0,
        y: Double = if (this.y) 1.0 else 0.0,
        z: Double = if (this.z) 1.0 else 0.0,
        w: Double = if (this.x) 1.0 else 0.0
    ) = Vector4(x, y, z, w)

    /** Casts to [IntVector4]. */
    fun toIntVector4(
        x: Int = if (this.x) 1 else 0,
        y: Int = if (this.y) 1 else 0,
        z: Int = if (this.z) 1 else 0,
        w: Int = if (this.w) 1 else 0
    ) = IntVector4(x, y, z, w)
}