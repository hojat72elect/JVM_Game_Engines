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
class GLSLFloat(override var name: String): ShaderData() {
    override val type: String
        get() = GLSLType.Float

    override fun asFloat(): String = fRef
    override fun asVec2(): String = "vec2($fRef)"
    override fun asVec3(): String = "vec3($fRef)"
    override fun asVec4(): String = "vec4($fRef)"
}