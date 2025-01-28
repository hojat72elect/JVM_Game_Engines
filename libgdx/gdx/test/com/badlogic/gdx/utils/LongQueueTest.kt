package com.badlogic.gdx.utils

import org.junit.Assert
import org.junit.Test

class LongQueueTest {

    private fun assertValues(q: LongQueue, vararg values: Long) {
        var i = 0
        val n = values.size
        while (i < n) {
            Assert.assertEquals(values[i], q[i])
            i++
        }
    }

    private fun assertEqualsAndHash(q1: LongQueue, q2: LongQueue) {
        Assert.assertEquals(q1, q2)
        Assert.assertEquals("Hash codes are not equal", q1.hashCode(), q2.hashCode())
    }

    @Test
    fun addFirstAndLastTest() {
        val queue = LongQueue()
        queue.addFirst(1)
        queue.addLast(2)
        queue.addFirst(3)
        queue.addLast(4)

        Assert.assertEquals(0, queue.indexOf(3))
        Assert.assertEquals(1, queue.indexOf(1))
        Assert.assertEquals(2, queue.indexOf(2))
        Assert.assertEquals(3, queue.indexOf(4))
    }

    @Test
    fun removeLastTest() {
        val queue = LongQueue()
        queue.addLast(1)
        queue.addLast(2)
        queue.addLast(3)
        queue.addLast(4)

        Assert.assertEquals(4, queue.size)
        Assert.assertEquals(3, queue.indexOf(4))
        Assert.assertEquals(4, queue.removeLast())

        Assert.assertEquals(3, queue.size)
        Assert.assertEquals(2, queue.indexOf(3))
        Assert.assertEquals(3, queue.removeLast())

        Assert.assertEquals(2, queue.size)
        Assert.assertEquals(1, queue.indexOf(2))
        Assert.assertEquals(2, queue.removeLast())

        Assert.assertEquals(1, queue.size)
        Assert.assertEquals(0, queue.indexOf(1))
        Assert.assertEquals(1, queue.removeLast())

        Assert.assertEquals(0, queue.size)
    }

    @Test
    fun removeFirstTest() {
        val queue = LongQueue()
        queue.addLast(1)
        queue.addLast(2)
        queue.addLast(3)
        queue.addLast(4)

        Assert.assertEquals(4, queue.size)
        Assert.assertEquals(0, queue.indexOf(1))
        Assert.assertEquals(1, queue.removeFirst())

        Assert.assertEquals(3, queue.size)
        Assert.assertEquals(0, queue.indexOf(2))
        Assert.assertEquals(2, queue.removeFirst())

        Assert.assertEquals(2, queue.size)
        Assert.assertEquals(0, queue.indexOf(3))
        Assert.assertEquals(3, queue.removeFirst())

        Assert.assertEquals(1, queue.size)
        Assert.assertEquals(0, queue.indexOf(4))
        Assert.assertEquals(4, queue.removeFirst())

        Assert.assertEquals(0, queue.size)
    }

    @Test
    fun resizableQueueTest() {
        val q = LongQueue(8)

        Assert.assertEquals("New queue is not empty!", 0, q.size)

        for (i in 0..99) {
            for (j in 0 until i) {
                try {
                    q.addLast(j.toLong())
                } catch (e: IllegalStateException) {
                    Assert.fail("Failed to add element $j ($i)")
                }
                val peeked = q.last()
                Assert.assertEquals("peekLast shows $peeked, should be $j ($i)", peeked, j.toLong())
                val size = q.size
                Assert.assertEquals("Size should be " + (j + 1) + " but is " + size + " (" + i + ")", size, j + 1)
            }

            if (i != 0) {
                val peek = q.first()
                Assert.assertEquals("First thing is not zero but $peek ($i)", 0, peek)
            }

            for (j in 0 until i) {
                val pop = q.removeFirst()
                Assert.assertEquals("Popped should be $j but is $pop ($i)", pop, j.toLong())

                val size = q.size
                Assert.assertEquals("Size should be " + (i - 1 - j) + " but is " + size + " (" + i + ")", size, i - 1 - j)
            }

            Assert.assertEquals("Not empty after cycle $i", 0, q.size)
        }

        for (i in 0..55) {
            q.addLast(42)
        }
        q.clear()
        Assert.assertEquals("Clear did not clear properly", 0, q.size)
    }

    /**
     * Same as resizableQueueTest, but in reverse.
     */
    @Test
    fun resizableDequeTest() {
        val q = LongQueue(8)

        Assert.assertEquals("New deque is not empty!", 0, q.size)

        for (i in 0..99) {
            for (j in 0 until i) {
                try {
                    q.addFirst(j.toLong())
                } catch (e: IllegalStateException) {
                    Assert.fail("Failed to add element $j ($i)")
                }
                val peeked = q.first()
                Assert.assertEquals("peek shows $peeked, should be $j ($i)", peeked, j.toLong())
                val size = q.size
                Assert.assertEquals("Size should be " + (j + 1) + " but is " + size + " (" + i + ")", size, j + 1)
            }

            if (i != 0) {
                val peek = q.last()
                Assert.assertEquals("Last thing is not zero but $peek ($i)", 0, peek)
            }

            for (j in 0 until i) {
                val pop = q.removeLast()
                Assert.assertEquals("Popped should be $j but is $pop ($i)", pop, j.toLong())

                val size = q.size
                Assert.assertEquals("Size should be " + (i - 1 - j) + " but is " + size + " (" + i + ")", size, i - 1 - j)
            }

            Assert.assertEquals("Not empty after cycle $i", 0, q.size)
        }

        for (i in 0..55) {
            q.addFirst(42)
        }
        q.clear()
        Assert.assertEquals("Clear did not clear properly", 0, q.size)
    }

    @Test
    fun getTest() {
        val q = LongQueue(7)
        for (i in 0..4) {
            for (j in 0..3) {
                q.addLast(j.toLong())
            }
            Assert.assertEquals("get(0) is not equal to peek ($i)", q.get(0), q.first())
            Assert.assertEquals("get(size-1) is not equal to peekLast ($i)", q.get(q.size - 1), q.last())
            for (j in 0..3) {
                Assert.assertEquals(q.get(j), j.toLong())
            }
            for (j in 0 until 4 - 1) {
                q.removeFirst()
                Assert.assertEquals("get(0) is not equal to peek ($i)", q.get(0), q.first())
            }
            q.removeFirst()
            assert(
                q.size == 0 // Failing this means broken test
            )
            try {
                q.get(0)
                Assert.fail("get() on empty queue did not throw")
            } catch (ignore: IndexOutOfBoundsException) {
                // Expected
            }
        }
    }

    @Test
    fun removeTest() {
        val q = LongQueue()

        // Test head < tail.
        for (j in 0..6) q.addLast(j.toLong())
        assertValues(q, 0, 1, 2, 3, 4, 5, 6)
        q.removeIndex(0)
        assertValues(q, 1, 2, 3, 4, 5, 6)
        q.removeIndex(1)
        assertValues(q, 1, 3, 4, 5, 6)
        q.removeIndex(4)
        assertValues(q, 1, 3, 4, 5)
        q.removeIndex(2)
        assertValues(q, 1, 3, 5)

        // Test head >= tail and index >= head.
        q.clear()
        for (j in 2 downTo 0) q.addFirst(j.toLong())
        for (j in 3..6) q.addLast(j.toLong())
        assertValues(q, 0, 1, 2, 3, 4, 5, 6)
        q.removeIndex(1)
        assertValues(q, 0, 2, 3, 4, 5, 6)
        q.removeIndex(0)
        assertValues(q, 2, 3, 4, 5, 6)

        // Test head >= tail and index < tail.
        q.clear()
        for (j in 2 downTo 0) q.addFirst(j.toLong())
        for (j in 3..6) q.addLast(j.toLong())
        assertValues(q, 0, 1, 2, 3, 4, 5, 6)
        q.removeIndex(5)
        assertValues(q, 0, 1, 2, 3, 4, 6)
        q.removeIndex(5)
        assertValues(q, 0, 1, 2, 3, 4)
    }

    @Test
    fun indexOfTest() {
        val q = LongQueue()

        // Test head < tail.
        for (j in 0..6) q.addLast(j.toLong())
        for (j in 0..6) Assert.assertEquals(q.indexOf(j.toLong()), j)

        // Test head >= tail.
        q.clear()
        for (j in 2 downTo 0) q.addFirst(j.toLong())
        for (j in 3..6) q.addLast(j.toLong())
        for (j in 0..6) Assert.assertEquals(q.indexOf(j.toLong()), j)
    }

    @Test
    fun toStringTest() {
        val q = LongQueue(1)
        Assert.assertEquals("[]", q.toString())
        q.addLast(4)
        Assert.assertEquals("[4]", q.toString())
        q.addLast(5)
        q.addLast(6)
        q.addLast(7)
        Assert.assertEquals("[4, 5, 6, 7]", q.toString())
    }

    @Test
    fun hashEqualsTest() {
        val q1 = LongQueue()
        val q2 = LongQueue()

        assertEqualsAndHash(q1, q2)
        q1.addFirst(1)
        Assert.assertNotEquals(q1, q2)
        q2.addFirst(1)
        assertEqualsAndHash(q1, q2)

        q1.clear()
        q1.addLast(1)
        q1.addLast(2)
        q2.addLast(2)
        assertEqualsAndHash(q1, q2)

        for (i in 0..99) {
            q1.addLast(i.toLong())
            q1.addLast(i.toLong())
            q1.removeFirst()

            Assert.assertNotEquals(q1, q2)

            q2.addLast(i.toLong())
            q2.addLast(i.toLong())
            q2.removeFirst()

            assertEqualsAndHash(q1, q2)
        }
    }
}