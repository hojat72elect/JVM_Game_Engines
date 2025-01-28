package com.badlogic.gdx.utils

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class JsonValueTest {

    @Test
    fun testAddingRemovedValue() {

        // Prepare two JSON objects
        val firstObject = JsonValue(JsonValue.ValueType.`object`)
        val secondObject = JsonValue(JsonValue.ValueType.`object`)

        firstObject.addChild("a", JsonValue("A"))
        secondObject.addChild("b", JsonValue("B"))
        secondObject.addChild("c", JsonValue("C"))

        // Remove an item from one object and add it to the other
        val b = secondObject.remove("b")
        firstObject.addChild(b)

        // Check if both objects have the expected children
        assertNotNull(firstObject["a"])
        assertNotNull(firstObject["b"])
        assertNull(firstObject["c"])
        assertNull(secondObject["a"])
        assertNull(secondObject["b"])
        assertNotNull(secondObject["c"])
    }
}