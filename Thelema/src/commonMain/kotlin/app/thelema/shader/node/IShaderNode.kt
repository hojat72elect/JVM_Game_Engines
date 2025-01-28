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

import app.thelema.ecs.IEntityComponent
import app.thelema.g3d.IUniformArgs
import app.thelema.shader.IShader

/** @author zeganstyl */
interface IShaderNode: IEntityComponent {
    var shaderOrNull: IShader?

    val shader: IShader
        get() = shaderOrNull!!

    val inputs: List<IShaderNodeInput<IShaderData?>>

    fun forEachOutput(block: (output: IShaderData) -> Unit)

    /** Before code will be generated */
    fun prepareToBuild()

    /** Here you can get uniform locations, set sampler handles and etc */
    fun shaderCompiled()

    /** Set some uniforms, like transformations, lights, textures and etc */
    fun bind(uniforms: IUniformArgs)

    /** Vertex shader code, that will be executed in main */
    fun executionVert(out: StringBuilder)

    /** Fragment shader code, that will be executed in main */
    fun executionFrag(out: StringBuilder)

    /** Vertex shader code, that will be in global section */
    fun declarationVert(out: StringBuilder)

    /** Fragment shader code, that will be in global section */
    fun declarationFrag(out: StringBuilder)
}