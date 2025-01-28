package com.badlogic.gdx.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AtomicQueueTest {

    @Test
    fun putTest() {
        val atomicQueue = AtomicQueue<Int>(2)
        // We still have enough space to write another element.
        assertTrue(atomicQueue.put(1))

        // We do not have enough space to write another element as the read pointer is there.
        assertFalse(atomicQueue.put(2))
    }

    @Test
    fun pullTest() {
        val atomicQueue = AtomicQueue<Int>(3)

        // Add elements, note that the last element can not fit in the array.
        atomicQueue.put(1)
        atomicQueue.put(2)
        atomicQueue.put(3)

        // Poll the two elements in the array.
        assertEquals(1, (atomicQueue.poll() as Int).toLong())
        assertEquals(2, (atomicQueue.poll() as Int).toLong())

        // Last element did not fit, so it is null.
        assertNull(atomicQueue.poll())
    }

    @Test
    fun loopAroundTest() {
        val atomicQueue = AtomicQueue<Int>(2)
        // We still have enough space to write another element.
        assertTrue(atomicQueue.put(1))

        // We do not have enough space to write another element as the read pointer is there.
        assertFalse(atomicQueue.put(2))

        // Retrieve the element.
        assertEquals(1, atomicQueue.poll())

        // We can push an element again
        assertTrue(atomicQueue.put(2))
        assertEquals(2, atomicQueue.poll())
    }
}