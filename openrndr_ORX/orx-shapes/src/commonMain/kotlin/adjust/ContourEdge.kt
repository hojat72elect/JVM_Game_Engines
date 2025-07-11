package org.openrndr.extra.shapes.adjust

import org.openrndr.extra.shapes.rectify.rectified
import org.openrndr.extra.shapes.utilities.fromContours
import org.openrndr.extra.shapes.utilities.insertPointAt
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.transforms.buildTransform
import org.openrndr.shape.Segment2D
import org.openrndr.shape.SegmentType
import org.openrndr.shape.ShapeContour
import kotlin.jvm.JvmRecord
import kotlin.math.abs

internal fun Vector2.transformedBy(t: Matrix44, mask: Int = 0x0f, maskRef: Int = 0x0f) =
    if ((mask and maskRef) != 0)
        (t * (this.xy01)).xy else {
        this
    }

fun <E> List<E>.update(vararg updates: Pair<Int, E>): List<E> {
    if (updates.isEmpty()) {
        return this
    }
    val result = this.toMutableList()
    for ((index, value) in updates) {
        result[index] = value
    }
    return result
}

/**
 * Helper for querying and adjusting [ShapeContour].
 * * An edge embodies exactly the same thing as a [Segment][org.openrndr.shape.Segment]
 * * All edge operations are immutable and will create a new [ContourEdge] pointing to a copied and updated [ShapeContour]
 * @param contour the contour to be adjusted
 * @param segmentIndex the index of the segment of the contour to be adjusted
 * @param adjustments a list of [SegmentOperation] that have been applied to reach to [contour], this is used to inform [ShapeContour]
 * of changes in the contour topology.
 * @since 0.4.4
 */
@JvmRecord
data class ContourEdge(
    val contour: ShapeContour,
    val segmentIndex: Int,
    val adjustments: List<SegmentOperation> = emptyList()
) {
    /**
     * provide a copy without the list of adjustments
     */
    fun withoutAdjustments(): ContourEdge {
        return if (adjustments.isEmpty()) {
            this
        } else {
            copy(adjustments = emptyList())
        }
    }

    /**
     * convert the edge to a linear edge, truncating control points if those exist
     */
    fun toLinear(): ContourEdge {
        return if (contour.segments[segmentIndex].type != SegmentType.LINEAR) {
            val newSegment = contour.segments[segmentIndex].copy(control = emptyList())
            val newSegments = contour.segments
                .update(segmentIndex to newSegment)

            ContourEdge(
                ShapeContour.fromSegments(newSegments, contour.closed),
                segmentIndex
            )
        } else {
            this
        }
    }

    /**
     * convert the edge to a cubic edge
     */
    fun toCubic(): ContourEdge {
        return if (contour.segments[segmentIndex].type != SegmentType.CUBIC) {
            val newSegment = contour.segments[segmentIndex].cubic
            val newSegments = contour.segments
                .update(segmentIndex to newSegment)

            ContourEdge(
                ShapeContour.fromSegments(newSegments, contour.closed),
                segmentIndex
            )
        } else {
            this
        }
    }

    val length: Double
        get() {
            return contour.segments[segmentIndex].length
        }


    /**
     * replace this edge with a point at [t]
     * @param t an edge t value between 0 and 1
     */
    fun replacedWith(t: Double, updateTangents: Boolean): ContourEdge {
        if (contour.empty) {
            return withoutAdjustments()
        }
        val point = contour.segments[segmentIndex].position(t)
        val segmentInIndex = if (contour.closed) (segmentIndex - 1).mod(contour.segments.size) else segmentIndex - 1
        val segmentOutIndex = if (contour.closed) (segmentIndex + 1).mod(contour.segments.size) else segmentIndex + 1
        val refIn = contour.segments.getOrNull(segmentInIndex)
        val refOut = contour.segments.getOrNull(segmentOutIndex)

        val newSegments = contour.segments.toMutableList()
        if (refIn != null) {
            newSegments[segmentInIndex] = newSegments[segmentInIndex].copy(end = point)
        }
        if (refOut != null) {
            newSegments[segmentOutIndex] = newSegments[segmentOutIndex].copy(start = point)
        }
        val adjustments = newSegments.adjust {
            removeAt(segmentIndex)
        }
        return ContourEdge(ShapeContour.fromSegments(newSegments, contour.closed), segmentIndex, adjustments)
    }

    fun splitIn(parts: Int): ContourEdge {
        if (contour.empty || parts < 2) {
            return withoutAdjustments()
        }
        val segment = contour.segments[segmentIndex]
        val r = segment.contour.rectified()
        val newSegments = (0..parts).map {
            it.toDouble() / parts
        }.windowed(2, 1).map {
            r.sub(it[0], it[1])
        }
        require(newSegments.size == parts)
        return replacedWith(ShapeContour.fromContours(newSegments, false, 1.0))
    }

    fun replacedWith(openContour: ShapeContour): ContourEdge {
        if (contour.empty) {
            return withoutAdjustments()
        }
        require(!openContour.closed) { "openContour should be open" }
        val segment = contour.segments[segmentIndex]
        var newSegments = contour.segments.toMutableList()

        var insertIndex = segmentIndex
        val adjustments = newSegments.adjust {
            removeAt(segmentIndex)

            if (segment.start.distanceTo(openContour.position(0.0)) > 1E-3) {
                add(insertIndex, Segment2D(segment.start, openContour.position(0.0)))
                insertIndex++
            }
            for (s in openContour.segments) {
                add(insertIndex, s)
                insertIndex++
            }
            if (segment.end.distanceTo(openContour.position(1.0)) > 1E-3) {
                add(insertIndex, Segment2D(segment.end, openContour.position(1.0)))
            }
        }
        return ContourEdge(ShapeContour.fromSegments(newSegments, contour.closed), segmentIndex, adjustments)
    }


    /**
     * subs the edge from [t0] to [t1], preserves topology unless t0 = t1
     * @param t0 the start edge t-value, between 0 and 1
     * @param t1 the end edge t-value, between 0 and 1
     */
    fun subbed(t0: Double, t1: Double, updateTangents: Boolean = true): ContourEdge {
        if (contour.empty) {
            return withoutAdjustments()
        }
        if (abs(t0 - t1) > 1E-6) {
            val sub = contour.segments[segmentIndex].sub(t0, t1)
            val segmentInIndex = if (contour.closed) (segmentIndex - 1).mod(contour.segments.size) else segmentIndex - 1
            val segmentOutIndex =
                if (contour.closed) (segmentIndex + 1).mod(contour.segments.size) else segmentIndex + 1
            val refIn = contour.segments.getOrNull(segmentInIndex)
            val refOut = contour.segments.getOrNull(segmentOutIndex)

            val newSegments = contour.segments.toMutableList()
            if (refIn != null) {
                newSegments[segmentInIndex] = newSegments[segmentInIndex].copy(end = sub.start)
            }
            if (refOut != null) {
                newSegments[segmentOutIndex] = newSegments[segmentOutIndex].copy(start = sub.end)
            }
            newSegments[segmentIndex] = sub
            return ContourEdge(ShapeContour.fromSegments(newSegments, contour.closed), segmentIndex)
        } else {
            return replacedWith(t0, updateTangents)
        }
    }

    /**
     * split the edge at [t]
     * @param t an edge t value between 0 and 1, will not split when t == 0 or t == 1
     */
    fun splitAt(t: Double): ContourEdge {
        if (contour.empty) {
            return withoutAdjustments()
        }
        val newContour = contour.insertPointAt(segmentIndex, t)
        if (newContour.segments.size == contour.segments.size + 1) {
            return ContourEdge(newContour, segmentIndex, listOf(SegmentOperation.Insert(segmentIndex + 1, 1)))
        } else {
            return this.copy(adjustments = emptyList())
        }
    }


    enum class ControlMask(val mask: Int) {
        START(1),
        CONTROL0(2),
        CONTROL1(4),
        END(8)
    }

    fun maskOf(vararg control: ControlMask): Int {
        var mask = 0
        for (c in control) {
            mask = mask or c.mask
        }
        return mask
    }

    /**
     * apply [transform] to the edge
     * @param transform a [Matrix44]
     */
    fun transformedBy(
        transform: Matrix44,
        updateTangents: Boolean = true,
        mask: Int = 0xf,
        promoteToCubic: Boolean = false
    ): ContourEdge {
        val segment = contour.segments[segmentIndex].let { if (promoteToCubic) it.cubic else it }
        val newSegment = segment.copy(
            start = segment.start.transformedBy(transform, mask, ControlMask.START.mask),
            control = segment.control.mapIndexed { index, it -> it.transformedBy(transform, mask, 1 shl (index + 1)) },
            end = segment.end.transformedBy(transform, mask, ControlMask.END.mask)
        )
        val segmentInIndex = if (contour.closed) (segmentIndex - 1).mod(contour.segments.size) else segmentIndex - 1
        val segmentOutIndex = if (contour.closed) (segmentIndex + 1).mod(contour.segments.size) else segmentIndex + 1
        val refIn = contour.segments.getOrNull(segmentInIndex)
        val refOut = contour.segments.getOrNull(segmentOutIndex)

        val newSegments = contour.segments.map { it }.toMutableList()

        if (refIn != null) {
            var control = if (refIn.linear || !updateTangents) {
                refIn.control
            } else {
                refIn.cubic.control
            }
            if (control.isNotEmpty()) {
                control = listOf(control[0], control[1].transformedBy(transform))
            }
            newSegments[segmentInIndex] = refIn.copy(control = control, end = segment.start.transformedBy(transform))
        }
        if (refOut != null) {
            var control = if (refOut.linear || !updateTangents) {
                refOut.control
            } else {
                refOut.cubic.control
            }
            if (control.isNotEmpty()) {
                control = listOf(control[0].transformedBy(transform), control[1])

            }
            newSegments[segmentOutIndex] = refOut.copy(start = segment.end.transformedBy(transform), control = control)
        }

        newSegments[segmentIndex] = newSegment
        return ContourEdge(ShapeContour.fromSegments(newSegments, contour.closed), segmentIndex)
    }

    fun startMovedBy(translation: Vector2, updateTangents: Boolean = true): ContourEdge =
        transformedBy(buildTransform {
            translate(translation)
        }, updateTangents = updateTangents, mask = maskOf(ControlMask.START))

    fun control0MovedBy(translation: Vector2): ContourEdge = transformedBy(buildTransform {
        translate(translation)
    }, updateTangents = false, mask = maskOf(ControlMask.CONTROL0), promoteToCubic = true)

    fun control1MovedBy(translation: Vector2): ContourEdge = transformedBy(buildTransform {
        translate(translation)
    }, updateTangents = false, mask = maskOf(ControlMask.CONTROL1), promoteToCubic = true)

    fun endMovedBy(translation: Vector2, updateTangents: Boolean = true): ContourEdge {
        return transformedBy(buildTransform {
            translate(translation)
        }, updateTangents = updateTangents, mask = maskOf(ControlMask.END))
    }

    fun movedBy(translation: Vector2, updateTangents: Boolean = true): ContourEdge {
        return transformedBy(buildTransform {
            translate(translation)
        }, updateTangents)
    }

    fun rotatedBy(rotationInDegrees: Double, anchorT: Double, updateTangents: Boolean = true): ContourEdge {
        val anchor = contour.segments[segmentIndex].position(anchorT)
        return transformedBy(buildTransform {
            translate(anchor)
            rotate(rotationInDegrees)
            translate(-anchor)
        }, updateTangents)
    }

    fun scaledBy(scaleFactor: Double, anchorT: Double, updateTangents: Boolean = true): ContourEdge {
        val anchor = contour.segments[segmentIndex].position(anchorT)
        return scaledBy(scaleFactor, anchor, updateTangents)
    }

    fun scaledBy(scaleFactor: Double, anchor: Vector2, updateTangents: Boolean = true): ContourEdge {
        return transformedBy(buildTransform {
            translate(anchor)
            scale(scaleFactor)
            translate(-anchor)
        }, updateTangents)
    }
}

