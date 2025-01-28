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

package app.thelema.img

/** @author zeganstyl */
interface IFrameBufferAttachment {
    /** Texture or render buffer handle */
    val glHandle: Int

    /** Frame buffer target */
    val target: Int

    val attachment: Int

    /** Specifies the number of color components in the texture.
     * [OpenGL documentation](https://www.khronos.org/registry/OpenGL-Refpages/es3/html/glTexImage2D.xhtml) */
    val internalFormat: Int

    var texture: ITexture?

    val mipmapLevel: Int

    val isShadowMap: Boolean

    /** Setup this attachment for frame buffer */
    fun setup(frameBuffer: IFrameBuffer)

    /** Dispose GPU objects */
    fun destroy()
}