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

package app.thelema.jvm.ode

import app.thelema.phys.ICylinderShape
import org.ode4j.ode.DCylinder
import org.ode4j.ode.DMass
import org.ode4j.ode.OdeHelper

/** @author zeganstyl */
class CylinderShape: OdeShapeAdapter<DCylinder>(), ICylinderShape {
    override var radius: Float = 1f
        set(value) {
            field = value
            geom?.setParams(radius.toDouble(), length.toDouble())
        }

    override var length: Float = 1f
        set(value) {
            field = value
            geom?.setParams(radius.toDouble(), length.toDouble())
        }

    override fun createGeom(): DCylinder =
        OdeHelper.createCylinder(getSpace(), radius.toDouble(), length.toDouble())

    override fun setupMass(density: Double, mass: DMass) {
        mass.setCylinder(density, 2, radius.toDouble(), length.toDouble())
    }
}