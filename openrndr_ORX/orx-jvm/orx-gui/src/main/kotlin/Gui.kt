package org.openrndr.extra.gui

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.github.oshai.kotlinlogging.KotlinLogging
import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.*
import org.openrndr.draw.Drawer
import org.openrndr.extra.noise.uniform
import org.openrndr.extra.parameters.*
import org.openrndr.internal.Driver
import org.openrndr.math.*
import org.openrndr.panel.ControlManager
import org.openrndr.panel.controlManager
import org.openrndr.panel.elements.*
import org.openrndr.panel.style.*
import org.openrndr.panel.style.Display
import java.io.File
import kotlin.math.roundToInt
import kotlin.reflect.KMutableProperty1

/** Dear contributor, just in case you are here looking to add a new parameter type.
There is a 6-step incantation to add a new parameter type
0) Add your parameter type to orx-parameters, follow the instructions provided there.

1) Set up a control style, very likely analogous to the styles already in place.
2) Add control creation code.
3) Add value serialization code, may need to update ParameterValue too.
4) Add value deserialization code.
5) Add value randomization code.
6) Add control update code.

You can use your editor's search functionality to jump to "1)", "2)".
 */
private data class LabeledObject(val label: String, val obj: Any)

private class CompartmentState(var collapsed: Boolean, val parameterValues: MutableMap<String, Any> = mutableMapOf())
private class SidebarState(var hidden: Boolean = false, var collapsed: Boolean = false, var scrollTop: Double = 0.0)
private class TrackedObjectBinding(
    val parameters: List<Parameter>,
    val parameterControls: MutableMap<Parameter, Element> = mutableMapOf()
)

private val persistentCompartmentStates = mutableMapOf<Long, MutableMap<String, CompartmentState>>()
private val persistentSidebarStates = mutableMapOf<Long, SidebarState>()

private fun compartmentState(): MutableMap<String, CompartmentState> = persistentCompartmentStates.getOrPut(Driver.instance.contextID) {
    mutableMapOf()
}

private fun sidebarState(): SidebarState = persistentSidebarStates.getOrPut(Driver.instance.contextID) {
    SidebarState()
}

private fun <T : Any> getPersistedOrDefault(
    compartmentLabel: String,
    property: KMutableProperty1<Any, T>,
    obj: Any
): T {
    val state = compartmentState()[compartmentLabel]
    if (state == null) {
        return property.get(obj)
    } else {
        @Suppress("UNCHECKED_CAST")
        return (state.parameterValues[property.name] as? T?) ?: return property.get(obj)
    }
}

private fun <T : Any> setAndPersist(compartmentLabel: String, property: KMutableProperty1<Any, T>, obj: Any, value: T) {
    property.set(obj, value)
    val state = compartmentState()[compartmentLabel] ?: error("item '$compartmentLabel' not in state (${compartmentState()}. ContextID ${Driver.instance.contextID} )")
    state.parameterValues[property.name] = value
}

private val logger = KotlinLogging.logger { }


class GUIAppearance(
    val baseColor: ColorRGBa = ColorRGBa.GRAY.opacify(0.99),
    val barWidth: Int = 200
)

@Suppress("unused", "UNCHECKED_CAST")
open class GUI(
    val appearance: GUIAppearance = GUIAppearance(),
    val defaultStyles: List<StyleSheet> = defaultStyles(),
) : Extension {
    private var onChangeListener: ((name: String, value: Any?) -> Unit)? = null
    override var enabled = true

    var listenToProduceAssetsEvent = true

    var visible = true
        set(value) {
            if (field != value) {
                field = value
                if (field) {
                    panel?.body?.classes?.remove(collapsed)
                } else {
                    panel?.body?.classes?.add(collapsed)
                }
                sidebarState().hidden = !field
            }
        }

    var compartmentsCollapsedByDefault = true
    var doubleBind = true
    var defaultSaveFolder = "gui-parameters"
    var persistState = true
    var enableSideCanvas = false
    var showToolbar = true

    var canvas: Canvas? = null
    private var panel: ControlManager? = null

    // Randomize button
    private var shiftDown = false
    private var randomizeButton: Button? = null

    fun onChange(listener: (name: String, value: Any?) -> Unit) {
        onChangeListener = listener
    }

    val collapsed = ElementClass("collapsed")

    override fun setup(program: Program) {
        if (persistState) {
            val guiState = File(defaultSaveFolder, "${program.name}-latest.json")
            if (guiState.exists()) {
                loadParameters(guiState)
            }
        }

        program.produceAssets.listen {
            if (listenToProduceAssetsEvent) {
                val folderFile = File(defaultSaveFolder)
                val targetFile = File(defaultSaveFolder, "${it.assetMetadata.assetBaseName}.json")
                if (folderFile.exists() && folderFile.isDirectory) {
                    logger.info { "Saving parameters to '${targetFile.absolutePath}" }
                    saveParameters(targetFile)
                } else {
                    if (folderFile.mkdirs()) {
                        logger.info { "Saving parameters to '${targetFile.absolutePath}" }
                        saveParameters(targetFile)
                    } else {
                        logger.error { "Could not save parameters because could not create directory ${folderFile.absolutePath}" }
                    }
                }
            }
        }

        program.keyboard.keyDown.listen {
            if (it.key == KEY_F11) {
                visible = !visible
            }

            if (it.key == KEY_LEFT_SHIFT) {
                shiftDown = true
                randomizeButton?.classes?.add(ElementClass("randomize-strong"))
            }
        }

        program.keyboard.keyUp.listen {
            if (it.key == KEY_LEFT_SHIFT) {
                shiftDown = false
                randomizeButton?.classes?.remove(ElementClass("randomize-strong"))
            }
        }

        panel = program.controlManager(defaultStyles = defaultStyles) {
            styleSheet(has class_ "fullscreen") {
                this.width = 100.percent
                this.height = 100.percent
                this.flexDirection = FlexDirection.Row
                this.display = Display.FLEX
            }
            styleSheet(has class_ "full-canvas") {
                this.background = Color.RGBa(ColorRGBa.RED)

                this.flexShrink = FlexGrow.Ratio(1.0)
                this.flexGrow = FlexGrow.Ratio(1.0)
                this.height = 100.percent
                this.width = 100.px
            }

            styleSheet(has class_ "container") {
                this.display = Display.FLEX
                this.flexDirection = FlexDirection.Column
                this.width = appearance.barWidth.px
                this.height = 100.percent
            }

            styleSheet(has class_ "collapse-border") {
                this.display = Display.FLEX
                this.flexDirection = FlexDirection.Column
                this.height = 5.px
                this.width = 100.percent
                this.background = Color.RGBa(appearance.baseColor.shade(0.9))

                and(has state "hover") {
                    this.background = Color.RGBa(appearance.baseColor.shade(1.1))
                }
            }

            styleSheet(has class_ "toolbar") {
                this.height = 42.px
                this.width = 100.percent
                this.display = Display.FLEX
                this.flexDirection = FlexDirection.Row
                this.background = Color.RGBa(appearance.baseColor)
            }

            styleSheet(has class_ "collapsed") {
                this.display = Display.NONE
            }

            styleSheet(has class_ "compartment") {
                this.paddingBottom = 20.px
            }

            styleSheet(has class_ "sidebar") {
                this.width = appearance.barWidth.px
                this.paddingBottom = 20.px
                this.paddingTop = 10.px
                this.paddingLeft = 10.px
                this.paddingRight = 10.px
                this.marginRight = 2.px
                this.height = 100.percent
                this.background = Color.RGBa(appearance.baseColor)
                this.overflow = Overflow.Scroll

                //<editor-fold desc="1) setup control style">
                descendant(has type "colorpicker-button") {
                    this.width = (appearance.barWidth - 25).px
                }

                descendant(has type "slider") {
                    this.width = (appearance.barWidth - 25).px
                }

                descendant(has type "button") {
                    this.width = (appearance.barWidth - 25).px
                }

                descendant(has type "textfield") {
                    this.width = (appearance.barWidth - 25).px
                }

                descendant(has type "toggle") {
                    this.width = (appearance.barWidth - 25).px
                }

                descendant(has type "xy-pad") {
                    this.width = (appearance.barWidth - 25).px
                    this.height = (appearance.barWidth - 25).px
                }

                descendant(
                    has type listOf(
                        "sequence-editor",
                        "sliders-vector2",
                        "sliders-vector3",
                        "sliders-vector4"
                    )
                ) {
                    this.width = (appearance.barWidth - 25).px
                    this.height = 100.px
                }
                //</editor-fold>
            }

            styleSheet(has class_ "randomize-strong") {
                color = Color.RGBa(ColorRGBa.PINK)

                and(has state "hover") {
                    color = Color.RGBa(ColorRGBa.BLACK)
                    background = Color.RGBa(ColorRGBa.PINK)
                }
            }

            styleSheet(has type "dropdown-button") {
                this.width = 175.px
            }
            layout {
                div("fullscreen") {
                    div("container") {
                        id = "container"
                        if (showToolbar) {
                            @Suppress("UNUSED_VARIABLE")
                            val header = div("toolbar") {
                                randomizeButton = button {
                                    label = "Randomize"
                                    clicked {
                                        randomize(strength = if (shiftDown) .75 else .05)
                                    }
                                }
                                button {
                                    label = "Load"
                                    clicked {
                                        openFileDialog(
                                            supportedExtensions = listOf("GUI parameters" to listOf("json")),
                                            contextID = "gui.parameters"
                                        ) {
                                            loadParameters(it)
                                        }
                                    }
                                }
                                button {
                                    label = "Save"
                                    clicked {
                                        val defaultPath = getDefaultPathForContext(contextID = "gui.parameters")

                                        if (defaultPath == null) {
                                            val local = File(".")
                                            val parameters = File(local, defaultSaveFolder)
                                            if (parameters.exists() && parameters.isDirectory) {
                                                setDefaultPathForContext(
                                                    contextID = "gui.parameters",
                                                    file = parameters
                                                )
                                            } else {
                                                if (parameters.mkdirs()) {
                                                    setDefaultPathForContext(
                                                        contextID = "gui.parameters",
                                                        file = parameters
                                                    )
                                                } else {
                                                    logger.warn { "Could not create directory ${parameters.absolutePath}" }
                                                }
                                            }
                                        }

                                        saveFileDialog(
                                            suggestedFilename = "parameters.json",
                                            contextID = "gui.parameters",
                                            supportedExtensions = listOf("GUI parameters" to listOf("json"))
                                        ) {
                                            saveParameters(it)
                                        }
                                    }
                                }
                            }
                        }
                        val collapseBorder = div("collapse-border") {

                        }

                        val collapsibles = mutableSetOf<Div>()
                        val sidebar = div("sidebar") {
                            id = "sidebar"
                            scrollTop = sidebarState().scrollTop
                            for ((labeledObject, binding) in trackedObjects) {
                                val (label, _) = labeledObject

                                val h3Header = h3 { label }
                                val collapsible = div("compartment") {
                                    for (parameter in binding.parameters) {
                                        val element = addControl(labeledObject, parameter)
                                        binding.parameterControls[parameter] = element
                                    }
                                }
                                collapsibles.add(collapsible)
                                val collapseClass = ElementClass("collapsed")

                                /* this is guaranteed to be in the dictionary after insertion through add() */
                                val collapseState = compartmentState()[label]!!
                                if (collapseState.collapsed) {
                                    collapsible.classes.add(collapseClass)
                                }

                                h3Header.mouse.pressed.listen {
                                    it.cancelPropagation()
                                }
                                h3Header.mouse.clicked.listen { me ->

                                    if (KeyModifier.CTRL in me.modifiers) {
                                        collapsible.classes.remove(collapseClass)
                                        compartmentState().forEach {
                                            it.value.collapsed = true
                                        }
                                        collapseState.collapsed = false

                                        (collapsibles - collapsible).forEach {
                                            it.classes.add(collapseClass)
                                        }
                                    } else {

                                        if (collapseClass in collapsible.classes) {
                                            collapsible.classes.remove(collapseClass)
                                            collapseState.collapsed = false
                                        } else {
                                            collapsible.classes.add(collapseClass)
                                            collapseState.collapsed = true
                                        }
                                    }
                                }
                            }
                        }
                        collapseBorder.mouse.pressed.listen {
                            it.cancelPropagation()
                        }

                        collapseBorder.mouse.clicked.listen {
                            val collapsed = ElementClass("collapsed")
                            if (collapsed in sidebar.classes) {
                                sidebar.classes.remove(collapsed)
                                sidebarState().collapsed = false
                            } else {
                                sidebar.classes.add(collapsed)
                                sidebarState().collapsed = true
                            }
                            it.cancelPropagation()
                        }
                        sidebar.mouse.scrolled.listen {
                            sidebarState().scrollTop = sidebar.scrollTop
                        }
                        if (sidebarState().collapsed) {
                            sidebar.classes.add(ElementClass("collapsed"))
                        }
                        sidebar.scrollTop = sidebarState().scrollTop
                    }
                    if (enableSideCanvas) {
                        canvas = canvas("full-canvas") {
                        }
                    }
                }
            }
        }

        visible = !sidebarState().hidden

        program.extend(panel ?: error("no panel"))
    }

    /* 2) control creation. create control, set label, set range, setup event-handler, load values */
    //<editor-fold desc="2) Control creation">
    private fun Div.addControl(compartment: LabeledObject, parameter: Parameter): Element {
        val obj = compartment.obj

        return when (parameter.parameterType) {

            ParameterType.Int -> {
                slider {
                    label = parameter.label
                    range = Range(parameter.intRange!!.first.toDouble(), parameter.intRange!!.last.toDouble())
                    precision = 0
                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Int>,
                            obj,
                            it.newValue.toInt()
                        )
                        (parameter.property as KMutableProperty1<Any, Int>).set(obj, value.toInt())
                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, Int>,
                        obj
                    ).let {
                        value = it.toDouble()
                        setAndPersist(compartment.label, parameter.property as KMutableProperty1<Any, Int>, obj, it)
                    }
                }
            }

            ParameterType.Double -> {
                slider {
                    label = parameter.label
                    range = Range(parameter.doubleRange!!.start, parameter.doubleRange!!.endInclusive)
                    precision = parameter.precision!!
                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Double>,
                            obj,
                            it.newValue
                        )
                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, Double>,
                        obj
                    ).let {
                        value = it
                        /*  this is generally not needed, but when the persisted value is equal to the slider default
                            it will not emit the newly set value */
                        setAndPersist(compartment.label, parameter.property as KMutableProperty1<Any, Double>, obj, it)
                    }
                }
            }

            ParameterType.Action -> {
                button {
                    label = parameter.label
                    events.clicked.listen {
                        /* the `obj` we pass in here is the receiver */
                        parameter.function!!.call(obj)
                        onChangeListener?.invoke(parameter.function!!.name, null)
                    }
                }
            }

            ParameterType.Boolean -> {
                toggle {
                    label = parameter.label
                    events.valueChanged.listen {
                        value = it.newValue
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Boolean>,
                            obj,
                            it.newValue
                        )
                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, Boolean>,
                        obj
                    ).let {
                        value = it
                        setAndPersist(compartment.label, parameter.property as KMutableProperty1<Any, Boolean>, obj, it)
                    }
                }
            }

            ParameterType.Text -> {
                textfield {
                    label = parameter.label
                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, String>,
                            obj,
                            it.newValue
                        )
                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, String>,
                        obj
                    ).let {
                        value = it
                    }
                }
            }

            ParameterType.Color -> {
                colorpickerButton {
                    label = parameter.label
                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, ColorRGBa>,
                            obj,
                            it.color
                        )
                        onChangeListener?.invoke(parameter.property!!.name, it.color)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, ColorRGBa>,
                        obj
                    ).let {
                        color = it
                    }
                }
            }

            ParameterType.XY -> {
                xyPad {
                    minX = parameter.vectorRange!!.first.x
                    minY = parameter.vectorRange!!.first.y
                    maxX = parameter.vectorRange!!.second.x
                    maxY = parameter.vectorRange!!.second.y
                    precision = parameter.precision!!
                    showVector = parameter.showVector!!
                    invertY = parameter.invertY!!
                    label = parameter.label

                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Vector2>,
                            obj,
                            it.newValue
                        )
                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                }
            }

            ParameterType.Path -> {
                button {
                    label = "Load ${parameter.label}"
                    clicked {

                        if (parameter.pathIsDirectory == false) {
                            openFileDialog(
                                supportedExtensions = parameter.pathExtensions?.let { listOf("supported extensions" to it.toList()) }
                                    ?: emptyList(),
                                contextID = parameter.pathContext ?: "null"
                            ) {
                                val resolvedPath = if (parameter.absolutePath == true) {
                                    it.absolutePath
                                } else {
                                    it.relativeTo(File(".").absoluteFile).path
                                }
                                setAndPersist(
                                    compartment.label,
                                    parameter.property as KMutableProperty1<Any, String>,
                                    obj,
                                    resolvedPath
                                )
                            }
                        } else {
                            openFolderDialog(contextID = parameter.pathContext ?: "null") {
                                val resolvedPath = if (parameter.absolutePath == true) {
                                    it.absolutePath
                                } else {
                                    it.relativeTo(File(".").absoluteFile).path
                                }
                                setAndPersist(
                                    compartment.label,
                                    parameter.property as KMutableProperty1<Any, String>,
                                    obj,
                                    resolvedPath
                                )
                            }
                        }
                    }
                }
            }

            ParameterType.DoubleList -> {
                sequenceEditor {
                    range = parameter.doubleRange!!
                    label = parameter.label
                    minimumSequenceLength = parameter.sizeRange!!.start
                    maximumSequenceLength = parameter.sizeRange!!.endInclusive
                    precision = parameter.precision!!

                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, MutableList<Double>>,
                            obj,
                            it.newValue.toMutableList()
                        )
                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, MutableList<Double>>,
                        obj
                    ).let {
                        value = it
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, MutableList<Double>>,
                            obj,
                            it
                        )
                    }
                }
            }

            ParameterType.Vector2 -> {
                slidersVector2 {
                    range = parameter.doubleRange!!
                    label = parameter.label
                    precision = parameter.precision!!

                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Vector2>,
                            obj,
                            it.newValue
                        )

                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, Vector2>,
                        obj
                    ).let {
                        value = it
                        setAndPersist(compartment.label, parameter.property as KMutableProperty1<Any, Vector2>, obj, it)
                    }
                }
            }

            ParameterType.Vector3 -> {
                slidersVector3 {
                    range = parameter.doubleRange!!
                    label = parameter.label
                    precision = parameter.precision!!

                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Vector3>,
                            obj,
                            it.newValue
                        )

                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, Vector3>,
                        obj
                    ).let {
                        value = it
                        setAndPersist(compartment.label, parameter.property as KMutableProperty1<Any, Vector3>, obj, it)
                    }
                }
            }

            ParameterType.Vector4 -> {
                slidersVector4 {
                    range = parameter.doubleRange!!
                    label = parameter.label
                    precision = parameter.precision!!

                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Vector4>,
                            obj,
                            it.newValue
                        )

                        onChangeListener?.invoke(parameter.property!!.name, it.newValue)
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, Vector4>,
                        obj
                    ).let {
                        value = it
                        setAndPersist(compartment.label, parameter.property as KMutableProperty1<Any, Vector4>, obj, it)
                    }
                }
            }

            ParameterType.Option -> {
                dropdownButton {
                    val enumProperty = parameter.property as KMutableProperty1<Any, Enum<*>>
                    val value = enumProperty.get(obj)
                    label = parameter.label
                    // -- this is dirty, but it is the only way to get the constants for arbitrary enums
                    // -- (that I know of, at least)
                    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") val jEnum = value as java.lang.Enum<*>
                    // -- we don't use the property syntax here because that leads to compilation errors
                    @Suppress("UsePropertyAccessSyntax") val constants = jEnum.getDeclaringClass().getEnumConstants()
                    constants.forEach {
                        item {
                            label = it.name
                            data = it
                        }
                    }
                    events.valueChanged.listen {
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Enum<*>>,
                            obj,
                            it.value.data as? Enum<*> ?: error("no data")
                        )

                        onChangeListener?.invoke(
                            parameter.property!!.name,
                            it.value.data as? Enum<*> ?: error("no data")
                        )
                    }
                    getPersistedOrDefault(
                        compartment.label,
                        parameter.property as KMutableProperty1<Any, Enum<*>>,
                        obj
                    ).let { enum ->
                        (this@dropdownButton).value = items().find { item -> item.data == enum }
                            ?: error("no matching item found")
                        setAndPersist(
                            compartment.label,
                            parameter.property as KMutableProperty1<Any, Enum<*>>,
                            obj,
                            enum
                        )
                    }
                }
            }
        }
    }
    //</editor-fold>

    private val trackedObjects = mutableMapOf<LabeledObject, TrackedObjectBinding>()

    private fun updateControls() {
        for ((labeledObject, binding) in trackedObjects) {
            for ((parameter, control) in binding.parameterControls) {
                updateControl(labeledObject, parameter, control)
            }
        }
    }

    class ParameterValue(
        var doubleValue: Double? = null,
        var intValue: Int? = null,
        var booleanValue: Boolean? = null,
        var colorValue: ColorRGBa? = null,
        var vector2Value: Vector2? = null,
        var vector3Value: Vector3? = null,
        var vector4Value: Vector4? = null,
        var doubleListValue: MutableList<Double>? = null,
        var textValue: String? = null,
        var optionValue: String? = null,
        var minValue: Double? = null,
        var maxValue: Double? = null
    )


    /**
     * Can be called by the user to obtain an object to be serialized
     * externally. This allows the user to combine custom data with gui
     * state and save it all to one file. Complements `.fromObject()`.
     */
    fun toObject(): Map<String, Map<String, ParameterValue>> {
        fun <T> KMutableProperty1<out Any, Any?>?.qget(obj: Any): T {
            return (this as KMutableProperty1<Any, T>).get(obj)
        }

        return trackedObjects.entries.associate { (lo, b) ->
            Pair(lo.label, b.parameterControls.keys.associate { k ->
                Pair(
                    k.property?.name ?: k.function?.name
                    ?: error("no name"), when (k.parameterType) {
                        /* 3) setup serializers */
                        ParameterType.Double -> ParameterValue(
                            doubleValue = k.property.qget(lo.obj) as Double,
                            minValue = k.doubleRange?.start,
                            maxValue = k.doubleRange?.endInclusive
                        )

                        ParameterType.Int -> ParameterValue(
                            intValue = k.property.qget(lo.obj) as Int,
                            minValue = k.intRange?.start?.toDouble(),
                            maxValue = k.intRange?.endInclusive?.toDouble()
                        )

                        ParameterType.Action -> ParameterValue()
                        ParameterType.Color -> ParameterValue(colorValue = k.property.qget(lo.obj) as ColorRGBa)
                        ParameterType.Text -> ParameterValue(textValue = k.property.qget(lo.obj) as String)
                        ParameterType.Boolean -> ParameterValue(booleanValue = k.property.qget(lo.obj) as Boolean)
                        ParameterType.XY -> ParameterValue(vector2Value = k.property.qget(lo.obj) as Vector2)
                        ParameterType.DoubleList -> ParameterValue(
                            doubleListValue = k.property.qget(
                                lo.obj
                            ) as MutableList<Double>,
                            minValue = k.doubleRange?.start,
                            maxValue = k.doubleRange?.endInclusive
                        )

                        ParameterType.Vector2 -> ParameterValue(
                            vector2Value = k.property.qget(lo.obj) as Vector2,
                            minValue = k.doubleRange?.start,
                            maxValue = k.doubleRange?.endInclusive
                        )

                        ParameterType.Vector3 -> ParameterValue(
                            vector3Value = k.property.qget(lo.obj) as Vector3,
                            minValue = k.doubleRange?.start,
                            maxValue = k.doubleRange?.endInclusive
                        )

                        ParameterType.Vector4 -> ParameterValue(
                            vector4Value = k.property.qget(lo.obj) as Vector4,
                            minValue = k.doubleRange?.start,
                            maxValue = k.doubleRange?.endInclusive
                        )

                        ParameterType.Path -> ParameterValue(textValue = k.property.qget(lo.obj) as String)

                        ParameterType.Option -> ParameterValue(optionValue = (k.property.qget(lo.obj) as Enum<*>).name)
                    }
                )
            })
        }
    }

    fun saveParameters(file: File) {
        file.writeText(Gson().toJson(toObject()))
    }

    /**
     * Can be called by the user to update the gui using an object
     * deserialized externally. Allows the user to load a larger json object,
     * deserialize it, and use part of it to update the GUI.
     * Complements `.toObject()`.
     */
    fun fromObject(labeledValues: Map<String, Map<String, ParameterValue>>) {
        fun <T> KMutableProperty1<out Any, Any?>?.qset(obj: Any, value: T) =
            (this as KMutableProperty1<Any, T>).set(obj, value)

        fun KMutableProperty1<out Any, Any?>?.enumSet(obj: Any, value: String) {
            val v = (this as KMutableProperty1<Any, Enum<*>>).get(obj)

            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UsePropertyAccessSyntax")
            val enumValue = (v as java.lang.Enum<*>).getDeclaringClass().getEnumConstants().find { it.name == value }
                ?: error("cannot map value $value to enum")
            (this as KMutableProperty1<Any, Enum<*>>).set(obj, enumValue)
        }

        labeledValues.forEach { (label, ps) ->
            trackedObjects.keys.find { it.label == label }?.let { lo ->
                val binding = trackedObjects[lo]!!
                ps.forEach { (parameterName, parameterValue) ->
                    binding.parameters.find { it.property?.name == parameterName }?.let { parameter ->
                        when (parameter.parameterType) {
                            /* 4) Set up deserializers */
                            ParameterType.Double -> parameterValue.doubleValue?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Int -> parameterValue.intValue?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Text -> parameterValue.textValue?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Color -> parameterValue.colorValue?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.XY -> parameterValue.vector2Value?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.DoubleList -> parameterValue.doubleListValue?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Boolean -> parameterValue.booleanValue?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Vector2 -> parameterValue.vector2Value?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Vector3 -> parameterValue.vector3Value?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Vector4 -> parameterValue.vector4Value?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Option -> parameterValue.optionValue?.let {
                                parameter.property.enumSet(lo.obj, it)
                            }

                            ParameterType.Path -> parameterValue.textValue?.let {
                                parameter.property.qset(lo.obj, it)
                            }

                            ParameterType.Action -> {
                                // intentionally do nothing
                            }
                        }
                    }
                }
            }
        }
        updateControls()
    }

    fun loadParameters(file: File) {
        val json = file.readText()
        val typeToken = object : TypeToken<Map<String, Map<String, ParameterValue>>>() {}
        val labeledValues: Map<String, Map<String, ParameterValue>> = try {
            Gson().fromJson(json, typeToken.type)
        } catch (e: JsonSyntaxException) {
            println("could not parse json: $json")
            throw e
        }

        fromObject(labeledValues)
    }

    private fun updateControl(labeledObject: LabeledObject, parameter: Parameter, control: Element) {
        when (parameter.parameterType) {
            /* 5) Update control from property value */
            ParameterType.Double -> {
                (control as Slider).value =
                    (parameter.property as KMutableProperty1<Any, Double>).get(labeledObject.obj)
            }

            ParameterType.Int -> {
                (control as Slider).value =
                    (parameter.property as KMutableProperty1<Any, Int>).get(labeledObject.obj).toDouble()
            }

            ParameterType.Text -> {
                (control as Textfield).value =
                    (parameter.property as KMutableProperty1<Any, String>).get(labeledObject.obj)
            }

            ParameterType.Color -> {
                (control as ColorpickerButton).color =
                    (parameter.property as KMutableProperty1<Any, ColorRGBa>).get(labeledObject.obj)
            }

            ParameterType.XY -> {
                (control as XYPad).value =
                    (parameter.property as KMutableProperty1<Any, Vector2>).get(labeledObject.obj)
            }

            ParameterType.DoubleList -> {
                (control as SequenceEditor).value =
                    (parameter.property as KMutableProperty1<Any, MutableList<Double>>).get(labeledObject.obj)
            }

            ParameterType.Boolean -> {
                (control as Toggle).value =
                    (parameter.property as KMutableProperty1<Any, Boolean>).get(labeledObject.obj)
            }

            ParameterType.Vector2 -> {
                (control as SlidersVector2).value =
                    (parameter.property as KMutableProperty1<Any, Vector2>).get(labeledObject.obj)
            }

            ParameterType.Vector3 -> {
                (control as SlidersVector3).value =
                    (parameter.property as KMutableProperty1<Any, Vector3>).get(labeledObject.obj)
            }

            ParameterType.Vector4 -> {
                (control as SlidersVector4).value =
                    (parameter.property as KMutableProperty1<Any, Vector4>).get(labeledObject.obj)
            }

            ParameterType.Option -> {
                val ddb = control as DropdownButton
                ddb.value = ddb.items().find { item ->
                    item.data == (parameter.property as KMutableProperty1<Any, Enum<*>>).get(labeledObject.obj)
                } ?: error("could not find item")
            }

            ParameterType.Path -> {

            }

            ParameterType.Action -> {
                // intentionally do nothing
            }
        }
    }

    fun randomize(strength: Double = 0.05) {
        for ((labeledObject, binding) in trackedObjects) {
            // -- only randomize visible parameters
            for (parameter in binding.parameterControls.keys) {
                when (parameter.parameterType) {
                    /* 6) Set up value randomizers */
                    ParameterType.Double -> {
                        val min = parameter.doubleRange!!.start
                        val max = parameter.doubleRange!!.endInclusive
                        val currentValue = (parameter.property as KMutableProperty1<Any, Double>).get(labeledObject.obj)
                        val randomValue = Double.uniform(min, max)
                        val newValue = mix(currentValue, randomValue, strength)
                        (parameter.property as KMutableProperty1<Any, Double>).set(labeledObject.obj, newValue)
                    }

                    ParameterType.Int -> {
                        val min = parameter.intRange!!.first
                        val max = parameter.intRange!!.last
                        val currentValue = (parameter.property as KMutableProperty1<Any, Int>).get(labeledObject.obj)
                        val randomValue = Double.uniform(min.toDouble(), max.toDouble())
                        val newValue = mix(currentValue.toDouble(), randomValue, strength).roundToInt()
                        (parameter.property as KMutableProperty1<Any, Int>).set(labeledObject.obj, newValue)
                    }

                    ParameterType.Boolean -> {
                        //I am not sure about randomizing boolean values here
                        //(parameter.property as KMutableProperty1<Any, Boolean>).set(labeledObject.obj, (Math.random() < 0.5))
                    }

                    ParameterType.Color -> {
                        val currentValue =
                            (parameter.property as KMutableProperty1<Any, ColorRGBa>).get(labeledObject.obj)
                        val randomValue =
                            ColorRGBa.fromVector(Vector3.uniform(0.0, 1.0), currentValue.alpha, currentValue.linearity)
                        val newValue = currentValue.mix(randomValue, strength)
                        (parameter.property as KMutableProperty1<Any, ColorRGBa>).set(labeledObject.obj, newValue)
                    }

                    ParameterType.Vector2 -> {
                        val min = parameter.doubleRange!!.start
                        val max = parameter.doubleRange!!.endInclusive
                        val currentValue =
                            (parameter.property as KMutableProperty1<Any, Vector2>).get(labeledObject.obj)
                        val randomValue = Vector2.uniform(min, max)
                        val newValue = currentValue.mix(randomValue, strength)
                        (parameter.property as KMutableProperty1<Any, Vector2>).set(labeledObject.obj, newValue)
                    }

                    ParameterType.XY -> {
                        val min = parameter.vectorRange!!.first
                        val max = parameter.vectorRange!!.second
                        val currentValue =
                            (parameter.property as KMutableProperty1<Any, Vector2>).get(labeledObject.obj)
                        val randomValue = Vector2.uniform(min, max)
                        val newValue = currentValue.mix(randomValue, strength)
                        (parameter.property as KMutableProperty1<Any, Vector2>).set(labeledObject.obj, newValue)
                    }

                    ParameterType.Vector3 -> {
                        val min = parameter.doubleRange!!.start
                        val max = parameter.doubleRange!!.endInclusive
                        val currentValue =
                            (parameter.property as KMutableProperty1<Any, Vector3>).get(labeledObject.obj)
                        val randomValue = Vector3.uniform(min, max)
                        val newValue = currentValue.mix(randomValue, strength)
                        (parameter.property as KMutableProperty1<Any, Vector3>).set(labeledObject.obj, newValue)
                    }

                    ParameterType.Vector4 -> {
                        val min = parameter.doubleRange!!.start
                        val max = parameter.doubleRange!!.endInclusive
                        val currentValue =
                            (parameter.property as KMutableProperty1<Any, Vector4>).get(labeledObject.obj)
                        val randomValue = Vector4.uniform(min, max)
                        val newValue = currentValue.mix(randomValue, strength)
                        (parameter.property as KMutableProperty1<Any, Vector4>).set(labeledObject.obj, newValue)
                    }

                    else -> {
                        // intentionally do nothing
                    }
                }
            }
        }
        updateControls()
    }

    /**
     * Recursively find a unique label
     * @param label to find an alternate for in case it already exist
     */
    private fun resolveUniqueLabel(label: String): String {
        return trackedObjects.keys.find { it.label == label }?.let { lo ->
            resolveUniqueLabel(Regex("(.*) / ([0-9]+)").matchEntire(lo.label)?.let {
                "${it.groupValues[1]} / ${1 + it.groupValues[2].toInt()}"
            } ?: "$label / 2")
        } ?: label
    }

    /**
     * Add an object to the GUI
     * @param objectWithParameters an object of a class that annotated parameters
     * @param label an optional label that overrides the label supplied in a [Description] annotation
     * @return pass-through of [objectWithParameters]
     */
    fun <T : Any> add(objectWithParameters: T, label: String? = objectWithParameters.title()): T {
        val parameters = objectWithParameters.listParameters()
        val uniqueLabel = resolveUniqueLabel(label ?: "No name")

        if (parameters.isNotEmpty()) {
            val collapseStates = persistentCompartmentStates.getOrPut(Driver.instance.contextID) {
                mutableMapOf()
            }
            collapseStates.getOrPut(uniqueLabel) {
                CompartmentState(compartmentsCollapsedByDefault)
            }
            trackedObjects[LabeledObject(uniqueLabel, objectWithParameters)] = TrackedObjectBinding(parameters)
        }
        return objectWithParameters
    }

    /**
     * Add an object to the GUI using a builder.
     * @param label an optional label that overrides the label supplied in a [Description] annotation
     * @return the built object
     */
    fun <T : Any> add(label: String? = null, builder: () -> T): T {
        val t = builder()
        return add(t, label ?: t.title())
    }

    override fun afterDraw(drawer: Drawer, program: Program) {
        if (doubleBind) {
            updateControls()
        }
    }

    override fun shutdown(program: Program) {
        if (persistState) {
            val folderFile = File(defaultSaveFolder)
            if (folderFile.exists() && folderFile.isDirectory) {
                saveParameters(File(defaultSaveFolder, "${program.name}-latest.json"))
            } else {
                if (folderFile.mkdirs()) {
                    saveParameters(File(defaultSaveFolder, "${program.name}-latest.json"))
                } else {
                    logger.error { "Could not persist GUI state because could not create directory ${folderFile.absolutePath}" }
                }
            }
        }
    }
}

@JvmName("addToGui")
fun <T : Any> T.addTo(gui: GUI, label: String? = this.title()): T {
    gui.add(this, label)
    return this
}
