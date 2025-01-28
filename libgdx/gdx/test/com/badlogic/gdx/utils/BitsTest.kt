package com.badlogic.gdx.utils

import org.junit.Assert
import org.junit.Test

class BitsTest {

    @Test
    fun testHashcodeAndEquals() {
        val b1 = Bits()
        val b2 = Bits()

        b1.set(1)
        b2.set(1)

        Assert.assertEquals(b1.hashCode(), b2.hashCode())
        Assert.assertEquals(b1, b2)

        // temporarily setting/clearing a single bit causing the backing array to grow
        b2.set(420)
        b2.clear(420)

        Assert.assertEquals(b1.hashCode(), b2.hashCode())
        Assert.assertEquals(b1, b2)

        b1.set(810)
        b1.clear(810)

        Assert.assertEquals(b1.hashCode(), b2.hashCode())
        Assert.assertEquals(b1, b2)
    }

    @Test
    fun testXor() {
        val b1 = Bits()
        val b2 = Bits()

        b2.set(200)


        // b1's array should grow to accommodate b2
        b1.xor(b2)

        Assert.assertTrue(b1.get(200))

        b1.set(1024)
        b2.xor(b1)

        Assert.assertTrue(b2.get(1024))
    }

    @Test
    fun testOr() {
        val b1 = Bits()
        val b2 = Bits()

        b2.set(200)

        // b1's array should grow to accommodate b2
        b1.or(b2)

        Assert.assertTrue(b1.get(200))

        b1.set(1024)
        b2.or(b1)

        Assert.assertTrue(b2.get(1024))
    }

    @Test
    fun testAnd() {
        val b1 = Bits()
        val b2 = Bits()

        b2.set(200)
        // b1 should cancel b2:s bit
        b2.and(b1)

        Assert.assertFalse(b2.get(200))

        b1.set(400)
        b1.and(b2)

        Assert.assertFalse(b1.get(400))
    }

    @Test
    fun testCopyConstructor() {
        val b1 = Bits()
        b1.set(50)
        b1.set(100)
        b1.set(150)

        val b2 = Bits(b1)
        Assert.assertNotSame(b1, b2)
        Assert.assertTrue(b1.containsAll(b2))
        Assert.assertTrue(b2.containsAll(b1))
        Assert.assertEquals(b1, b2)
    }
}