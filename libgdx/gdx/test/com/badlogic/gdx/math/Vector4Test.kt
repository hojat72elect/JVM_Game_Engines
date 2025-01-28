package com.badlogic.gdx.math

import org.junit.Assert.assertEquals
import org.junit.Test

class Vector4Test {
    @Test
    fun testToString() {
        assertEquals("(-5.0,42.00055,44444.32,-1.975)", Vector4(-5f, 42.00055f, 44444.32f, -1.975f).toString())
    }

    @Test
    fun testFromString() {
        assertEquals(Vector4(-5f, 42.00055f, 44444.32f, -1.975f), Vector4().fromString("(-5,42.00055,44444.32,-1.9750)"))
    }
}
