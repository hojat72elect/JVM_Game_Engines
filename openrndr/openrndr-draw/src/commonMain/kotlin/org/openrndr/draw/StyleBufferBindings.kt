package org.openrndr.draw

import org.openrndr.draw.font.BufferAccess
import org.openrndr.draw.font.BufferFlag

interface StyleBufferBindings {

    var bufferValues: MutableMap<String, Any>
    val buffers: MutableMap<String, String>
    val bufferTypes: MutableMap<String, String>
    val bufferFlags: MutableMap<String, Set<BufferFlag>>
    val bufferAccess: MutableMap<String, BufferAccess>

    fun buffer(name: String, buffer: ShaderStorageBuffer) {
        bufferValues[name] = buffer
        buffers[name] = buffer.format.hashCode().toString()
    }

    fun buffer(name: String, buffer: AtomicCounterBuffer) {
        bufferValues[name] = buffer
        buffers[name] = "AtomicCounterBuffer"
    }
}

inline fun <reified T : Struct<T>> StyleBufferBindings.registerStructuredBuffer(
    name: String,
    access: BufferAccess = BufferAccess.READ_WRITE,
    flags: Set<BufferFlag> = emptySet()
) {
    bufferTypes[name] = "struct ${T::class.simpleName}"
    bufferFlags[name] = flags
    bufferAccess[name] = access
}

inline fun <reified T : Struct<T>> StyleBufferBindings.structuredBuffer(name: String, buffer: StructuredBuffer<T>) {
    bufferValues[name] = buffer
    buffers[name] = buffer.ssbo.format.hashCode().toString()
    bufferTypes[name] = "struct ${T::class.simpleName}"
}
