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

class TextureCubeSide(val textureCube: TextureCube, target: Int): Texture2D() {
    override val componentName: String
        get() = "TextureCubeSide"

    override var textureHandle: Int
        get() = textureCube.textureHandle
        set(value) { textureCube.textureHandle = value }
    override var minFilter: Int
        get() = textureCube.minFilter
        set(value) { textureCube.minFilter = value }
    override var magFilter: Int
        get() = textureCube.magFilter
        set(value) { textureCube.magFilter = value }
    override var sWrap: Int
        get() = textureCube.sWrap
        set(value) { textureCube.sWrap = value }
    override var tWrap: Int
        get() = textureCube.tWrap
        set(value) { textureCube.tWrap = value }
    override var rWrap: Int
        get() = textureCube.rWrap
        set(value) { textureCube.rWrap = value }
    override var anisotropicFilter: Float
        get() = textureCube.anisotropicFilter
        set(value) { textureCube.anisotropicFilter = value }

    init {
        glTarget = target
    }

    override fun bind() = textureCube.bind()
    override fun bind(unit: Int) = textureCube.bind(unit)
    override fun unbind() = textureCube.unbind()
    override fun generateMipmapsGPU() = textureCube.generateMipmapsGPU()

//    override fun load(
//        image: IImage,
//        mipLevel: Int,
//        ready: ITexture2D.() -> Unit
//    ): ITexture2D {
//        this.image = image
//        this.width = image.width
//        this.height = image.height
//        if (textureHandle == 0) textureHandle = GL.glGenTexture()
//        bind()
//        GL.glTexImage2D(glTarget, mipLevel, image.internalFormat, width, height, 0, image.pixelFormat, image.pixelChannelType, image)
//        ready()
//        return this
//    }
//
//    override fun load(
//        width: Int,
//        height: Int,
//        pixels: IByteData?,
//        internalFormat: Int,
//        pixelFormat: Int,
//        pixelChannelType: Int,
//        mipmapLevel: Int
//    ): ITexture2D {
//        this.width = width
//        this.height = height
//        if (textureHandle == 0) textureHandle = GL.glGenTexture()
//        bind()
//        GL.glTexImage2D(glTarget, mipmapLevel, internalFormat, width, height, 0, pixelFormat, pixelChannelType, pixels)
//        return this
//    }
}
