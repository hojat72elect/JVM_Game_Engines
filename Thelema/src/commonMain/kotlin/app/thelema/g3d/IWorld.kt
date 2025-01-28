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

package app.thelema.g3d

import app.thelema.img.ITexture
import app.thelema.img.TextureCube
import app.thelema.math.IVec3
import app.thelema.math.IVec4

/** @author zeganstyl */
interface IWorld {
    var fogColor: IVec4
    var fogHeight: Float
    var fogMul: Float

    var ambientColor: IVec3

    var exposure: Float

    var environmentPrefilterMap: TextureCube?
    var environmentPrefilterMipCount: Int
    var environmentIrradianceMap: TextureCube?
    var brdfLUTMap: ITexture?
}