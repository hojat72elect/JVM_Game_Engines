package com.badlogic.gdx.math.collision

import com.badlogic.gdx.math.MathUtils.FLOAT_ROUNDING_ERROR
import com.badlogic.gdx.math.Vector3
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CollisionTest {

    @Test
    fun testBoundingBox() {
        val b1 = BoundingBox(Vector3.Zero, Vector3(1f, 1f, 1f))
        val b2 = BoundingBox(Vector3(1f, 1f, 1f), Vector3(2f, 2f, 2f))

        assertTrue(b1.contains(Vector3.Zero))
        assertTrue(b1.contains(b1))
        assertFalse(b1.contains(b2))
        // Note, in stage the bottom and left sides are inclusive while the right and top sides are exclusive.
    }

    @Test
    fun testOrientedBoundingBox() {
        val b1 = OrientedBoundingBox(BoundingBox(Vector3.Zero, Vector3(1f, 1f, 1f)))
        val b2 = OrientedBoundingBox(BoundingBox(Vector3(1f, 1f, 1f), Vector3(2f, 2f, 2f)))

        assertTrue(b1.contains(Vector3.Zero))
        assertTrue(b1.contains(b1))
        assertFalse(b1.contains(b2))
    }

    @Test
    fun testOrientedBoundingBoxCollision() {
        var b1 = OrientedBoundingBox(BoundingBox(Vector3.Zero, Vector3(1f, 1f, 1f)))
        var b2 = OrientedBoundingBox(BoundingBox(Vector3(1 + FLOAT_ROUNDING_ERROR, 1f, 1f), Vector3(2f, 2f, 2f)))

        assertFalse(b1.intersects(b2))

        b1 = OrientedBoundingBox(BoundingBox(Vector3.Zero, Vector3(1f, 1f, 1f)))
        b2 = OrientedBoundingBox(BoundingBox(Vector3(0.5f, 0.5f, 0.5f), Vector3(2f, 2f, 2f)))

        assertTrue(b1.intersects(b2))
    }
}