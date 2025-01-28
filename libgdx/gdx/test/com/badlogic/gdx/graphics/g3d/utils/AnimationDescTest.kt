package com.badlogic.gdx.graphics.g3d.utils

import com.badlogic.gdx.graphics.g3d.model.Animation
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AnimationDescTest {

    private var anim = AnimationDesc()

    @Before
    fun setup() {
        anim.animation = Animation()
        anim.duration = 1F
        anim.listener = null
        anim.loopCount = 1
        anim.offset = 0F
        anim.speed = 1F
        anim.time = 0F
    }

    @Test
    fun testUpdateNominal() {
        assertEquals(-1f, anim.update(.75f), EPSILON)
        assertEquals(.5f, anim.update(.75f), EPSILON)
        assertEquals(.75f, anim.update(.75f), EPSILON)
    }

    @Test
    fun testUpdateJustEnd() {
        assertEquals(-1f, anim.update(.5f), EPSILON)
        assertEquals(0f, anim.update(.5f), EPSILON)
        assertEquals(.5f, anim.update(.5f), EPSILON)
    }

    @Test
    fun testUpdateBigDelta() {
        assertEquals(4.2f, anim.update(5.2f), EPSILON)
        assertEquals(7.3f, anim.update(7.3f), EPSILON)
    }

    @Test
    fun testUpdateZeroDelta() {
        assertEquals(-1f, anim.update(0f), EPSILON)
        assertEquals(0f, anim.time, EPSILON)
    }

    @Test
    fun testUpdateReverseNominal() {
        anim.speed = -1f
        anim.time = anim.duration

        assertEquals(-1f, anim.update(.75f), EPSILON)
        assertEquals(.5f, anim.update(.75f), EPSILON)
        assertEquals(.75f, anim.update(.75f), EPSILON)
    }

    @Test
    fun testUpdateReverseJustEnd() {
        anim.speed = -1f
        anim.time = anim.duration

        assertEquals(-1f, anim.update(.5f), EPSILON)
        assertEquals(0f, anim.update(.5f), EPSILON)
        assertEquals(.5f, anim.update(.5f), EPSILON)
    }

    @Test
    fun testUpdateReverseBigDelta() {
        anim.speed = -1f
        anim.time = anim.duration

        assertEquals(4.2f, anim.update(5.2f), EPSILON)
        assertEquals(7.3f, anim.update(7.3f), EPSILON)
    }

    @Test
    fun testUpdateReverseZeroDelta() {
        anim.speed = -1f
        anim.time = anim.duration

        assertEquals(-1f, anim.update(0f), EPSILON)
        assertEquals(anim.duration, anim.time, EPSILON)
    }

    companion object {
        private const val EPSILON = 1e-6F
    }
}