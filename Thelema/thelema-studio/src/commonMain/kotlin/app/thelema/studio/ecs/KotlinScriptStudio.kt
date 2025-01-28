package app.thelema.studio.ecs

import app.thelema.ecs.ComponentDescriptor
import app.thelema.ecs.IEntityComponent
import app.thelema.ecs.IPropertyDescriptor
import app.thelema.ecs.PropertyType
import app.thelema.fs.FileLocation
import app.thelema.fs.IFile
import app.thelema.json.IJsonObject
import app.thelema.jvm.JvmFile
import app.thelema.res.RES
import app.thelema.ecs.KotlinScript
import app.thelema.utils.iterate
import java.io.File
import kotlin.reflect.KMutableProperty1
import kotlin.script.experimental.host.StringScriptSource

class KotlinScriptStudio: KotlinScript() {
    var file: IFile? = null

    override var scriptComponentName: String
        get() = file?.nameWithoutExtension ?: ""
        set(_) {}

    private var oldFullCode = ""

    /** Get full function name */
    fun fqn(): String {
        val file = file ?: throw IllegalStateException("$path: file is null")
        val scriptPackage = file.parent().path.replace('/', '.')
        return if (scriptPackage.isNotEmpty() && scriptPackage != RES.appPackage) {
            "$scriptPackage.$scriptComponentName"
        } else {
            scriptComponentName
        }
    }

    fun getScriptData(): ExecuteScriptData {
        var text = ""
        file?.readText { text = it }

        val entityPropertyName = "entity" + entity.hashCode().toString(16)

        val firstImport = text.indexOf("import ")
        var import = emptyList<String>()

        val src: String

        if (firstImport >= 0) {
            var t = text.substring(firstImport)
            var lastImport = t.lastIndexOf("import ")
            lastImport = t.indexOf('\n', lastImport)
            src = t.substring(lastImport)
            t = t.substring(0, lastImport)
            import = t.split('\n')
        } else {
            src = text.substringAfter('\n')
        }

        return ExecuteScriptData(
            import,
            entityPropertyName,
            scriptComponentName,
            src,
            this
        )
    }

    override fun destroy() {
        oldFullCode = ""
        file = null
        super.destroy()
    }

    companion object {
        suspend fun executeCurrentScripts(scripts: List<KotlinScriptStudio>) {
            val map = HashMap<String, ExecuteScriptData>()
            val list = ArrayList<ExecuteScriptData>()

            scripts.iterate { script ->
                val data = script.getScriptData()
                list.add(data)
                script.file?.also { file ->
                    if (!map.contains(file.path)) {
                        map[file.path] = data
                    }
                }
            }

            val import = HashSet<String>()
            import.add("import app.thelema.ecs.ECS")
            map.values.forEach {
                import.addAll(it.import)
            }

            val text = """
package ${RES.appPackage}

${import.joinToString("\n")}

${map.values.joinToString("\n") { it.text }}

${
                map.values.joinToString("\n") {
                    "ECS.descriptor(\"${it.functionName}\", { ${it.functionName}() })"
                }
}

${
                list.joinToString("\n") {
                    "${it.entityName}.component(\"${it.functionName}\")"
                }
}
"""

            val code = StringScriptSource(text, "Thelema")

            val properties = HashMap<String, Any?>().apply {
                list.iterate {
                    put(it.entityName, it.script.entity)
                }
            }

            KotlinScripting.eval(properties, code, emptyList())
        }
    }
}

class ExecuteScriptData(
    val import: List<String>,
    val entityName: String,
    val functionName: String,
    var text: String,
    var script: KotlinScriptStudio
)

/** Define File-property (string) */
fun <T: IEntityComponent> ComponentDescriptor<T>.kotlinScriptFile(
    property: KMutableProperty1<T, IFile?>
) = property(object : IPropertyDescriptor<T, IFile?> {
    override val name: String = property.name
    override val type = ScriptFile
    override fun setValue(component: T, value: IFile?) = property.set(component, value)
    override fun getValue(component: T): IFile? = property.get(component)
    override fun default(): IFile? = null
    override fun readJson(component: T, json: IJsonObject) {
        KotlinScripting.kotlinDirectory?.also {
            if (json.contains("file")) {
                val path = json.string("file")
                property.set(component, scriptFile(path))
            } else {
                property.set(component, null)
            }
        }
    }
    override fun writeJson(component: T, json: IJsonObject) {
        property.get(component)?.also { json["file"] = it.path }
    }
})

fun scriptFile(path: String) =
    JvmFile(
        File(
            KotlinScripting.kotlinDirectory?.platformPath
                ?: throw IllegalStateException("Kotlin sources directory is not set"), path
        ),
        path,
        FileLocation.Relative
    )

val ScriptFile = PropertyType("thelema/ScriptFile")
