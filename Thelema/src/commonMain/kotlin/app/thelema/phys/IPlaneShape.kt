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

package app.thelema.phys

import app.thelema.math.IVec3

/** @author zeganstyl */
interface IPlaneShape: IPhysicalShape {
    var depth: Float

    var normal: IVec3

    override val componentName: String
        get() = "PlaneShape"

    fun setParams(a: Double, b: Double, c: Double, d: Double)

    fun setParams(a: Float, b: Float, c: Float, d: Float) =
        setParams(a.toDouble(), b.toDouble(), c.toDouble(), d.toDouble())
}