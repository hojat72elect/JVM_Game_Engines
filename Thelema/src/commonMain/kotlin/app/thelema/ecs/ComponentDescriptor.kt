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

import app.thelema.fs.FS
import app.thelema.fs.FileLocation
import app.thelema.fs.IFile
import app.thelema.json.IJsonObject
import app.thelema.math.*
import app.thelema.res.RES
import app.thelema.shader.node.GLSL
import app.thelema.shader.node.IShaderData
import app.thelema.utils.LOG
import app.thelema.utils.iterate
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

class ComponentDescriptor<T: IEntityComponent>(
    typeName: String,
    val create: () -> T,
    block: ComponentDescriptor<T>.() -> Unit = {}
): ComponentDescriptorList(typeName), IPropertyType {
    val properties = LinkedHashMap<String, IPropertyDescriptor<T, Any?>>(0)

    override val propertyTypeName: String
        get() = componentName

    init {
        block(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun setAliases(vararg aliases: String) {
        aliases.forEach {
            ECS.allDescriptors[it] = this as ComponentDescriptor<IEntityComponent>
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> setAliases(vararg aliases: KClass<T>) {
        aliases.forEach {
            ECS.allDescriptors[it.simpleName!!] = this as ComponentDescriptor<IEntityComponent>
        }
    }

    fun checkName(name: String) {
        if (name.isEmpty()) throw IllegalStateException("Property descriptor name must not be empty")
        if (properties.containsKey(name)) throw IllegalStateException("Property descriptor \"$name\" already exists")
    }

    fun getProperty(name: String): IPropertyDescriptor<T, Any?> = properties[name] ?:
    throw IllegalArgumentException("ComponentDescriptor ($componentName): property \"$name\" not found")

    fun getPropertyOrNull(name: String): IPropertyDescriptor<T, Any?>? = properties[name]

    /** Define property */
    @Suppress("UNCHECKED_CAST")
    fun property(descriptor: IPropertyDescriptor<*, *>): IPropertyDescriptor<T, Any?> {
        checkName(descriptor.name)
        properties[descriptor.name] = descriptor as IPropertyDescriptor<T, Any?>
        return descriptor
    }

    fun bool(property: KMutableProperty1<T, Boolean>, default: Boolean = false) = property(object : IPropertyDescriptor<T, Boolean> {
        override val name: String = property.name
        override val type = PropertyType.Bool
        override fun setValue(component: T, value: Boolean) = property.set(component, value)
        override fun getValue(component: T): Boolean = property.get(component)
        override fun default(): Boolean = default
        override fun readJson(component: T, json: IJsonObject) = property.set(component, json.bool(name, default()))
        override fun writeJson(component: T, json: IJsonObject) { json[name] = property.get(component) }
    })

    /** Define integer property */
    fun int(name: String, get: T.() -> Int, set: T.(value: Int) -> Unit) = property(object : IPropertyDescriptor<T, Int> {
        override val name: String = name
        override val type = PropertyType.Int
        override fun setValue(component: T, value: Int) = set(component, value)
        override fun getValue(component: T): Int = get(component)
        override fun default(): Int = 0
        override fun readJson(component: T, json: IJsonObject) = set(component, json.int(name, default()))
        override fun writeJson(component: T, json: IJsonObject) { json[name] = component.get() }
    })

    fun int(property: KMutableProperty1<T, Int>, default: Int = 0) = property(object : IPropertyDescriptor<T, Int> {
        override val name: String = property.name
        override val type = PropertyType.Int
        override fun setValue(component: T, value: Int) = property.set(component, value)
        override fun getValue(component: T): Int = property.get(component)
        override fun default(): Int = default
        override fun readJson(component: T, json: IJsonObject) = property.set(component, json.int(name, default()))
        override fun writeJson(component: T, json: IJsonObject) { json[name] = property.get(component) }
    })

    /** Define float property */
    fun float(name: String, get: T.() -> Float, set: T.(value: Float) -> Unit) = property(object : IPropertyDescriptor<T, Float> {
        override val name: String = name
        override val type = PropertyType.Float
        override fun setValue(component: T, value: Float) = set(component, value)
        override fun getValue(component: T): Float = get(component)
        override fun default(): Float = 0f
        override fun readJson(component: T, json: IJsonObject) = set(component, json.float(name, default()))
        override fun writeJson(component: T, json: IJsonObject) { json[name] = component.get() }
    })

    fun float(property: KMutableProperty1<T, Float>, default: Float = 0f) = property(object : IPropertyDescriptor<T, Float> {
        override val name: String = property.name
        override val type = PropertyType.Float
        override fun setValue(component: T, value: Float) = property.set(component, value)
        override fun getValue(component: T): Float = property.get(component)
        override fun default(): Float = default
        override fun readJson(component: T, json: IJsonObject) = property.set(component, json.float(name, default()))
        override fun writeJson(component: T, json: IJsonObject) { json[name] = property.get(component) }
    })

    fun vec2(property: KMutableProperty1<T, IVec2>) = property(object : IPropertyDescriptor<T, IVec2> {
        override val name: String = property.name
        override val type = PropertyType.Vec2
        override fun setValue(component: T, value: IVec2) = property.set(component, value)
        override fun getValue(component: T): IVec2 = property.get(component)
        override fun default(): IVec2 = MATH.Zero2
        override fun readJson(component: T, json: IJsonObject) {
            json.array(name) { property.set(component, Vec2(float(0, 0f), float(1, 0f))) }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            property.get(component).also {
                if (it.x != 0f || it.y != 0f) {
                    json.setArray(name) { add(it.x, it.y) }
                }
            }
        }
    })

    fun vec3C(property: KMutableProperty1<T, IVec3C>, default: IVec3C = MATH.Zero3) = property(object : IPropertyDescriptor<T, IVec3C> {
        override val name: String = property.name
        override val type = PropertyType.Vec3
        override fun setValue(component: T, value: IVec3C) = property.set(component, value)
        override fun getValue(component: T): IVec3C = property.get(component)
        override fun default(): IVec3C = default
        override fun readJson(component: T, json: IJsonObject) {
            json.array(name) {
                val vec = Vec3(float(0, 0f), float(1, 0f), float(2, 0f))
                property.set(component, vec)
                IEntityComponent.propertiesLinkingMap?.get(component)?.also { listeners ->
                    listeners.iterate { it.setProperty(name, vec) }
                }
            }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            property.get(component).also {
                if (it.x != 0f || it.y != 0f || it.z != 0f) {
                    json.setArray(name) { add(it.x, it.y, it.z) }
                }
            }
        }
    })

    fun vec3(property: KMutableProperty1<T, IVec3>, default: IVec3C = MATH.Zero3) = property(object : IPropertyDescriptor<T, IVec3> {
        override val name: String = property.name
        override val type = PropertyType.Vec3
        override fun setValue(component: T, value: IVec3) = property.set(component, value)
        override fun getValue(component: T): IVec3 = property.get(component)
        override fun default(): IVec3 = default as IVec3
        override fun readJson(component: T, json: IJsonObject) {
            json.array(name) { property.set(component, Vec3(float(0, 0f), float(1, 0f), float(2, 0f))) }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            property.get(component).also {
                if (it.x != 0f || it.y != 0f || it.z != 0f) {
                    json.setArray(name) { add(it.x, it.y, it.z) }
                }
            }
        }
    })

    fun vec4(property: KMutableProperty1<T, IVec4>) = property(object : IPropertyDescriptor<T, IVec4> {
        override val name: String = property.name
        override val type = PropertyType.Vec4
        override fun setValue(component: T, value: IVec4) = property.set(component, value)
        override fun getValue(component: T): IVec4 = property.get(component)
        override fun default(): IVec4 = MATH.Zero3One1
        override fun readJson(component: T, json: IJsonObject) {
            json.array(name) { property.set(component, Vec4(float(0, 0f), float(1, 0f), float(2, 0f), float(3, 1f))) }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            property.get(component).also {
                if (it.x != 0f || it.y != 0f || it.z != 0f || it.w != 1f) {
                    json.setArray(name) { add(it.x, it.y, it.z, it.w) }
                }
            }
        }
    })

    fun quaternion(property: KMutableProperty1<T, IVec4C>) = property(object : IPropertyDescriptor<T, IVec4C> {
        override val name: String = property.name
        override val type = PropertyType.Quaternion
        override fun setValue(component: T, value: IVec4C) = property.set(component, value)
        override fun getValue(component: T): IVec4C = property.get(component)
        override fun default(): IVec4C = MATH.Zero3One1
        override fun readJson(component: T, json: IJsonObject) {
            json.array(name) { property.set(component, Vec4(float(0, 0f), float(1, 0f), float(2, 0f), float(3, 1f))) }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            property.get(component).also {
                if (it.x != 0f || it.y != 0f || it.z != 0f || it.w != 1f) {
                    json.setArray(name) { add(it.x, it.y, it.z, it.w) }
                }
            }
        }
    })

    /** Define 4x4 matrix property */
    fun mat4(name: String, get: T.() -> IMat4, set: T.(value: IMat4) -> Unit) = property(object : IPropertyDescriptor<T, IMat4> {
        override val name: String = name
        override val type = PropertyType.Mat4
        override val useJsonReadWrite: Boolean = false
        override fun setValue(component: T, value: IMat4) = set(component, value)
        override fun getValue(component: T): IMat4 = get(component)
        override fun default(): IMat4 = MATH.IdentityMat4
        override fun readJson(component: T, json: IJsonObject) {}
        override fun writeJson(component: T, json: IJsonObject) {}
    })

    fun mat3C(property: KMutableProperty1<T, IMat3C>) = property(object : IPropertyDescriptor<T, IMat3C> {
        override val name: String = property.name
        override val type = PropertyType.Mat3
        override val useJsonReadWrite: Boolean = false
        override fun setValue(component: T, value: IMat3C) = property.set(component, value)
        override fun getValue(component: T): IMat3C = property.get(component)
        override fun default(): IMat3C = MATH.IdentityMat3
        override fun readJson(component: T, json: IJsonObject) {
            json.array(name) {
                if (size == 9) {
                    val mat = Mat3()
                    for (i in 0 until 9) {
                        mat.values[i] = float(0, mat.values[i])
                    }
                    property.set(component, mat)
                } else {
                    property.set(component, default())
                }
            }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            property.get(component).also {
                json.setArray(name) {
                    for (i in 0 until 9) {
                        add(it.values[i])
                    }
                }
            }
        }
    })

    /** Define string-property */
    fun string(name: String, get: T.() -> String, set: T.(value: String) -> Unit) = property(object : IPropertyDescriptor<T, String> {
        override val name: String = name
        override val type = PropertyType.String
        override fun setValue(component: T, value: String) = set(component, value)
        override fun getValue(component: T): String = get(component)
        override fun default(): String = ""
        override fun readJson(component: T, json: IJsonObject) = set(component, json.string(name, default()))
        override fun writeJson(component: T, json: IJsonObject) { json[name] = component.get() }
    })

    fun string(property: KMutableProperty1<T, String>, default: String = "") = property(object : IPropertyDescriptor<T, String> {
        override val name: String = property.name
        override val type = PropertyType.String
        override fun setValue(component: T, value: String) = property.set(component, value)
        override fun getValue(component: T): String = property.get(component)
        override fun default(): String = default
        override fun readJson(component: T, json: IJsonObject) = property.set(component, json.string(name, default()))
        override fun writeJson(component: T, json: IJsonObject) { json[name] = property.get(component) }
    })

    fun stringEnum(property: KMutableProperty1<T, String>, values: List<String>) =
        property(StringEnumPropertyDesc2(property, values))

    fun intEnum(property: KMutableProperty1<T, Int>, values: Map<Int, String>, defaultValue: String) =
        property(IntEnumPropertyDesc2(property, values, defaultValue))

    fun intEnum(property: KMutableProperty1<T, Int>, defaultValue: String, vararg values: Pair<Int, String>) =
        property(IntEnumPropertyDesc2(property, linkedMapOf(*values), defaultValue))

    fun intEnum(property: KMutableProperty1<T, Int>, vararg values: Pair<Int, String>) =
        property(IntEnumPropertyDesc2(property, linkedMapOf(*values), "???"))

    /** Define File-property (string) */
    fun file(property: KMutableProperty1<T, IFile?>) = property(object : IPropertyDescriptor<T, IFile?> {
        override val name: String = property.name
        override val type = PropertyType.File
        override fun setValue(component: T, value: IFile?) = property.set(component, value)
        override fun getValue(component: T): IFile? = property.get(component)
        override fun default(): IFile? = null
        override fun readJson(component: T, json: IJsonObject) {
            property.set(component, FS.file(json.string(name, ""), json.string("${name}Location", FileLocation.Project)))
        }
        override fun writeJson(component: T, json: IJsonObject) {
            property.get(component)?.also { file ->
                json[name] = file.path
                if (file.location != FileLocation.Project) json["${name}Location"] = file.location
            }
        }
    })

    /** Define relative reference-property to component
     * @param name property name
     * @param requiredComponent component name
     * @param get get component reference from this component's property
     * @param set set component reference to this component's property */
    fun <V: IEntityComponent> ref(name: String, requiredComponent: String, get: T.() -> V?, set: T.(value: V?) -> Unit) = property(object : IPropertyDescriptor<T, V?> {
        override val name: String = name
        override val type = ComponentRefType(requiredComponent)
        override fun setValue(component: T, value: V?) = set(component, value)
        override fun getValue(component: T): V? = get(component)
        override fun default(): V? = null
        @Suppress("UNCHECKED_CAST")
        override fun copy(component: T, other: T) {
            val otherRef = get(other) as IEntityComponent?
            if (otherRef != null) {
                val path = other.entity.getRelativePathTo(otherRef.entity)
                val ref = component.entity.getEntityByPath(path)?.componentOrNull(requiredComponent)
                if (ref != null) {
                    set(component, ref as V)
                } else {
                    LOG.error("${component.path}: can't link component reference $name to path: $path")
                }
            } else {
                set(component, null)
            }
        }
        @Suppress("UNCHECKED_CAST")
        override fun readJson(component: T, json: IJsonObject) {
            val path = json.string(name, "")
            val entity = component.entityOrNull
            if (path.isNotEmpty() && entity != null) {
                set(component, entity.getRootEntity().makePath(path).componentTyped<V>(requiredComponent))
            }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            val path = get(component)?.entityOrNull?.path ?: ""
            if (path.isNotEmpty()) json[name] = path
        }
    })

    fun <V: IEntityComponent> ref(property: KMutableProperty1<T, V?>, requiredComponent: String) = property(object : IPropertyDescriptor<T, V?> {
        override val name: String = property.name
        override val type = ComponentRefType(requiredComponent)
        override fun setValue(component: T, value: V?) = property.set(component, value)
        override fun getValue(component: T): V? = property.get(component)
        override fun default(): V? = null
        @Suppress("UNCHECKED_CAST")
        override fun copy(component: T, other: T) {
            val otherRefEntity = (property.get(other) as IEntityComponent?)?.entityOrNull
            if (otherRefEntity != null) {
                val path = other.entityOrNull?.getRelativePathTo(otherRefEntity)
                if (path != null) {
                    val ref = component.entityOrNull?.getEntityByPath(path)?.componentOrNull(requiredComponent)
                    if (ref != null) {
                        property.set(component, ref as V)
                    } else {
                        LOG.error("${component.path}: can't link component reference $name to path: $path")
                    }
                } else {
                    LOG.error("${component.path}: can't find relative path ${otherRefEntity.path}")
                }
            } else {
                property.set(component, null)
            }
        }
        @Suppress("UNCHECKED_CAST")
        override fun readJson(component: T, json: IJsonObject) {
            val path = json.string(name, "")
            val entity = component.entityOrNull
            if (path.isNotEmpty() && entity != null) {
                property.set(component, entity.getRootEntity().makePath(path).componentTyped<V>(requiredComponent))
            }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            val path = property.get(component)?.entityOrNull?.path ?: ""
            if (path.isNotEmpty()) json[name] = path
        }
    })

    fun <V: IEntityComponent> refAbs(property: KMutableProperty1<T, V?>, requiredComponent: String) = property(object : IPropertyDescriptor<T, V?> {
        override val name: String = property.name
        override val type = ComponentRefType(requiredComponent)
        override fun setValue(component: T, value: V?) = property.set(component, value)
        override fun getValue(component: T): V? = property.get(component)
        override fun default(): V? = null
        override fun readJson(component: T, json: IJsonObject) {
            val path = json.string(name, "")
            val entity = component.entityOrNull

            if (path.isNotEmpty() && entity != null) {
                property.set(component, RES.entity.makePath(path).componentTyped<V>(requiredComponent))
            }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            val path = (property.get(component) as IEntityComponent?)?.entityOrNull?.path ?: ""
            if (path.isNotEmpty()) json[name] = path
        }
    })

    /** Define reference-property to component
     * @param name property name
     * @param get get component reference from this component's property
     * @param set set component reference to this component's property */
    inline fun <reified V: IEntityComponent> ref(name: String, noinline get: T.() -> V?, noinline set: T.(value: V?) -> Unit) {
        ref(name, V::class.simpleName!!, get, set)
    }

    inline fun <reified V: IEntityComponent> ref(property: KMutableProperty1<T, V?>) {
        ref(property, V::class.simpleName!!)
    }

    inline fun <reified V: IEntityComponent> refAbs(property: KMutableProperty1<T, V?>) {
        refAbs(property, V::class.simpleName!!)
    }

    fun shaderNodeInput(property: KMutableProperty1<T, IShaderData>) = property(object : IPropertyDescriptor<T, IShaderData> {
        override val name: String = property.name
        override val type = PropertyType.ShaderNodeInput
        override fun setValue(component: T, value: IShaderData) = property.set(component, value)
        override fun getValue(component: T): IShaderData = property.get(component)
        override fun default(): IShaderData = GLSL.zeroFloat
        override fun readJson(component: T, json: IJsonObject) {
            val path = json.string(name, "")
            val entity = component.entityOrNull
            if (path.isNotEmpty() && entity != null) {
                val value = RES.entity.makePathToProperty(path)
                if (value != null) property.set(component, value as IShaderData)
            }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            val data = property.get(component)
            val node = data.container ?: return
            if (node.entityOrNull != null) json[name] = node.getPropertyPath(data.name)
        }
    })

    fun shaderNodeInputOrNull(property: KMutableProperty1<T, IShaderData?>) = property(object : IPropertyDescriptor<T, IShaderData?> {
        override val name: String = property.name
        override val type = PropertyType.ShaderNodeInput
        override fun setValue(component: T, value: IShaderData?) = property.set(component, value)
        override fun getValue(component: T): IShaderData? = property.get(component)
        override fun default(): IShaderData? = null
        override fun readJson(component: T, json: IJsonObject) {
            val path = json.string(name, "")
            val entity = component.entityOrNull
            if (path.isNotEmpty() && entity != null) {
                property.set(component, RES.entity.makePathToPropertyTyped(path))
            }
        }
        override fun writeJson(component: T, json: IJsonObject) {
            val data = property.get(component)
            val node = data?.container ?: return
            json[name] = node.getPropertyPath(data.name)
        }
    })

    fun shaderNodeOutput(property: KProperty1<T, IShaderData>) = property(object : IPropertyDescriptor<T, IShaderData> {
        override val name: String = property.name
        override val type = PropertyType.ShaderNodeOutput
        override fun setValue(component: T, value: IShaderData) {}
        override fun getValue(component: T): IShaderData = property.get(component)
        override fun default(): IShaderData = GLSL.zeroFloat
        override fun readJson(component: T, json: IJsonObject) {}
        override fun writeJson(component: T, json: IJsonObject) {}
    })
}