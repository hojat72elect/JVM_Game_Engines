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

package app.thelema.ecs

import app.thelema.fs.IFile
import app.thelema.fs.projectFile
import app.thelema.g3d.ISceneInstance
import app.thelema.g3d.ISceneProvider
import app.thelema.g3d.SceneProvider
import app.thelema.g3d.SceneProviderProxy
import app.thelema.json.IJsonObject
import app.thelema.json.JSON
import app.thelema.res.IProject
import app.thelema.res.LoaderAdapter
import app.thelema.res.RES
import app.thelema.res.load
import app.thelema.utils.LOG
import app.thelema.utils.iterate

/** Loads entity from file.
 * Loaded entity will be [targetEntity]. */
class EntityLoader: LoaderAdapter(), SceneProviderProxy {
    /** Root scene entity. You can load it with [load] */
    val targetEntity: IEntity = Entity()

    override val componentName: String
        get() = "EntityLoader"

    var saveTargetEntityOnWrite = false

    private val _sceneInstances = ArrayList<ISceneInstance>()
    override val sceneInstances: List<ISceneInstance>
        get() =_sceneInstances

    var provider: ISceneProvider = SceneProvider().also { it.proxy = this }

    override var entityOrNull: IEntity?
        get() = super.entityOrNull
        set(value) {
            super.entityOrNull = value
            provider = (value?.component() ?: SceneProvider()).also { it.proxy = this }
        }

    override fun cancelProviding(instance: ISceneInstance) {
        _sceneInstances.remove(instance)
    }

    override fun provideScene(instance: ISceneInstance) {
        if (file?.exists() == true) load()
        if (isLoaded) instance.sceneClassEntity = targetEntity
        _sceneInstances.add(instance)
    }

    override fun loadBase(file: IFile) {
        loadEntityTo(targetEntity, file)

        currentProgress = 1
        stop()

        sceneInstances.iterate { it.sceneClassEntity = targetEntity }
    }

    fun loadEntityTo(entity: IEntity, file: IFile) {
        file.readText {
            try {
                val json = JSON.parseObject(it)
                entity.readJson(json)
            } catch (ex: Exception) {
                LOG.error("EntityLoader: can't load entity \"${file.path}\"")
                ex.printStackTrace()
            }
        }
    }

    override fun getOrCreateFile(): IFile? {
        if (file == null) {
            val fileName = entity.name + ext
            file = projectFile(fileName)
        }
        return file
    }

    fun saveTargetEntity() {
        if (targetEntity.name.isEmpty()) targetEntity.name = entityOrNull?.name ?: ""
        if (targetEntity.name.isNotEmpty()) {
            val file = getOrCreateFile()
            if (file != null) {
                file.writeText(JSON.printObject(targetEntity))
            } else {
                LOG.error("$path: Can't save entity, file is null")
            }
        } else {
            LOG.error("$path: Can't save entity, entity name is empty")
        }
    }

    companion object {
        const val ext = ".entity"
    }
}

inline fun IEntity.entityLoader(block: EntityLoader.() -> Unit) = component(block)
inline fun IEntity.entityLoader() = component<EntityLoader>()
fun IProject.loadEntity(uri: String, block: EntityLoader.() -> Unit = {}): EntityLoader = load(uri, block)