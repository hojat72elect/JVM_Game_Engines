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

package app.thelema.js.json

import app.thelema.json.IJsonArray
import app.thelema.json.IJsonArrayIO
import app.thelema.json.IJsonObject
import app.thelema.json.IJsonObjectIO
import kotlin.js.Json
import kotlin.js.json

class JsJsonObject(val source: Json = json()): IJsonObject {
    constructor(io: IJsonObjectIO): this() {
        io.writeJson(this)
    }

    override val sourceObject: Any
        get() = source

    override val size: Int
        get() = keys(source).size

    override fun printJson(): String = JSON.stringify(source)

    override fun obj(key: String): IJsonObject = JsJsonObject(source[key] as Json)
    override fun array(key: String): IJsonArray = JsJsonArray(source[key] as Array<dynamic>)

    override fun string(key: String): String = source[key] as String

    override fun float(key: String): Float = source[key] as Float

    override fun int(key: String): Int = source[key] as Int

    override fun bool(key: String): Boolean = source[key] as Boolean

    override fun forEachString(block: (key: String, value: String) -> Unit) {
        val entries = objEntries(source) as Array<dynamic>
        for (i in entries.indices) {
            val entry = entries[i] as Array<dynamic>
            block(entry[0], entry[1])
        }
    }

    override fun forEachInt(block: (key: String, value: Int) -> Unit) {
        val entries = objEntries(source) as Array<dynamic>
        for (i in entries.indices) {
            val entry = entries[i] as Array<dynamic>
            block(entry[0], entry[1])
        }
    }

    override fun forEachBool(block: (key: String, value: Boolean) -> Unit) {
        val entries = objEntries(source) as Array<dynamic>
        for (i in entries.indices) {
            val entry = entries[i] as Array<dynamic>
            block(entry[0], entry[1])
        }
    }

    override fun forEachFloat(block: (key: String, value: Float) -> Unit) {
        val entries = objEntries(source) as Array<dynamic>
        for (i in entries.indices) {
            val entry = entries[i] as Array<dynamic>
            block(entry[0], entry[1])
        }
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

    override fun setObj(key: String, childBlock: IJsonObject.() -> Unit) {
        source[key] = JsJsonObject().apply(childBlock).source
    }

    override fun contains(key: String): Boolean = source[key] != null

    override fun forEachObject(block: IJsonObject.(key: String) -> Unit) {
        val entries = objEntries(source) as Array<dynamic>
        for (i in entries.indices) {
            val entry = entries[i] as Array<dynamic>
            block(JsJsonObject(entry[1] as Json), entry[0])
        }
    }

    override fun forEachArray(block: IJsonArray.(key: String) -> Unit) {
        val entries = objEntries(source) as Array<dynamic>
        for (i in entries.indices) {
            val entry = entries[i] as Array<dynamic>
            block(JsJsonArray(entry[1] as Array<dynamic>), entry[0])
        }
    }

    override fun set(key: String, value: IJsonObjectIO) {
        source[key] = JsJsonObject(value).source
    }

    override fun set(key: String, value: IJsonArrayIO) {
        source[key] = JsJsonArray(value).toJSON()
    }

    override fun setArray(key: String, childBlock: IJsonArray.() -> Unit) {
        source[key] = JsJsonArray().apply(childBlock).toJSON()
    }

    companion object {
        val objKeys = js("Object.keys")
        val objEntries = js("Object.entries")

        fun keys(dyn: dynamic) = objKeys(dyn) as Array<String>
    }
}