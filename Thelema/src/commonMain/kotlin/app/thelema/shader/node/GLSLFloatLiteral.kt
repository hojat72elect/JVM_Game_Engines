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

package app.thelema.shader.node

/** @author zeganstyl */
class GLSLFloatLiteral(var value: Float = 0f): GLSLLiteralBase() {
    override var inlineCode: String
        get() = asFloat()
        set(_) {}

    override val type: String
        get() = GLSLType.Float

    override val componentName: String
        get() = "GLSLFloatLiteral"

    override fun asFloat(): String = str(value)
    override fun asVec2(): String {
        val v = str(value)
        return "vec2($v, $v)"
    }
    override fun asVec3(): String {
        val v = str(value)
        return "vec3($v, $v, $v)"
    }
    override fun asVec4(): String {
        val v = str(value)
        return "vec4($v, $v, $v, $v)"
    }
}