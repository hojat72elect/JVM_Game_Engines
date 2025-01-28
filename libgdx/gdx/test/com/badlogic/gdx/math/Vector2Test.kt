package com.badlogic.gdx.math

import org.junit.Assert.assertEquals
import org.junit.Test

class Vector2Test {

    @Test
    fun testToString() {
        assertEquals("(-5.0,42.00055)", Vector2(-5f, 42.00055f).toString())
    }

    @Test
    fun testFromString() {
        assertEquals(Vector2(-5f, 42.00055f), Vector2().fromString("(-5,42.00055)"))
    }

    @Test
    fun testAngle() {
        assertEquals(270f, Vector2(0f, -1f).angleDeg(), MathUtils.FLOAT_ROUNDING_ERROR)
    }

    @Test
    fun testAngleRelative() {
        assertEquals(270f, Vector2(0f, -1f).angleDeg(Vector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
    }

    @Test
    fun testAngleStatic() {
        assertEquals(270f, Vector2.angleDeg(0f, -1f), MathUtils.FLOAT_ROUNDING_ERROR)
    }

    @Test
    fun testAngleRad() {
        assertEquals(-MathUtils.HALF_PI, Vector2(0f, -1f).angleRad(), MathUtils.FLOAT_ROUNDING_ERROR)
    }

    @Test
    fun testAngleRadRelative() {
        assertEquals(-MathUtils.HALF_PI, Vector2(0f, -1f).angleRad(Vector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
    }

    @Test
    fun testAngleRadStatic() {
        assertEquals(-MathUtils.HALF_PI, Vector2.angleRad(0f, -1f), MathUtils.FLOAT_ROUNDING_ERROR)
    }

}