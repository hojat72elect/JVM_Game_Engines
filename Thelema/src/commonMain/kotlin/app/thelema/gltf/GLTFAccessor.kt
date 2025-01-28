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

package app.thelema.gltf

import app.thelema.data.IByteData
import app.thelema.gl.*

/** [glTF 2.0 specification](https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#reference-accessor)
 *
 * @author zeganstyl */
class GLTFAccessor(array: IGLTFArray) : GLTFArrayElementAdapter(array) {
    var bufferView: Int = -1
    var byteOffset: Int = 0
    var componentType: Int = -1
    var normalized: Boolean = false
    var count: Int = -1
    var type: String = ""
    var max: FloatArray? = null
    var min: FloatArray? = null
    var sparse: GLTFSparse? = null
    override var name: String = ""

    fun getOrWaitSetupBuffer(block: (bytes: IByteData) -> Unit) {
        val view = gltf.bufferViews[bufferView] as GLTFBufferView
        gltf.buffers.getOrWait(view.buffer) { buffer ->
            buffer as GLTFBuffer
            //buffer.bytes.limit = view.byteOffset + view.byteLength
            buffer.bytes.limit = view.byteOffset + view.byteLength
            buffer.bytes.position = view.byteOffset + byteOffset

            block(buffer.bytes)
        }
    }

    /** Element size in components */
    fun typeSize() = when (type) {
        "SCALAR" -> 1
        "VEC2" -> 2
        "VEC3" -> 3
        "VEC4", "MAT2" -> 4
        "MAT3" -> 9
        "MAT4" -> 16
        else -> throw IllegalStateException("type not known yet : $type")
    }

    /** Component size in bytes */
    fun componentTypeSize() = when (componentType) {
        5120 -> 1 // byte
        5121 -> 1 // ubyte
        5122 -> 2 // short
        5123 -> 2 // ushort
        5125 -> 4 // uint
        5126 -> 4 // float
        else -> throw IllegalStateException("type not known yet : $componentType")
    }

    fun glComponentType() = when (componentType) {
        5120 -> GL_BYTE
        5121 -> GL_UNSIGNED_BYTE
        5122 -> GL_SHORT
        5123 -> GL_UNSIGNED_SHORT
        5125 -> GL_UNSIGNED_INT
        5126 -> GL_FLOAT
        else -> throw IllegalStateException("type not known yet : $componentType")
    }

    /** Element size in bytes
     *
     * [glTF 2.0 specification](https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#accessor-element-size) */
    fun elementSize() = typeSize() * componentTypeSize()

    fun size() = elementSize() * count

    override fun readJson() {
        super.readJson()

        bufferView = json.int("bufferView", -1)
        byteOffset = json.int("byteOffset", 0)
        componentType = json.int("componentType")

        normalized = json.bool("normalized", when (componentType) {
            GLTF.Float -> false
            else -> true
        })

        count = json.int("count")
        type = json.string("type")

        max = null
        json.array("max") { max = FloatArray(size) { float(it) } }

        min = null
        json.array("min") { min = FloatArray(size) { float(it) } }

        sparse = null
        json.get("sparse") {
            sparse = GLTFSparse()
            sparse?.readJson(this)
        }

        ready()
    }

    override fun writeJson() {
        json["componentType"] = componentType
        json["count"] = count
        json["type"] = type
        if (bufferView != -1) json["bufferView"] = bufferView
        if (byteOffset > 0) json["byteOffset"] = byteOffset
        if (normalized) json["normalized"] = normalized

        val max = max
        if (max != null) {
            json.setArray("max") {
                for (i in max.indices) {
                    add(max[i])
                }
            }
        }

        val min = min
        if (min != null) {
            json.setArray("min") {
                for (i in min.indices) {
                    add(min[i])
                }
            }
        }

        val sparse = sparse
        if (sparse != null) json["sparse"] = sparse

        if (name.isNotEmpty()) json["name"] = name
    }

    override fun destroy() {}
}