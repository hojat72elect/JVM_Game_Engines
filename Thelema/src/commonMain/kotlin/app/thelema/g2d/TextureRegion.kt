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

package app.thelema.g2d

import app.thelema.img.ITexture2D
import app.thelema.img.Texture2D
import kotlin.math.abs
import kotlin.math.round

/** Defines a rectangular area of a texture. The coordinate system used has its origin in the upper left corner with the x-axis
 * pointing to the right and the y axis pointing downwards.
 * @author mzechner, Nathan Sweet, zeganstyl
 */
open class TextureRegion constructor(
    texture: ITexture2D = Texture2D(),
    left: Float = 0f,
    bottom: Float = 0f,
    right: Float = 1f,
    top: Float = 1f
) {
    open var texture: ITexture2D = texture

    /** Left texture coordinate */
    var u = left
        private set

    /** Bottom texture coordinate */
    var v = bottom
        private set

    /** Right texture coordinate */
    var u2 = right
        private set

    /** Top texture coordinate */
    var v2 = top
        private set

    val regionX: Int
        get() = round(u * texture.width).toInt()

    val regionY: Int
        get() = round(v * texture.height).toInt()

    var regionWidth: Int = round(abs(right - left) * texture.width).toInt()
        private set

    var regionHeight: Int = round(abs(top - bottom) * texture.height).toInt()
        private set

    /** Sets the texture and sets the coordinates to the size of the specified texture.  */
    fun setRegion(texture: ITexture2D): TextureRegion {
        this.texture = texture
        return setRegion(0, 0, texture.width, texture.height)
    }

    /** @param width The width of the texture region. May be negative to flip the sprite when drawn.
     * @param height The height of the texture region. May be negative to flip the sprite when drawn.
     */
    fun setRegion(x: Int, y: Int, width: Int, height: Int): TextureRegion {
        val invTexWidth = 1f / texture.width
        val invTexHeight = 1f / texture.height
        setRegion(x * invTexWidth, y * invTexHeight, (x + width) * invTexWidth, (y + height) * invTexHeight)
        regionWidth = abs(width)
        regionHeight = abs(height)
        return this
    }

    open fun setRegion(u: Float, v: Float, u2: Float, v2: Float): TextureRegion {
        var uvar = u
        var vvar = v
        var u2var = u2
        var v2var = v2
        val texWidth = texture.width
        val texHeight = texture.height
        regionWidth = round(abs(u2var - uvar) * texWidth).toInt()
        regionHeight = round(abs(v2var - vvar) * texHeight).toInt()
        // For a 1x1 region, adjust UVs toward pixel center to avoid filtering artifacts on AMD GPUs when drawing very stretched.
        if (regionWidth == 1 && regionHeight == 1) {
            val adjustX = 0.25f / texWidth
            uvar += adjustX
            u2var -= adjustX
            val adjustY = 0.25f / texHeight
            vvar += adjustY
            v2var -= adjustY
        }
        this.u = uvar
        this.v = vvar
        this.u2 = u2var
        this.v2 = v2var
        return this
    }

    fun preventInterpolationOnEdge() {
        val tx = texture.texelSizeX * 0.5f
        val ty = texture.texelSizeY * 0.5f

        if (u != 0f) u += tx
        if (v != 0f) v += ty
        if (u2 != 0f) u2 -= tx
        if (v2 != 0f) v2 -= ty
    }

    /** Sets the texture and coordinates to the specified region.  */
    fun setRegion(region: TextureRegion): TextureRegion {
        texture = region.texture
        return setRegion(region.u, region.v, region.u2, region.v2)
    }

    /** Sets the texture to that of the specified region and sets the coordinates relative to the specified region.  */
    fun setRegion(region: TextureRegion, x: Int, y: Int, width: Int, height: Int): TextureRegion {
        texture = region.texture
        return setRegion(region.regionX + x, region.regionY + y, width, height)
    }

    open fun flip(x: Boolean, y: Boolean) {
        if (x) {
            val temp = u
            u = u2
            u2 = temp
        }
        if (y) {
            val temp = v
            v = v2
            v2 = temp
        }
    }

    /** Offsets the region relative to the current region. Generally the region's size should be the entire size of the texture in
     * the direction(s) it is scrolled.
     * @param xAmount The percentage to offset horizontally.
     * @param yAmount The percentage to offset vertically. This is done in texture space, so up is negative.
     */
    open fun scroll(xAmount: Float, yAmount: Float) {
        if (xAmount != 0f) {
            val width = (u2 - u) * texture.width
            u = (u + xAmount) % 1
            u2 = u + width / texture.width
        }
        if (yAmount != 0f) {
            val height = (v2 - v) * texture.height
            v = (v + yAmount) % 1
            v2 = v + height / texture.height
        }
    }

    /** Helper function to create tiles out of this TextureRegion starting from the top left corner going to the right and ending
     * at the bottom right corner. Only complete tiles will be returned so if the region's width or height are not a multiple of
     * the tile width and height not all of the region will be used. This will not work on texture regions returned form a
     * TextureAtlas that either have whitespace removed or where flipped before the region is split.
     *
     * @param tileWidth a tile's width in pixels
     * @param tileHeight a tile's height in pixels
     * @return a 2D array of TextureRegions indexed by row, column.
     */
    fun split(tileWidth: Int, tileHeight: Int): Array<Array<TextureRegion>> {
        var x = regionX
        var y = regionY
        val width = regionWidth
        val height = regionHeight
        val rows = height / tileHeight
        val cols = width / tileWidth
        val startX = x
        val tiles = Array(rows) { Array(cols) { TextureRegion(texture).apply { setRegion(x, y, tileWidth, tileHeight) } } }
        var row = 0
        while (row < rows) {
            x = startX
            var col = 0
            while (col < cols) {
                tiles[row][col].setRegion(x, y, tileWidth, tileHeight)
                col++
                x += tileWidth
            }
            row++
            y += tileHeight
        }
        return tiles
    }

    companion object {
        /** Helper function to create tiles out of the given [Texture2D] starting from the top left corner going to the right and
         * ending at the bottom right corner. Only complete tiles will be returned so if the texture's width or height are not a
         * multiple of the tile width and height not all of the texture will be used.
         *
         * @param texture the Texture
         * @param tileWidth a tile's width in pixels
         * @param tileHeight a tile's height in pixels
         * @return a 2D array of TextureRegions indexed by row, column.
         */
        fun split(texture: Texture2D, tileWidth: Int, tileHeight: Int): Array<Array<TextureRegion>> {
            val region = TextureRegion(texture)
            return region.split(tileWidth, tileHeight)
        }
    }
}
