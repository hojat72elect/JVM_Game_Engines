package com.badlogic.gdx.math

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Shape2DTest {
    @Test
    fun testCircle() {
        val c1 = Circle(0f, 0f, 1f)
        val c2 = Circle(0f, 0f, 1f)
        val c3 = Circle(2f, 0f, 1f)
        val c4 = Circle(0f, 0f, 2f)

        assertTrue(c1.overlaps(c1))
        assertTrue(c1.overlaps(c2))
        assertFalse(c1.overlaps(c3))
        assertTrue(c1.overlaps(c4))
        assertTrue(c4.overlaps(c1))
        assertTrue(c1.contains(0f, 1f))
        assertFalse(c1.contains(0f, 2f))
        assertTrue(c1.contains(c1))
        assertFalse(c1.contains(c4))
        assertTrue(c4.contains(c1))
    }

    @Test
    fun testRectangle() {
        val r1 = Rectangle(0f, 0f, 1f, 1f)
        val r2 = Rectangle(1f, 0f, 2f, 1f)
        assertTrue(r1.overlaps(r1))
        assertFalse(r1.overlaps(r2))
        assertTrue(r1.contains(0f, 0f))
    }
}
