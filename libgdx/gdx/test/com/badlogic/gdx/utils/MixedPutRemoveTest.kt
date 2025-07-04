package com.badlogic.gdx.utils

import org.junit.Assert
import org.junit.Test

class MixedPutRemoveTest {

    @Test
    fun testLongMapPut() {
        val gdxMap = LongMap<Int>()
        val jdkMap = HashMap<Long, Int>()
        var stateA = 0L
        var stateB = 1L
        var gdxRepeats = 0
        var jdkRepeats = 0
        var item = 0L

        for (i in 0..0xfffff) { // a million should do
            // simple-ish RNG that repeats more than RandomXS128; we want repeats to test behavior
            stateA += -0x3943d8696d4a3cddL
            item = (stateA xor (stateA ushr 31)) * (-0x61c8864680b583eaL.let { stateB += it; stateB })
            item = item and (item ushr 24) // causes 64-bit state to get crammed into 40 bits, with item biased toward low bit counts
            if (gdxMap.put(item, i) != null) gdxRepeats++
            if (jdkMap.put(item, i) != null) jdkRepeats++
            Assert.assertEquals(gdxMap.size, jdkMap.size)
        }
        Assert.assertEquals(gdxRepeats, jdkRepeats)
    }

    @Test
    fun testLongMapMix() {
        val gdxMap = LongMap<Int>()
        val jdkMap = HashMap<Long, Int>()
        var stateA = 0L
        var stateB = 1L
        var gdxRemovals = 0
        var jdkRemovals = 0
        var item: Long
        for (i in 0..0xfffff) { // 1 million should do
            // simple-ish RNG that repeats more than RandomXS128; we want repeats to test behavior
            stateA += -0x3943d8696d4a3cddL
            item = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
            item = item and (item ushr 24) // causes 64-bit state to get crammed into 40 bits, with item biased toward low bit counts
            if (gdxMap.remove(item) == null) gdxMap.put(item, i)
            else gdxRemovals++
            if (jdkMap.remove(item) == null) jdkMap[item] = i
            else jdkRemovals++
            Assert.assertEquals(gdxMap.size, jdkMap.size)
        }
        Assert.assertEquals(gdxRemovals, jdkRemovals)
    }

    @Test
    fun testLongMapIterator() {
        val gdxMap = LongMap<Long>()
        var stateA = 0L
        var stateB = 1L
        var actualSize = 0
        var item: Long
        for (i in 0..0xffff) { // 64K should do
            // simple-ish RNG that repeats more than RandomXS128; we want repeats to test behavior
            stateA += -0x3943d8696d4a3cddL
            item = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
            item = item and (item ushr 24) // causes 64-bit state to get crammed into 40 bits, with item biased toward low bit counts
            if (gdxMap.put(item, item) == null) actualSize++
            if (actualSize % 6 == 5) {
                val it = gdxMap.values().iterator()
                for (n in (item and 3L).toInt() + 1 downTo 1) {
                    it.next()
                }
                it.remove()
                actualSize--
                // repeat above RNG
                for (j in 0..1) {
                    stateA += -0x3943d8696d4a3cddL
                    item = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
                    item = item and (item ushr 24)
                    if (gdxMap.put(item, item) == null) actualSize++
                }
            }
            Assert.assertEquals(gdxMap.size, actualSize)
        }
        for (ent in gdxMap) {
            Assert.assertEquals(ent.key, ent.value)
        }
    }

    @Test
    fun testIntMapPut() {
        val gdxMap = IntMap<Int>()
        val jdkMap = HashMap<Int, Int>()
        var stateA = 0L
        var stateB = 1L
        var temp: Long
        var gdxRepeats = 0
        var jdkRepeats = 0
        var item: Int
        for (i in 0..0xfffff) { // 1 million should do
            // simple-ish RNG that repeats more than RandomXS128; we want repeats to test behavior
            stateA += -0x3943d8696d4a3cddL
            temp = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
            item = (temp and (temp ushr 24)).toInt() // causes 64-bit state to get crammed into 32 bits, with item biased toward low bit
            // counts
            if (gdxMap.put(item, i) != null) gdxRepeats++
            if (jdkMap.put(item, i) != null) jdkRepeats++
            Assert.assertEquals(gdxMap.size, jdkMap.size)
        }
        Assert.assertEquals(gdxRepeats, jdkRepeats)
    }

    @Test
    fun testIntMapMix() {
        val gdxMap = IntMap<Int>()
        val jdkMap = HashMap<Int, Int>()
        var stateA = 0L
        var stateB = 1L
        var temp: Long
        var gdxRemovals = 0
        var jdkRemovals = 0
        var item: Int
        for (i in 0..0xfffff) { // 1 million should do
            // simple-ish RNG that repeats more than RandomXS128; we want repeats to test behavior
            stateA += -0x3943d8696d4a3cddL
            temp = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
            item = (temp and (temp ushr 24)).toInt() // causes 64-bit state to get crammed into 32 bits, with item biased toward low bit
            // counts
            if (gdxMap.remove(item) == null) gdxMap.put(item, i)
            else gdxRemovals++
            if (jdkMap.remove(item) == null) jdkMap[item] = i
            else jdkRemovals++
            Assert.assertEquals(gdxMap.size, jdkMap.size)
        }
        Assert.assertEquals(gdxRemovals, jdkRemovals)
    }

    @Test
    fun testIntMapIterator() {
        val gdxMap = IntMap<Int>()
        var stateA = 0L
        var stateB = 1L
        var temp: Long
        var actualSize = 0
        var item: Int
        for (i in 0..0xffff) { // 64K should do
            // simple-ish RNG that repeats more than RandomXS128; we want repeats to test behavior
            stateA += -0x3943d8696d4a3cddL
            temp = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
            item = (temp and (temp ushr 24)).toInt() // causes 64-bit state to get crammed into 32 bits, with item biased toward low bit
            // counts
            if (gdxMap.put(item, item) == null) actualSize++
            if (actualSize % 6 == 5) {
                val it = gdxMap.values().iterator()
                for (n in (temp and 3L).toInt() + 1 downTo 1) {
                    it.next()
                }
                it.remove()
                actualSize--
                // repeat above RNG
                for (j in 0..1) {
                    stateA += -0x3943d8696d4a3cddL
                    temp = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
                    item = (temp and (temp ushr 24)).toInt()
                    if (gdxMap.put(item, item) == null) actualSize++
                }
            }
            Assert.assertEquals(gdxMap.size, actualSize)
        }
        for (ent in gdxMap) {
            Assert.assertEquals(ent.key, ent.value)
        }
    }

    @Test
    fun testObjectMapPut() {
        val gdxMap = ObjectMap<Int, Int>()
        val jdkMap = HashMap<Int, Int>()
        var stateA = 0L
        var stateB = 1L
        var temp: Long
        var gdxRepeats = 0
        var jdkRepeats = 0
        var item: Int
        for (i in 0..0xfffff) { // 1 million should do
            // simple-ish RNG that repeats more than RandomXS128; we want repeats to test behavior
            stateA += -0x3943d8696d4a3cddL
            temp = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
            item = (temp and (temp ushr 24)).toInt() // causes 64-bit state to get crammed into 32 bits, with item biased toward low bit
            // counts
            if (gdxMap.put(item, i) != null) gdxRepeats++
            if (jdkMap.put(item, i) != null) jdkRepeats++
            Assert.assertEquals(gdxMap.size, jdkMap.size)
        }
        Assert.assertEquals(gdxRepeats, jdkRepeats)
    }

    @Test
    fun testObjectMapMix() {
        val gdxMap = ObjectMap<Int, Int>()
        val jdkMap = HashMap<Int, Int>()
        var stateA = 0L
        var stateB = 1L
        var temp: Long
        var gdxRemovals = 0
        var jdkRemovals = 0
        var item: Int
        for (i in 0..0xfffff) { // 1 million should do
            // simple-ish RNG that repeats more than RandomXS128; we want repeats to test behavior
            stateA += -0x3943d8696d4a3cddL
            temp = (stateA xor (stateA ushr 31)) * ((-0x61c8864680b583eaL).let { stateB += it; stateB })
            item = (temp and (temp ushr 24)).toInt() // causes 64-bit state to get crammed into 32 bits, with item biased toward low bit
            // counts
            if (gdxMap.remove(item) == null) gdxMap.put(item, i)
            else gdxRemovals++
            if (jdkMap.remove(item) == null) jdkMap[item] = i
            else jdkRemovals++
            Assert.assertEquals(gdxMap.size, jdkMap.size)
        }
        Assert.assertEquals(gdxRemovals, jdkRemovals)
    }
}