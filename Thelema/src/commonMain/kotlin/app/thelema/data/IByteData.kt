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

package app.thelema.data

import kotlin.math.min

/** @author zeganstyl */
interface IByteData: IDataArray {
    var order: DataByteOrder

    val isAlive: Boolean

    fun copy(): IByteData {
        val remaining = remaining
        val newBytes = DATA.bytes(remaining)
        val oldPos = position
        for (i in 0 until remaining) {
            newBytes.put(get())
        }
        newBytes.rewind()
        position = oldPos
        return newBytes
    }

    fun copy(position: Int, length: Int): IByteData {
        val oldPos = this.position
        this.position = position
        val newBytes = DATA.bytes(length)
        for (i in 0 until length) {
            newBytes.put(get())
        }
        this.position = oldPos
        newBytes.rewind()
        return newBytes
    }

    fun byteView(): IByteData
    fun byteView(position: Int, length: Int): IByteData {
        val oldPos = this.position
        val oldLim = this.limit
        this.limit = position + length
        this.position = position
        val view = byteView()
        this.limit = oldLim
        this.position = oldPos
        return view
    }

    fun toByteArray(): ByteArray {
        val array = ByteArray(remaining)
        val l = limit
        if (position == 0) {
            var i = position
            while (i < l) {
                array[i] = get(i)
                i++
            }
        } else {
            var j = 0
            var i = position
            while (i < l) {
                array[j] = get(i)
                j++
                i++
            }
        }
        return array
    }

    fun shortView(): IShortData
    fun intView(): IIntData
    fun floatView(): IFloatData

    fun getShort(byteIndex: Int): Short
    fun getInt(byteIndex: Int): Int
    fun getFloat(byteIndex: Int): Float

    fun getUShort(byteIndex: Int): Int

    operator fun set(index: Int, value: Byte)

    operator fun get(index: Int): Byte

    /** Get element and increment position (index in array) */
    fun get(): Byte {
        val value = get(position)
        position++
        return value
    }

    /** Put element and increment position (index in array) */
    fun put(value: Byte) {
        set(position, value)
        position++
    }

    fun put(index: Int, value: Byte) {
        set(index, value)
    }

    fun put(vararg values: Byte) {
        for (i in values.indices) {
            put(values[i])
        }
    }

    fun put(array: ByteArray): IByteData {
        for (i in array.indices) {
            put(array[i])
        }
        return this
    }

    fun put(array: ByteArray, num: Int = array.size, offset: Int = 0): IByteData {
        for (i in offset until num) {
            put(array[i])
        }
        return this
    }

    fun put(bytes: IByteData) {
        val remaining = min(remaining, bytes.remaining)
        for (i in 0 until remaining) {
            put(bytes.get())
        }
    }

    fun put(toIndex: Int, from: IByteData, fromOffset: Int, length: Int) {
        var iother = fromOffset
        var ithis = toIndex
        for (i in 0 until length) {
            put(ithis, from[iother])
            iother++
            ithis++
        }
    }

    /** Integer will be converted to byte */
    fun putByte(value: Int) {
        put(value.toByte())
    }

    /** Integer will be converted to byte */
    fun putBytes(vararg values: Int): IByteData {
        for (i in values.indices) {
            put(values[i].toByte())
        }
        return this
    }

    fun putFloat(byteIndex: Int, value: Float)

    fun putInt(byteIndex: Int, value: Int)

    /** Integer will be converted to short */
    fun putShort(byteIndex: Int, value: Int)

    /** Integer will be converted to short */
    fun putShort(value: Int) {
        putShort(position, value)
        position += 2
    }

    /** Integer will be converted to short */
    fun putShorts(vararg values: Int) {
        for (i in values.indices) {
            putShort(values[i])
        }
    }

    fun putInt(value: Int) {
        putInt(position, value)
        position += 4
    }

    fun putInts(vararg values: Int) {
        for (i in values.indices) {
            putInt(values[i])
        }
    }

    /** Long will be converted to integer */
    fun putInts(vararg values: Long) {
        for (i in values.indices) {
            putInt(values[i].toInt())
        }
    }

    /** Put integer with big endian bytes order. Useful for RGBA colors */
    fun putRGBA(byteIndex: Int, rgba: Int) {
        put(byteIndex, (rgba ushr 24).toByte())
        put(byteIndex + 1, (rgba ushr 16).toByte())
        put(byteIndex + 2, (rgba ushr 8).toByte())
        put(byteIndex + 3, rgba.toByte())
    }
    fun putRGBA(byteIndex: Int, rgba: Long) = putRGBA(byteIndex, rgba.toInt())

    fun putRGBA(rgba: Int) {
        put((rgba ushr 24).toByte())
        put((rgba ushr 16).toByte())
        put((rgba ushr 8).toByte())
        put(rgba.toByte())
    }
    fun putRGBA(rgba: Long) = putRGBA(rgba.toInt())

    fun putRGBAs(vararg colors: Int) {
        for (i in colors.indices) {
            putRGBA(colors[i])
        }
    }
    fun putRGBAs(vararg colors: Long) {
        for (i in colors.indices) {
            putRGBA(colors[i])
        }
    }

    fun putFloat(value: Float) {
        putFloat(position, value)
        position += 4
    }

    fun putVec2(x: Float, y: Float) {
        putFloat(position, x)
        putFloat(position + 4, y)
        position += 8
    }

    fun putVec3(x: Float, y: Float, z: Float) {
        putFloat(position, x)
        putFloat(position + 4, y)
        putFloat(position + 8, z)
        position += 12
    }

    fun putVec4(x: Float, y: Float, z: Float, w: Float) {
        putFloat(position, x)
        putFloat(position + 4, y)
        putFloat(position + 8, z)
        putFloat(position + 12, w)
        position += 16
    }

    fun putFloats(byteStartIndex: Int, vararg values: Float) {
        var bi = byteStartIndex
        for (i in values.indices) {
            putFloat(bi, values[i])
            bi += 4
        }
    }

    fun putFloats(vararg values: Float) {
        for (i in values.indices) {
            putFloat(values[i])
        }
    }

    fun putFloatsArray(values: FloatArray) {
        for (i in values.indices) {
            putFloat(values[i])
        }
    }

    fun putFloatsArray(values: FloatArray, length: Int) {
        for (i in 0 until length) {
            putFloat(values[i])
        }
    }

    fun getFloats(out: FloatArray) {
        for (i in out.indices) {
            out[i] = getFloat()
        }
    }

    fun getFloat(): Float

    override fun toUInt(index: Int): Int = get(index).toInt() and 0xFF

    override fun toUFloat(index: Int): Float = (get(index).toInt() and 0xFF).toFloat()

    fun toStringUTF8(): String

    fun forEachByte(block: (byte: Byte) -> Unit) {
        for (i in 0 until limit) {
            block(get(i))
        }
    }

    fun destroy() {
        if (this != DATA.nullBuffer) {
            DATA.destroyBytes(this)
        }
    }
}