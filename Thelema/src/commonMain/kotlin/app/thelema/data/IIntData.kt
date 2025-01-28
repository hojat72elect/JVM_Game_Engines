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

/** @author zeganstyl */
interface IIntData: IDataArray {
    override fun toUInt(index: Int): Int = get(index)
    override fun toUFloat(index: Int): Float = get(index).toFloat()

    operator fun set(index: Int, value: Int)

    operator fun get(index: Int): Int

    /** Put element and increment position (index in array) */
    fun put(value: Int) {
        set(position, value)
        position++
    }

    fun put(index: Int, value: Int) {
        set(index, value)
    }

    fun put(vararg values: Int) {
        for (i in values.indices) {
            put(values[i])
        }
    }

    /** Get element and increment position (index in array) */
    fun get(): Int {
        val value = get(position)
        position++
        return value
    }
}
