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

package app.thelema.test.action

import app.thelema.action.*
import app.thelema.app.APP
import app.thelema.ecs.ECS
import app.thelema.ecs.Entity
import app.thelema.ecs.component
import app.thelema.g3d.cam.ActiveCamera
import app.thelema.g3d.mesh.BoxMesh
import app.thelema.g3d.TransformNode
import app.thelema.g3d.material
import app.thelema.g3d.mesh.boxMesh
import app.thelema.g3d.scene
import app.thelema.math.MATH
import app.thelema.math.Vec3
import app.thelema.phys.PhysicsProperties
import app.thelema.shader.SimpleShader3D
import app.thelema.test.Test

class SwitchActionTest: Test {
    override val name: String
        get() = "Switch action"

    override fun testMain() {
        ActiveCamera {
            lookAt(Vec3(0f, 3f, -3f), MATH.Zero3)
            updateCamera()
        }

        Entity {
            makeCurrent()
            scene()

            val box = entity("box") {
                material { shader = SimpleShader3D() }
                boxMesh { setSize(2f) }
                component<PhysicsProperties> {
                    linearVelocity = 1f
                    angularVelocity = 1f
                }
            }

            component<ActionList> {
                customContext = box

                action<SwitchAction> {
                    addBranch<VariableAction, MoveForwardAction>({ returnValue.setBool(false) }) {
                        length = 2f
                    }
                    addBranch<VariableAction, RotateYAction>({ returnValue.setBool(true) }) {
                        angleLength = -0.3f
                    }
                    elseBranch<ActionList> {
                        action<RotateYAction> {
                            angleLength = 1f
                        }
                        action<RotateYAction> {
                            angleLength = -1f
                        }
                    }
                }
                action<MoveForwardAction> {
                    length = 4f
                }
                restart()
            }
        }
    }
}