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

import app.thelema.ecs.IEntity
import app.thelema.ecs.component

/** @author zeganstyl */
interface ISphereShape: IPhysicalShape {
    var radius: Float

    override val componentName: String
        get() = "SphereShape"

    fun setSize(radius: Float)
}

fun IEntity.sphereShape(block: ISphereShape.() -> Unit) = component(block)
fun IEntity.sphereShape() = component<ISphereShape>()