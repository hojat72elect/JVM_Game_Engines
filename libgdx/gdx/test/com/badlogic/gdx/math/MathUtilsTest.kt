package com.badlogic.gdx.math

import com.badlogic.gdx.math.MathUtils.FLOAT_ROUNDING_ERROR
import com.badlogic.gdx.math.MathUtils.HALF_PI
import com.badlogic.gdx.math.MathUtils.PI
import com.badlogic.gdx.math.MathUtils.PI2
import com.badlogic.gdx.math.MathUtils.atan2Deg360
import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.lerpAngle
import com.badlogic.gdx.math.MathUtils.lerpAngleDeg
import com.badlogic.gdx.math.MathUtils.map
import com.badlogic.gdx.math.MathUtils.norm
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.MathUtils.sinDeg
import com.badlogic.gdx.math.MathUtils.tanDeg
import kotlin.math.tan
import kotlin.math.withSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MathUtilsTest {

    @Test
    fun lerpAngle() {
        assertEquals(PI / 18f, lerpAngle(PI / 18f, PI / 6f, 0.0f), 0.01f)
        assertEquals(PI / 9f, lerpAngle(PI / 18f, PI / 6f, 0.5f), 0.01f)
        assertEquals(PI / 6f, lerpAngle(PI / 18f, PI / 6f, 1.0f), 0.01f)

        // checks both negative c, which should produce a result close to HALF_PI, and
        // positive c, which should be close to PI + HALF_PI.
        // intentionally skips where c == 0, because there are two equally-valid results for that case.
        // checks both negative c, which should produce a result close to HALF_PI, and
        // positive c, which should be close to PI + HALF_PI.
        // intentionally skips where c == 0, because there are two equally-valid results for that case.

        var c = -1f
        while (c <= 1f) {
            assertEquals(PI + HALF_PI.withSign(c) + c, lerpAngle(0f, PI2 + PI + c + c, 0.5f), 0.01f)
            assertEquals(PI + HALF_PI.withSign(c) + c, lerpAngle(PI2 + PI + c + c, 0f, 0.5f), 0.01f)
            c += 0.003f
        }

    }

    @Test
    fun lerpAngleDeg() {
        assertEquals(10f, lerpAngleDeg(10f, 30f, 0.0f), 0.01f)
        assertEquals(20f, lerpAngleDeg(10f, 30f, 0.5f), 0.01f)
        assertEquals(30f, lerpAngleDeg(10f, 30f, 1.0f), 0.01f)


        // checks both negative c, which should produce a result close to 90, and
        // positive c, which should be close to 270.
        // intentionally skips where c == 0, because there are two equally-valid results for that case.
        var c = -80f
        while (c <= 80f) {
            assertEquals(180f + 90F.withSign(c) + c, lerpAngleDeg(0f, 540 + c + c, 0.5f), 0.01f)
            assertEquals(180f + 90F.withSign(c) + c, lerpAngleDeg(540 + c + c, 0f, 0.5f), 0.01f)
            c += 0.3f
        }

    }

    @Test
    fun lerpAngleDegCrossingZero() {
        assertEquals(350f, lerpAngleDeg(350f, 10f, 0.0f), 0.01f)
        assertEquals(0f, lerpAngleDeg(350f, 10f, 0.5f), 0.01f)
        assertEquals(10f, lerpAngleDeg(350f, 10f, 1.0f), 0.01f)
    }

    @Test
    fun lerpAngleDegCrossingZeroBackwards() {
        assertEquals(10f, lerpAngleDeg(10f, 350f, 0.0f), 0.01f)
        assertEquals(0f, lerpAngleDeg(10f, 350f, 0.5f), 0.01f)
        assertEquals(350f, lerpAngleDeg(10f, 350f, 1.0f), 0.01f)
    }

    @Test
    fun testNorm() {
        assertEquals(-1.0f, norm(10f, 20f, 0f), 0.01f)
        assertEquals(0.0f, norm(10f, 20f, 10f), 0.01f)
        assertEquals(0.5f, norm(10f, 20f, 15f), 0.01f)
        assertEquals(1.0f, norm(10f, 20f, 20f), 0.01f)
        assertEquals(2.0f, norm(10f, 20f, 30f), 0.01f)
    }

    @Test
    fun testMap() {
        assertEquals(0f, map(10f, 20f, 100f, 200f, 0f), 0.01f)
        assertEquals(100f, map(10f, 20f, 100f, 200f, 10f), 0.01f)
        assertEquals(150f, map(10f, 20f, 100f, 200f, 15f), 0.01f)
        assertEquals(200f, map(10f, 20f, 100f, 200f, 20f), 0.01f)
        assertEquals(300f, map(10f, 20f, 100f, 200f, 30f), 0.01f)
    }

    @Test
    fun testRandomLong() {
        var r: Long
        for (i in 0..511) {
            assertTrue((random(1L, 5L).also { r = it }) >= 1L && r <= 5L)
            assertTrue((random(6L, 1L).also { r = it }) >= 1L && r <= 6L)
            assertTrue((random(-1L, -7L).also { r = it }) <= -1L && r >= -7L)
            assertTrue((random(-8L, -1L).also { r = it }) <= -1L && r >= -8L)
        }
    }

    @Test
    fun testSinDeg() {
        assertEquals(0f, sinDeg(0f), 0f)
        assertEquals(1f, sinDeg(90f), 0f)
        assertEquals(0f, sinDeg(180f), 0f)
        assertEquals(-1f, sinDeg(270f), 0f)
    }

    @Test
    fun testCosDeg() {
        assertEquals(1f, cosDeg(0f), 0f)
        assertEquals(0f, cosDeg(90f), 0f)
        assertEquals(-1f, cosDeg(180f), 0f)
        assertEquals(0f, cosDeg(270f), 0f)
    }

    @Test
    fun testTanDeg() {
        assertEquals(0f, tanDeg(0f), FLOAT_ROUNDING_ERROR)
        assertEquals(tan(Math.toRadians(45.0)), tanDeg(45f).toDouble(), FLOAT_ROUNDING_ERROR.toDouble())

        assertEquals(tan(Math.toRadians(135.0)), tanDeg(135f).toDouble(), FLOAT_ROUNDING_ERROR.toDouble())
        assertEquals(0f, tanDeg(180f), FLOAT_ROUNDING_ERROR)
    }

    @Test
    fun testAtan2Deg360() {
        assertEquals(0f, atan2Deg360(0f, 1f), FLOAT_ROUNDING_ERROR)
        assertEquals(45f, atan2Deg360(1f, 1f), FLOAT_ROUNDING_ERROR)
        assertEquals(90f, atan2Deg360(1f, 0f), FLOAT_ROUNDING_ERROR)
        assertEquals(135f, atan2Deg360(1f, -1f), FLOAT_ROUNDING_ERROR)
        assertEquals(180f, atan2Deg360(0f, -1f), FLOAT_ROUNDING_ERROR)
        assertEquals(225f, atan2Deg360(-1f, -1f), FLOAT_ROUNDING_ERROR)
        assertEquals(270f, atan2Deg360(-1f, 0f), FLOAT_ROUNDING_ERROR)
        assertEquals(315f, atan2Deg360(-1f, 1f), FLOAT_ROUNDING_ERROR)
    }
}