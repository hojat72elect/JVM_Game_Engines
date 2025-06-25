package com.badlogic.gdx.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


class FlushablePoolTest {

    @Test
    fun `Initialization of a FlushablePool, example 1`() {
        val sut = FlushablePoolExample()
        assertEquals(0, sut.free)
        assertEquals(Integer.MAX_VALUE, sut.max)
    }

    @Test
    fun `Initialization of a FlushablePool, example 2`() {
        val sut = FlushablePoolExample(10)
        assertEquals(0, sut.free)
        assertEquals(Integer.MAX_VALUE, sut.max)
    }

    @Test
    fun `Initialization of a FlushablePool, example 3`() {
        val sut = FlushablePoolExample(10, 10)
        assertEquals(0, sut.free)
        assertEquals(10, sut.max)
    }

    @Test
    fun `Can correctly obtain an object from a FlushablePool`() {
        val sut = FlushablePoolExample(10, 10)
        sut.newObject()
        assertEquals(0, sut.obtained.size)
        sut.obtain()
        assertEquals(1, sut.obtained.size)
        sut.flush()
        assertEquals(0, sut.obtained.size)
    }

    @Test
    fun `Can correctly flush a FlushablePool`() {
        val sut = FlushablePoolExample(10, 10)
        sut.newObject()
        sut.obtain()
        assertEquals(1, sut.obtained.size)
        sut.flush()
        assertEquals(0, sut.obtained.size)
    }

    @Test
    fun `Can correctly free objects from a FlushablePool`() {

        // first create the pool
        val sut = FlushablePoolExample(10, 10)
        sut.newObject()
        sut.newObject()

        // Then obtain the required elements
        val element1 = sut.obtain()
        val element2 = sut.obtain()

        // Now test the preconditions
        assertTrue(sut.obtained.contains(element1, true))
        assertTrue(sut.obtained.contains(element2, true))

        // Finally, free the elements and check containment
        sut.free(element2)
        assertTrue(sut.obtained.contains(element1, true))
        assertFalse(sut.obtained.contains(element2, true))
    }


    @Test
    fun `Can correctly free all objects from a FlushablePool`() {

        // first, create a flushable pool.
        val sut = FlushablePoolExample(5, 5)
        sut.newObject()
        sut.newObject()

        // Then, obtain elements from the pool.
        val element1 = sut.obtain()
        val element2 = sut.obtain()

        // Create an array with the obtained elements.
        val elementArray = Array<String>()
        elementArray.add(element1)
        elementArray.add(element2)

        // Test preconditions.
        assertTrue(sut.obtained.contains(element1, true))
        assertTrue(sut.obtained.contains(element2, true))

        // Finally, free all the elements and check containment.
        sut.freeAll(elementArray)
        assertFalse(sut.obtained.contains(element1, true))
        assertFalse(sut.obtained.contains(element2, true))
    }

}

/**
 * This is a FlushablePool<String> which doesn't implement anything special on its own.
 * we just use it to test the functionalities of a FlushablePool.
 */
private class FlushablePoolExample : FlushablePool<String> {
    constructor() : super()

    constructor(initialCapacity: Int) : super(initialCapacity)

    constructor(initialCapacity: Int, max: Int) : super(initialCapacity, max)

    public override fun newObject() = free.toString()

}