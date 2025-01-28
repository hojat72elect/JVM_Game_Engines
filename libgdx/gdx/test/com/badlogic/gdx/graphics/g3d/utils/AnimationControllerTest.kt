package com.badlogic.gdx.graphics.g3d.utils

import com.badlogic.gdx.utils.Array as GdxArray
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.model.Animation
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class AnimationControllerTest {

    @Test
    fun testGetFirstKeyframeIndexAtTimeNominal() {
        val keyFrames = GdxArray<NodeKeyframe<String>>()

        keyFrames.add(NodeKeyframe(0f, "1st"))
        keyFrames.add(NodeKeyframe(3f, "2nd"))
        keyFrames.add(NodeKeyframe(12f, "3rd"))
        keyFrames.add(NodeKeyframe(13f, "4th"))

        assertEquals(0, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, -1f))
        assertEquals(0, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 0f))
        assertEquals(0, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 2f))
        assertEquals(1, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 9f))
        assertEquals(2, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 12.5f))
        assertEquals(2, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 13f))
        assertEquals(0, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 14f))
    }

    @Test
    fun testGetFirstKeyframeIndexAtTimeSingleKey() {
        val keyFrames = GdxArray<NodeKeyframe<String>>()

        keyFrames.add(NodeKeyframe(10f, "1st"))

        assertEquals(0, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 9f))
        assertEquals(0, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 10f))
        assertEquals(0, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 11f))
    }

    @Test
    fun testGetFirstKeyframeIndexAtTimeEmpty() {
        val keyFrames = GdxArray<NodeKeyframe<String>>()
        assertEquals(0, BaseAnimationController.getFirstKeyframeIndexAtTime(keyFrames, 3f))
    }

    @Test
    fun testEndUpActionAtDurationTime() {
        val loop = Animation()
        loop.id = "loop"
        loop.duration = 1f

        val action = Animation()
        action.id = "action"
        action.duration = 0.2f

        val modelInstance = ModelInstance(Model())
        modelInstance.animations.add(loop)
        modelInstance.animations.add(action)

        val animationController = AnimationController(modelInstance)

        animationController.setAnimation("loop", -1)
        assertSameAnimation(loop, animationController.current)

        animationController.update(1f)
        assertSameAnimation(loop, animationController.current)

        animationController.update(0.01f)
        assertSameAnimation(loop, animationController.current)

        animationController.action("action", 1, 1f, null, 0f)
        assertSameAnimation(action, animationController.current)

        animationController.update(0.2f)
        assertSameAnimation(loop, animationController.current)
    }

    @Test
    fun testEndUpActionAtDurationTimeReverse() {
        val loop = Animation()
        loop.id = "loop"
        loop.duration = 1f

        val action = Animation()
        action.id = "action"
        action.duration = 0.2f

        val modelInstance = ModelInstance(Model())
        modelInstance.animations.add(loop)
        modelInstance.animations.add(action)

        val animationController = AnimationController(modelInstance)

        animationController.setAnimation("loop", -1, -1f, null)
        assertSameAnimation(loop, animationController.current)

        animationController.update(1f)
        assertSameAnimation(loop, animationController.current)

        animationController.update(0.01f)
        assertSameAnimation(loop, animationController.current)

        animationController.action("action", 1, -1f, null, 0f)
        assertSameAnimation(action, animationController.current)

        animationController.update(0.2f)
        assertSameAnimation(loop, animationController.current)
    }

    companion object {
        private fun assertSameAnimation(expected: Animation, actual: AnimationDesc) {
            if (expected.id != actual.animation.id) {
                Assert.fail("expected: ${expected.id}, actual: ${actual.animation.id}")
            }
        }
    }

}