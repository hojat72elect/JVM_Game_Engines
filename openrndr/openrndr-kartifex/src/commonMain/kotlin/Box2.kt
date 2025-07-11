package org.openrndr.kartifex


/**
 * Represents a two-dimensional rectangular box defined by two points: a lower-left point and an upper-right point.
 * This class extends the generic `Box` class specialized for two-dimensional vectors (`Vec2`).
 *
 * @constructor Creates a `Box2` instance from two specified corners of the box.
 * @property lx The x-coordinate of the lower-left corner.
 * @property ly The y-coordinate of the lower-left corner.
 * @property ux The x-coordinate of the upper-right corner.
 * @property uy The y-coordinate of the upper-right corner.
 */
class Box2 internal constructor(ax: Double, ay: Double, bx: Double, by: Double) :
    Box<Vec2, Box2>() {
    val lx: Double
    val ly: Double
    val ux: Double
    val uy: Double

    fun width(): Double {
        return ux - lx
    }

    fun height(): Double {
        return uy - ly
    }

    constructor(a: Vec2, b: Vec2) : this(a.x, a.y, b.x, b.y)

    fun scale(k: Double): Box2 {
        return scale(Vec2(k, k))
    }

    fun scale(x: Double, y: Double): Box2 {
        return scale(Vec2(x, y))
    }

    fun translate(x: Double, y: Double): Box2 {
        return translate(Vec2(x, y))
    }

    fun vertices(): Array<Vec2> {
        return arrayOf(
            Vec2(lx, ly),
            Vec2(ux, ly),
            Vec2(ux, uy),
            Vec2(lx, uy)
        )
    }

    fun outline(): Ring2 {
        val cs: MutableList<Curve2> = mutableListOf()
        val vs: Array<Vec2> = vertices()
        for (i in vs.indices) {
            cs.add(Line2.line(vs[i], vs[(i + 1) % 4]))
        }
        return Ring2(cs)
    }

    override fun intersects(b: Box2): Boolean {
        return if (isEmpty || b.isEmpty) {
            false
        } else (b.ux >= lx
                ) and (ux >= b.lx
                ) and (b.uy >= ly
                ) and (uy >= b.ly)
    }

    override fun lower(): Vec2 {
        return Vec2(lx, ly)
    }

    override fun upper(): Vec2 {
        return Vec2(ux, uy)
    }

    override val isEmpty: Boolean
        get() = this === EMPTY

    override fun construct(a: Vec2, b: Vec2): Box2 {
        return Box2(a.x, a.y, b.x, b.y)
    }

    override fun empty(): Box2 {
        return EMPTY
    }

    companion object {
        val EMPTY =
            Box2(Vec2(Double.NaN, Double.NaN), Vec2(Double.NaN, Double.NaN))
    }

    init {
        if (ax < bx) {
            lx = ax
            ux = bx
        } else {
            ux = ax
            lx = bx
        }
        if (ay < by) {
            ly = ay
            uy = by
        } else {
            uy = ay
            ly = by
        }
    }
}