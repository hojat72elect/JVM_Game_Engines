package com.badlogic.gdx.utils

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PooledLinkedListTest {
    private lateinit var list: PooledLinkedList<Int>

    @Before
    fun setUp() {
        list = PooledLinkedList(10)
        list.add(1)
        list.add(2)
        list.add(3)
    }

    @Test
    fun size() {
        Assert.assertEquals(3, list.size())
        list.iter()
        list.next()
        list.remove()
        Assert.assertEquals(2, list.size())
    }

    @Test
    fun iteration() {
        list.iter()
        Assert.assertEquals(1, list.next())
        Assert.assertEquals(2, list.next())
        Assert.assertEquals(3, list.next())
        Assert.assertNull(list.next())
    }

    @Test
    fun reverseIteration() {
        list.iterReverse()
        Assert.assertEquals(3, list.previous())
        Assert.assertEquals(2, list.previous())
        Assert.assertEquals(1, list.previous())
        Assert.assertNull(list.previous())
    }

    @Test
    fun remove() {
        list.iter()
        list.next() // 1
        list.remove()
        list.next() // 2
        list.next() // 3
        list.remove()
        list.iter()
        Assert.assertEquals(2, list.next())
        Assert.assertNull(list.next())
    }

    @Test
    fun removeLast() {
        list.iter()
        Assert.assertEquals(1, list.next())
        Assert.assertEquals(3, list.removeLast())
        Assert.assertEquals(2, list.next())
        Assert.assertNull(list.next())
    }

    @Test
    fun clear() {
        list.clear()
        Assert.assertEquals(0, list.size())
        list.iter()
        Assert.assertNull(list.next())
    }
}