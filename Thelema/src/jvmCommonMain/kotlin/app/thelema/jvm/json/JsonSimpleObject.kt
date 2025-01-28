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

package app.thelema.jvm.json

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import app.thelema.json.IJsonArray
import app.thelema.json.IJsonArrayIO
import app.thelema.json.IJsonObject
import app.thelema.json.IJsonObjectIO
import java.math.BigDecimal

/** @author zeganstyl */
class JsonSimpleObject(val source: JsonObject = JsonObject()): IJsonObject {
    override val size: Int
        get() = source.size

    override val sourceObject: Any
        get() = source

    override fun printJson(): String = source.toJson()

    override fun obj(key: String) = JsonSimpleObject(source[key] as JsonObject)
    override fun array(key: String) = JsonSimpleArray(source[key] as JsonArray)

    override fun string(key: String): String = source[key] as String
    override fun float(key: String): Float = (source[key] as BigDecimal).toFloat()
    override fun int(key: String): Int = (source[key] as BigDecimal).toInt()
    override fun bool(key: String): Boolean = source[key] as Boolean

    override fun forEachString(block: (key: String, value: String) -> Unit) {
        source.entries.forEach { block(it.key, it.value as String) }
    }

    override fun forEachInt(block: (key: String, value: Int) -> Unit) {
        source.entries.forEach { block(it.key, (it.value  as BigDecimal).toInt()) }
    }

    override fun forEachBool(block: (key: String, value: Boolean) -> Unit) {
        source.entries.forEach { block(it.key, it.value as Boolean) }
    }

    override fun forEachFloat(block: (key: String, value: Float) -> Unit) {
        source.entries.forEach { block(it.key, (it.value  as BigDecimal).toFloat()) }
    }

    override fun set(key: String, value: Boolean) {
        source[key] = value
    }

    override fun set(key: String, value: Int) {
        source[key] = value
    }

    override fun set(key: String, value: Float) {
        source[key] = value
    }

    override fun set(key: String, value: String) {
        source[key] = value
    }

    override fun contains(key: String) = source[key] != null

    override fun forEachObject(block: IJsonObject.(key: String) -> Unit) {
        source.entries.forEach { block(JsonSimpleObject(it.value as JsonObject), it.key) }
    }

    override fun forEachArray(block: IJsonArray.(key: String) -> Unit) {
        source.entries.forEach { block(JsonSimpleArray(it.value as JsonArray), it.key) }
    }

    override fun set(key: String, value: IJsonObjectIO) {
        source[key] = JsonSimpleObject().apply { value.writeJson(this) }.source
    }

    override fun set(key: String, value: IJsonArrayIO) {
        source[key] = JsonSimpleArray().apply { value.writeJson(this) }.source
    }

    override fun setObj(key: String, childBlock: IJsonObject.() -> Unit) {
        source[key] = JsonSimpleObject().apply(childBlock).source
    }

    override fun setArray(key: String, childBlock: IJsonArray.() -> Unit) {
        source[key] = JsonSimpleArray().apply(childBlock).source
    }
}
