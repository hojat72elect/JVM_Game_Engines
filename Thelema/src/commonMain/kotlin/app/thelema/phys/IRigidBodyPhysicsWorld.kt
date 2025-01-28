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
import app.thelema.ecs.IEntityComponent
import app.thelema.ecs.SimulationComponent
import app.thelema.ecs.component
import app.thelema.math.IVec3

/** @author zeganstyl */
interface IRigidBodyPhysicsWorld: IEntityComponent, SimulationComponent {
    val sourceObject: Any
        get() = this

    override val componentName: String
        get() = "RigidBodyPhysicsWorld"

    /** If not 0.0, it will be used as time delta */
    var fixedDelta: Float

    var iterations: Int

    var maxContacts: Int

    var gravity: IVec3

    var useQuickStep: Boolean

    fun step(delta: Float)

    fun addPhysicsWorldListener(listener: IPhysicsWorldListener)
    fun removePhysicsWorldListener(listener: IPhysicsWorldListener)
}

fun IEntity.rigidBodyPhysicsWorld(block: IRigidBodyPhysicsWorld.() -> Unit) = component(block)
fun IEntity.rigidBodyPhysicsWorld() = component<IRigidBodyPhysicsWorld>()