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

package app.thelema.action

import app.thelema.ecs.*

abstract class ActionAdapter: IAction {
    override var isRunning: Boolean
        get() = actionData.isRunning
        set(value) {
            actionData.isRunning = value
        }

    override var entityOrNull: IEntity? = null
        set(value) {
            field = value
            value?.componentOrNull<MainLoop>()?.also {
                it.onUpdate { update(it) }
            }
        }

    override var actionData: ActionData = ActionData()

    override fun addedSiblingComponent(component: IEntityComponent) {
        if (component is MainLoop) {
            component.onUpdate { update(it) }
        }
    }

    inline fun <reified T: IAction> action(entityName: String? = null, block: T.() -> Unit): T {
        return action<T>(T::class.simpleName!!, entityName) {}.apply(block)
    }

    inline fun <reified T: IAction> action(block: T.() -> Unit): T {
        return action<T>(T::class.simpleName!!).apply(block)
    }

    inline fun <reified T: IAction> action(): T = action(T::class.simpleName!!)

    inline fun <reified T: IEntityComponent> getContextComponent(): T? =
        getContext()?.componentOrNull()


    override fun restart() {
        isRunning = true
    }

    override fun resume() {
        isRunning = true
    }
}
