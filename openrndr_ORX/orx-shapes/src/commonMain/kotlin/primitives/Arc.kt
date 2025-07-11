package org.openrndr.extra.shapes.primitives

import org.openrndr.math.LinearType
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.math.clamp
import org.openrndr.math.mix
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.contour

/**
 * Represents an arc defined by a center point, a radius, and a range of angles.
 *
 * This class provides methods to compute the position of a point along the arc
 * and to create a shape contour representation of the arc.
 *
 * @property center The center point of the arc.
 * @property radius The radius of the arc.
 * @property angle0 The starting angle of the arc, in degrees.
 * @property angle1 The ending angle of the arc, in degrees.
 */
class Arc(val center: Vector2, val radius: Double, val angle0: Double, val angle1: Double) : LinearType<Arc> {
    /**
     * Calculates the position of a point along the arc at a specified parameter `t`.
     * The parameter `t` interpolates between the starting and ending angles of the arc.
     *
     * @param t A parameter ranging from 0.0 to 1.0, where 0.0 corresponds to the starting point of the arc
     * and 1.0 corresponds to the ending point of the arc.
     * @return The position of the point on the arc as a [Vector2].
     */
    fun position(t: Double): Vector2 {
        val angle = mix(angle0, angle1, t.clamp(0.0, 1.0))
        return Polar(angle, radius).cartesian + center
    }

    /**
     * A computed property that provides a [ShapeContour] representation of the arc.
     * The contour is constructed by moving to the position at the start of the arc (t=0.0),
     * and then creating a circular arc from that point through an intermediate position
     * (t=0.5) before ending at the final position (t=1.0).
     */
    val contour: ShapeContour
        get() {
            return contour {
                moveTo(position(0.0))
                circularArcTo(position(0.5), position(1.0))
            }
        }

    override fun div(scale: Double) = Arc(center / scale, radius / scale, angle0 / scale, angle1 / scale)

    override fun times(scale: Double) = Arc(center * scale, radius * scale, angle0 * scale, angle1 * scale)

    override fun plus(right: Arc) =
        Arc(center + right.center, radius + right.radius, angle0 + right.angle0, angle1 + right.angle1)

    override fun minus(right: Arc) =
        Arc(center - right.center, radius - right.radius, angle0 - right.angle0, angle1 - right.angle1)
}
