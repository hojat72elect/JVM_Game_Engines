package org.openrndr.draw

import org.openrndr.internal.Driver

interface AtomicCounterBuffer: AutoCloseable {
    companion object {
        fun create(counterCount: Int) = Driver.instance.createAtomicCounterBuffer(counterCount)
    }

    fun write(data: IntArray)
    fun read(): IntArray

    /**
     * Reset all the counters to 0
     */
    fun reset()

    fun destroy()

    /**
     * The number of counters
     */
    val size: Int
}