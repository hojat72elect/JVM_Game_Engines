package com.badlogic.gdx.math

import com.badlogic.gdx.utils.FloatArray as GdxFloatArray
import kotlin.FloatArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ConvexHullTest {

    @Test
    fun testComputePolygon() {

        val convexHull = ConvexHull()
        val rawPolygon = floatArrayOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f)
        val polygonCounterclockwise = floatArrayOf(1f, 0f, 1f, 1f, 0f, 1f, 0f, 0f)

        assertArraySimilar(convexHull.computePolygon(rawPolygon, 0, 8, false), polygonCounterclockwise, 0, 8)
        assertArraySimilar(convexHull.computePolygon(rawPolygon, false), polygonCounterclockwise, 0, 8)
        assertArraySimilar(convexHull.computePolygon(rawPolygon, 2, 6, false), polygonCounterclockwise, 0, 6)
        assertArraySimilar(convexHull.computePolygon(rawPolygon, 0, 6, false), polygonCounterclockwise, 2, 6)

        assertArraySimilar(ConvexHull().computePolygon(rawPolygon, 0, 8, false), polygonCounterclockwise, 0, 8)
        assertArraySimilar(ConvexHull().computePolygon(rawPolygon, false), polygonCounterclockwise, 0, 8)
        assertArraySimilar(ConvexHull().computePolygon(rawPolygon, 2, 6, false), polygonCounterclockwise, 0, 6)
        assertArraySimilar(ConvexHull().computePolygon(rawPolygon, 0, 6, false), polygonCounterclockwise, 2, 6)
    }

    private fun assertArraySimilar(array: GdxFloatArray, witness: FloatArray, witnessOffset: Int, witnessCount: Int) {

        // under this method the same contents of the arrays must be in the same order (and have the same values),
        // but they are not required to start with the same element
        // The first two elements of the output array should correspond to the last two elements,
        // otherwise the last two element is ignored.

        val witnessLength = witnessCount + witnessOffset
        assertTrue(witnessCount + witnessOffset <= witness.size)
        assertEquals(witnessCount + 2, array.size)
        assertEquals(array.items[0], array.items[array.size - 2], 0f)
        assertEquals(array.items[1], array.items[array.size - 1], 0f)

        for (offset in 0 until witnessLength) {
            var contentMatches = true
            for (i in 0 until witnessLength) {
                val j = ((offset + i) % witnessCount) + witnessOffset
                if (array[i] != witness[j]) {
                    contentMatches = false
                    break
                }
            }
            if (contentMatches) return
        }

        fail("Array items $array does not match witness array ${witness.contentToString()}")
    }
}