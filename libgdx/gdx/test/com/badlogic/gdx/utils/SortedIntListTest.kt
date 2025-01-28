package com.badlogic.gdx.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class SortedIntListTest {
    @Test
    fun testIteratorWithAllocation() {
        Collections.allocateIterators = true
        try {
            val list = SortedIntList<String>()
            list.insert(0, "hello")
            assertEquals(1, list.size.toLong())
            assertEquals("hello", list[0])
            assertEquals("hello", list.iterator().next().value)
        } finally {
            Collections.allocateIterators = false
        }
    }
}
