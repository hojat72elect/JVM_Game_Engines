/*
 * Copyright 2020-2021 Anton Trushkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.thelema.gl

import app.thelema.data.DATA
import app.thelema.data.IByteData
import app.thelema.utils.iterate
import kotlin.math.min

/** VBO interface */
interface IVertexBuffer: IGLBuffer {
    var verticesCount: Int

    /** @return the [IVertexAccessor] as specified during construction. */
    val vertexAttributes: List<IVertexAccessor>

    /** The size of a single vertex in bytes. It is updated only when any vertex input added or removed */
    val bytesPerVertex: Int

    override val target: Int
        get() = GL_ARRAY_BUFFER

    fun addBufferListener(listener: VertexBufferListener)

    fun removeBufferListener(listener: VertexBufferListener)

    /** Set divisor for all attributes */
    @Deprecated("")
    fun setDivisor(divisor: Int = 1) {}

    /** You can use this after adding all vertex inputs
     * @param count vertices count */
    fun initVertexBuffer(count: Int, block: IByteData.() -> Unit = {})

    /** Set capacity and keep old data */
    fun resizeVertexBuffer(newVerticesCount: Int)

    fun addAttribute(attribute: IVertexAttribute): IVertexAccessor

    fun addAttributes(vararg attributes: IVertexAttribute) {
        attributes.iterate { addAttribute(it) }
    }

    fun getOrAddAccessor(attribute: IVertexAttribute): IVertexAccessor {
        val accessor = getAccessorOrNull(attribute)
        if (accessor != null) return accessor

        return addAttribute(attribute)
    }

    fun removeAccessorAt(index: Int)

    fun removeAccessor(name: String)

    fun removeAccessor(attribute: IVertexAccessor)

    fun getAccessor(attribute: IVertexAttribute): IVertexAccessor = getAccessorOrNull(attribute)!!

    fun getAccessor(name: String): IVertexAccessor = getAccessorOrNull(name)!!

    fun getAccessorOrNull(attribute: IVertexAttribute): IVertexAccessor?

    fun getAccessorOrNull(name: String): IVertexAccessor?

    fun containsAccessor(attribute: IVertexAttribute): Boolean = getAccessorOrNull(attribute) != null

    fun containsAccessor(name: String): Boolean = getAccessorOrNull(name) != null

    fun printVertexAttributes(): String
}

class VertexBuffer(override var bytes: IByteData = DATA.nullBuffer): IVertexBuffer {
    constructor(block: VertexBuffer.() -> Unit): this() {
        block(this)
    }

    constructor(
        verticesCount: Int,
        vararg attributes: IVertexAttribute,
        block: IByteData.() -> Unit = {}
    ): this() {
        addAttributes(*attributes)
        initVertexBuffer(verticesCount, block)
    }

    private val vertexAttributesInternal = ArrayList<IVertexAccessor>()
    override val vertexAttributes: List<IVertexAccessor>
        get() = vertexAttributesInternal

    override var verticesCount: Int = 0

    override var gpuUploadRequested = false

    override var usage: Int = GL_STATIC_DRAW

    override var bufferHandle: Int = 0

    private var bytesPerVertexInternal: Int = 0
    override val bytesPerVertex: Int
        get() = bytesPerVertexInternal

    private var listeners: ArrayList<VertexBufferListener>? = null

    override fun uploadBufferToGpu() {
        super.uploadBufferToGpu()
        listeners?.also { listeners ->
            for (i in listeners.indices) {
                listeners[i].bufferUploadedToGPU(this)
            }
        }
    }

    override fun addBufferListener(listener: VertexBufferListener) {
        if (listeners == null) listeners = ArrayList()
        listeners?.add(listener)
    }

    override fun removeBufferListener(listener: VertexBufferListener) {
        listeners?.remove(listener)
    }

    override fun getAccessorOrNull(attribute: IVertexAttribute): IVertexAccessor? =
        vertexAttributesInternal.firstOrNull { it.attribute == attribute }

    override fun getAccessorOrNull(name: String): IVertexAccessor? =
        vertexAttributesInternal.firstOrNull { it.attribute.name == name }

    fun updateVertexInputOffsets() {
        vertexAttributes.forEach {
            it.updateOffset()
        }

        bytesPerVertexInternal = lastInputByte()
    }

    private fun lastInputByte(): Int = vertexAttributes.lastOrNull()?.nextInputByte ?: 0

    override fun addAttribute(attribute: IVertexAttribute): IVertexAccessor {
        val accessor = VertexAccessor(this, attribute, lastInputByte())
        vertexAttributesInternal.add(accessor)
        bytesPerVertexInternal = lastInputByte()
        listeners?.iterate { it.addedAccessor(accessor) }
        return accessor
    }

    /** After removing, you must call [updateVertexInputOffsets] manually */
    override fun removeAccessorAt(index: Int) {
        val accessor = vertexAttributesInternal.removeAt(index)
        listeners?.iterate { it.removedAccessor(accessor) }
    }

    /** After removing, you must call [updateVertexInputOffsets] manually */
    override fun removeAccessor(name: String) {
        val accessor = vertexAttributesInternal.firstOrNull { it.attribute.name == name }
        if (accessor != null) {
            vertexAttributesInternal.remove(accessor)
            listeners?.iterate { it.removedAccessor(accessor) }
        }
    }

    /** After removing, you must call [updateVertexInputOffsets] manually */
    override fun removeAccessor(attribute: IVertexAccessor) {
        if (vertexAttributesInternal.remove(attribute)) {
            listeners?.iterate { it.removedAccessor(attribute) }
        }
    }

    override fun initVertexBuffer(count: Int, block: IByteData.() -> Unit) {
        this.verticesCount = count
        bytes.destroy()
        bytes = DATA.bytes(count * bytesPerVertex)
        block(bytes)
        bytes.rewind()
        requestBufferUploading()
    }

    override fun resizeVertexBuffer(newVerticesCount: Int) {
        if (bytes == DATA.nullBuffer) {
            initVertexBuffer(newVerticesCount)
        } else {
            this.verticesCount = newVerticesCount
            val oldBytes = bytes
            bytes = DATA.bytes(newVerticesCount * bytesPerVertex)

            oldBytes.rewind()
            val bytesToCopy = min(oldBytes.limit, bytes.limit)
            for (i in 0 until bytesToCopy) {
                bytes.put(oldBytes.get())
            }
            bytes.rewind()

            requestBufferUploading()
            oldBytes.destroy()
        }
    }

    override fun toString(): String {
        return super.toString() + "[bufferHandle=$bufferHandle]"
    }

    override fun printVertexAttributes(): String  = StringBuilder().apply {
        append("[\n")
        vertexAttributes.forEach {
            append("(")
            append(it)
            append(")")
            append("\n")
        }
        append("]")
    }.toString()
}

interface VertexBufferListener {
    fun addedAccessor(accessor: IVertexAccessor) {}

    fun removedAccessor(accessor: IVertexAccessor) {}

    fun bufferUploadedToGPU(buffer: IGLBuffer) {}
}
