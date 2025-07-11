package org.openrndr.extra.composition

import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.ShadeStyle
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.transforms.buildTransform
import org.openrndr.shape.Rectangle
import org.openrndr.shape.Shape
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.bounds
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

/**
 * Describes a node in a composition
 */
sealed class CompositionNode {

    var id: String? = null

    var parent: CompositionNode? = null

    /** This CompositionNode's own style. */
    var style: Style = Style()

    /**
     * This CompositionNode's computed style.
     * Where every style attribute is obtained by
     * overwriting the Style in the following order:
     * 1. Default style attributes.
     * 2. Parent Node's computed style's inheritable attributes.
     * 3. This Node's own style attributes.
     */
    val effectiveStyle: Style
        get() = when (val p = parent) {
            is CompositionNode -> style inherit p.effectiveStyle
            else -> style
        }

    /**
     * Custom attributes to be applied to the Node in addition to the Style attributes.
     */
    var attributes = mutableMapOf<String, String?>()

    /**
     * a map that stores user data
     */
    val userData = mutableMapOf<String, Any>()

    /**
     * a [Rectangle] that describes the bounding box of the contents
     */
    abstract val bounds: Rectangle

    val effectiveStroke get() = effectiveStyle.stroke.value
    val effectiveStrokeOpacity get() = effectiveStyle.strokeOpacity.value
    val effectiveStrokeWeight get() = effectiveStyle.strokeWeight.value
    val effectiveMiterLimit get() = effectiveStyle.miterLimit.value
    val effectiveLineCap get() = effectiveStyle.lineCap.value
    val effectiveLineJoin get() = effectiveStyle.lineJoin.value
    val effectiveFill get() = effectiveStyle.fill.value
    val effectiveFillOpacity get() = effectiveStyle.fillOpacity.value
    val effectiveDisplay get() = effectiveStyle.display.value
    val effectiveOpacity get() = effectiveStyle.opacity.value
    val effectiveVisibility get() = effectiveStyle.visibility.value
    val effectiveShadeStyle get() = effectiveStyle.shadeStyle.value

    /** Calculates the absolute transformation of the current node. */
    val effectiveTransform: Matrix44
        get() = when (val p = parent) {
            is CompositionNode -> transform * p.effectiveTransform
            else -> transform
        }

    var stroke
        get() = style.stroke.value
        set(value) {
            style.stroke = when (value) {
                null -> Paint.None
                else -> Paint.RGB(value)
            }
        }
    var strokeOpacity
        get() = style.strokeOpacity.value
        set(value) {
            style.strokeOpacity = Numeric.Rational(value)
        }
    var strokeWeight
        get() = style.strokeWeight.value
        set(value) {
            style.strokeWeight = Length.Pixels(value)
        }
    var miterLimit
        get() = style.miterLimit.value
        set(value) {
            style.miterLimit = Numeric.Rational(value)
        }
    var lineCap
        get() = style.lineCap.value
        set(value) {
            style.lineCap = when (value) {
                org.openrndr.draw.LineCap.BUTT -> LineCap.Butt
                org.openrndr.draw.LineCap.ROUND -> LineCap.Round
                org.openrndr.draw.LineCap.SQUARE -> LineCap.Square
            }
        }
    var lineJoin
        get() = style.lineJoin.value
        set(value) {
            style.lineJoin = when (value) {
                org.openrndr.draw.LineJoin.BEVEL -> LineJoin.Bevel
                org.openrndr.draw.LineJoin.MITER -> LineJoin.Miter
                org.openrndr.draw.LineJoin.ROUND -> LineJoin.Round
            }
        }
    var fill
        get() = style.fill.value
        set(value) {
            style.fill = when (value) {
                null -> Paint.None
                else -> Paint.RGB(value)
            }
        }
    var fillOpacity
        get() = style.fillOpacity.value
        set(value) {
            style.fillOpacity = Numeric.Rational(value)
        }
    var opacity
        get() = style.opacity.value
        set(value) {
            style.opacity = Numeric.Rational(value)
        }
    var shadeStyle
        get() = style.shadeStyle.value
        set(value) {
            style.shadeStyle = Shade.Value(value)
        }
    var transform
        get() = style.transform.value
        set(value) {
            style.transform = Transform.Matrix(value)
        }
}

// TODO: Deprecate this?
operator fun KMutableProperty0<Shade>.setValue(thisRef: Style, property: KProperty<*>, value: ShadeStyle) {
    this.set(Shade.Value(value))
}

fun transform(node: CompositionNode): Matrix44 =
    (node.parent?.let { transform(it) } ?: Matrix44.IDENTITY) * node.transform

/**
 * a [CompositionNode] that holds a single image [ColorBuffer]
 */
class ImageNode(var image: ColorBuffer, var x: Double, var y: Double, var width: Double, var height: Double) :
    CompositionNode() {
    override val bounds: Rectangle
        get() = Rectangle(0.0, 0.0, width, height).contour.transform(transform(this)).bounds
}

/**
 * a [CompositionNode] that holds a single [Shape]
 */
class ShapeNode(var shape: Shape) : CompositionNode() {
    override val bounds: Rectangle
        get() {
            val t = effectiveTransform
            return if (t === Matrix44.IDENTITY) {
                shape.bounds
            } else {
                shape.bounds.contour.transform(t).bounds
            }
        }

    /**
     * apply transforms of all ancestor nodes and return a new detached org.openrndr.shape.ShapeNode with conflated transform
     */
    fun conflate(): ShapeNode {
        return ShapeNode(shape).also {
            it.id = id
            it.parent = parent
            it.style = style
            it.transform = transform(this)
            it.attributes = attributes
        }
    }


    /**
     * apply transforms of all ancestor nodes and return a new detached shape node with identity transform and transformed Shape
     * @param composition use viewport transform
     */
    fun flatten(composition: Composition? = null): ShapeNode {

        val viewport = composition?.calculateViewportTransform() ?: Matrix44.IDENTITY

        return ShapeNode(shape.transform(viewport * transform(this))).also {
            it.id = id
            it.parent = parent
            it.style = effectiveStyle
            it.attributes = attributes
        }
    }

    fun copy(
        id: String? = this.id,
        parent: CompositionNode? = null,
        style: Style = this.style,
        attributes: MutableMap<String, String?> = this.attributes,
        shape: Shape = this.shape
    ): ShapeNode {
        return ShapeNode(shape).also {
            it.id = id
            it.parent = parent
            it.style = style
            it.attributes = attributes
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShapeNode) return false
        if (shape != other.shape) return false
        return true
    }

    override fun hashCode(): Int {
        return shape.hashCode()
    }

    /**
     * the local [Shape] with the [effectiveTransform] applied to it
     */
    val effectiveShape
        get() = shape.transform(effectiveTransform)
}

/**
 * a [CompositionNode] that holds a single text
 */
data class TextNode(var text: String, var contour: ShapeContour?) : CompositionNode() {
    // TODO: This should not be Rectangle.EMPTY
    override val bounds: Rectangle
        get() = Rectangle.EMPTY
}


/**
 * Represents a group node in a composition hierarchy.
 * A `GroupNode` itself does not have explicit contents but serves as a container for managing child nodes.
 * It allows grouping of multiple `CompositionNode` instances and provides functionalities like calculating
 * the bounds for all its child elements and copying itself with overrides.
 *
 * @property children A mutable list of child nodes belonging to this group. Defaults to an empty list.
 */
open class GroupNode(open val children: MutableList<CompositionNode> = mutableListOf()) : CompositionNode() {
    override val bounds: Rectangle
        get() {
            return children.map { it.bounds }.bounds
        }

    fun copy(
        id: String? = this.id,
        parent: CompositionNode? = null,
        style: Style = this.style,
        children: MutableList<CompositionNode> = this.children
    ): GroupNode {
        return GroupNode(children).also {
            it.id = id
            it.parent = parent
            it.style = style
            it.attributes = attributes
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GroupNode) return false

        if (children != other.children) return false
        return true
    }

    override fun hashCode(): Int {
        return children.hashCode()
    }
}

data class CompositionDimensions(val x: Length, val y: Length, val width: Length, val height: Length) {
    val position = Vector2((x as Length.Pixels).value, (y as Length.Pixels).value)
    val dimensions = Vector2((width as Length.Pixels).value, (height as Length.Pixels).value)

    constructor(rectangle: Rectangle) : this(
        rectangle.corner.x.pixels,
        rectangle.corner.y.pixels,
        rectangle.dimensions.x.pixels,
        rectangle.dimensions.y.pixels
    )

    override fun toString(): String = "$x $y $width $height"

    // I'm not entirely sure why this is needed but
    // but otherwise equality checks will never succeed
    override fun equals(other: Any?): Boolean {
        return other is CompositionDimensions
                && x.value == other.x.value
                && y.value == other.y.value
                && width.value == other.width.value
                && height.value == other.height.value
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + height.hashCode()
        return result
    }
}

val defaultCompositionDimensions = CompositionDimensions(0.0.pixels, 0.0.pixels, 768.0.pixels, 576.0.pixels)


/**
 * Represents a specialized type of `GroupNode` in a composition hierarchy, serving as a container for child nodes.
 *
 * `GroupNodeStop` inherits from `GroupNode` and extends its functionality. It can be used to define a specific
 * grouping behavior or semantic grouping in a composition system. Instances of this class hold a mutable list
 * of `CompositionNode` entities as children.
 *
 * @constructor Creates a `GroupNodeStop` with the given child nodes.
 * @param children A mutable list of `CompositionNode` instances to be managed by this group.
 */
class GroupNodeStop(children: MutableList<CompositionNode>) : GroupNode(children)

/**
 * A vector composition.
 * @param root the root node of the composition
 * @param bounds the dimensions of the composition
 */
class Composition(val root: CompositionNode, var bounds: CompositionDimensions = defaultCompositionDimensions) {
    constructor(root: CompositionNode, bounds: Rectangle) : this(root, CompositionDimensions(bounds))

    /** SVG/XML namespaces */
    val namespaces = mutableMapOf<String, String>()

    var style: Style = Style()

    /**
     * The style attributes affecting the whole document, such as the viewBox area and aspect ratio.
     */
    var documentStyle: DocumentStyle = DocumentStyle()

    init {
        val (x, y, width, height) = bounds
        style.x = x
        style.y = y
        style.width = width
        style.height = height
    }

    fun findShapes() = root.findShapes()
    fun findShape(id: String): ShapeNode? {
        return (root.find { it is ShapeNode && it.id == id }) as? ShapeNode
    }

    fun findImages() = root.findImages()
    fun findImage(id: String): ImageNode? {
        return (root.find { it is ImageNode && it.id == id }) as? ImageNode
    }

    fun findGroups(): List<GroupNode> = root.findGroups()
    fun findGroup(id: String): GroupNode? {
        return (root.find { it is GroupNode && it.id == id }) as? GroupNode
    }

    fun clear() = (root as? GroupNode)?.children?.clear()

    /** Calculates the equivalent of `1%` in pixels. */
    internal fun normalizedDiagonalLength(): Double = sqrt(bounds.dimensions.squaredLength / 2.0)

    /**
     * Calculates effective viewport transformation using [viewBox] and [preserveAspectRatio].
     * As per [the SVG 2.0 spec](https://svgwg.org/svg2-draft/single-page.html#coords-ComputingAViewportsTransform).
     */
    fun calculateViewportTransform(): Matrix44 {
        return when (documentStyle.viewBox) {
            ViewBox.None -> Matrix44.IDENTITY
            is ViewBox.Value -> {
                when (val vb = (documentStyle.viewBox as ViewBox.Value).value) {
                    Rectangle.EMPTY -> {
                        // The intent is to not display the element
                        Matrix44.ZERO
                    }

                    else -> {
                        val vbCorner = vb.corner
                        val vbDims = vb.dimensions
                        val eCorner = bounds.position
                        val eDims = bounds.dimensions
                        val (align, meetOrSlice) = documentStyle.preserveAspectRatio

                        val scale = (eDims / vbDims).let {
                            if (align != Align.NONE) {
                                if (meetOrSlice == MeetOrSlice.MEET) {
                                    Vector2(min(it.x, it.y))
                                } else {
                                    Vector2(max(it.x, it.y))
                                }
                            } else {
                                it
                            }
                        }

                        val translate = (eCorner - (vbCorner * scale)).let {
                            val cx = eDims.x - vbDims.x * scale.x
                            val cy = eDims.y - vbDims.y * scale.y
                            it + when (align) {
                                // TODO: This first one probably doesn't comply with the spec
                                Align.NONE -> Vector2.ZERO
                                Align.X_MIN_Y_MIN -> Vector2.ZERO
                                Align.X_MID_Y_MIN -> Vector2(cx / 2, 0.0)
                                Align.X_MAX_Y_MIN -> Vector2(cx, 0.0)
                                Align.X_MIN_Y_MID -> Vector2(0.0, cy / 2)
                                Align.X_MID_Y_MID -> Vector2(cx / 2, cy / 2)
                                Align.X_MAX_Y_MID -> Vector2(cx, cy / 2)
                                Align.X_MIN_Y_MAX -> Vector2(0.0, cy)
                                Align.X_MID_Y_MAX -> Vector2(cx / 2, cy)
                                Align.X_MAX_Y_MAX -> Vector2(cx, cy)
                            }
                        }

                        buildTransform {
                            translate(translate)
                            scale(scale.x, scale.y, 1.0)
                        }
                    }
                }
            }
        }
    }
}

/**
 * remove node from its parent [CompositionNode]
 */
fun CompositionNode.remove() {
    require(parent != null) { "parent is null" }
    val parentGroup = (parent as? GroupNode)
    if (parentGroup != null) {
        val filtered = parentGroup.children.filter {
            it != this
        }
        parentGroup.children.clear()
        parentGroup.children.addAll(filtered)
    }
    parent = null
}

/**
 * Recursively finds all terminal nodes within the composition tree starting from the current node
 * and applies the provided filter to determine which nodes to include in the result.
 *
 * @param filter A predicate function used to filter terminal nodes. Only nodes that satisfy this
 * predicate will be included in the result.
 * @return A list of terminal nodes within the composition tree that satisfy the given filter.
 */
fun CompositionNode.findTerminals(filter: (CompositionNode) -> Boolean): List<CompositionNode> {
    val result = mutableListOf<CompositionNode>()
    fun find(node: CompositionNode) {
        when (node) {
            is GroupNode -> node.children.forEach { find(it) }
            else -> if (filter(node)) {
                result.add(node)
            }
        }
    }
    find(this)
    return result
}

/**
 * Finds all `CompositionNode` instances in the current node hierarchy that satisfy the given filter.
 * Traverses the hierarchy recursively, evaluating each node and its children.
 *
 * @param filter A predicate function to determine whether a node should be included in the result.
 *               It takes a `CompositionNode` as input and returns a Boolean.
 * @return A list of `CompositionNode` instances that satisfy the provided filter condition.
 */
fun CompositionNode.findAll(filter: (CompositionNode) -> Boolean): List<CompositionNode> {
    val result = mutableListOf<CompositionNode>()
    fun find(node: CompositionNode) {
        if (filter(node)) {
            result.add(node)
        }
        if (node is GroupNode) {
            node.children.forEach { find(it) }
        }
    }
    find(this)
    return result
}

/**
 * Finds first [CompositionNode] to match the given [predicate].
 */
fun CompositionNode.find(predicate: (CompositionNode) -> Boolean): CompositionNode? {
    if (predicate(this)) {
        return this
    } else if (this is GroupNode) {
        val deque: ArrayDeque<CompositionNode> = ArrayDeque(children)
        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            if (predicate(node)) {
                return node
            } else if (node is GroupNode) {
                deque.addAll(node.children)
            }
        }
    }
    return null
}

/**
 * find all descendant [ShapeNode] nodes, including potentially this node
 * @return a [List] of [ShapeNode] nodes
 */
fun CompositionNode.findShapes(): List<ShapeNode> = findTerminals { it is ShapeNode }.map { it as ShapeNode }

/**
 * find all descendant [ImageNode] nodes, including potentially this node
 * @return a [List] of [ImageNode] nodes
 */
fun CompositionNode.findImages(): List<ImageNode> = findTerminals { it is ImageNode }.map { it as ImageNode }

/**
 * find all descendant [GroupNode] nodes, including potentially this node
 * @return a [List] of [GroupNode] nodes
 */
fun CompositionNode.findGroups(): List<GroupNode> = findAll { it is GroupNode }.map { it as GroupNode }

/**
 * visit this [CompositionNode] and all descendant nodes and execute [visitor]
 */
fun CompositionNode.visitAll(visitor: (CompositionNode.() -> Unit)) {
    visitor()
    if (this is GroupNode) {
        for (child in children) {
            child.visitAll(visitor)
        }
    }
}

/**
 * org.openrndr.shape.UserData delegate
 */
class UserData<T : Any>(
    val name: String, val initial: T
) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(node: CompositionNode, property: KProperty<*>): T {
        val value: T? = node.userData[name] as? T
        return value ?: initial
    }

    operator fun setValue(stylesheet: CompositionNode, property: KProperty<*>, value: T) {
        stylesheet.userData[name] = value
    }
}

/**
 * Filters a `CompositionNode` and its hierarchy based on the provided filter function.
 * The method recursively applies the filter to the node and its children, creating
 * a new hierarchy that contains only the nodes for which the filter returns true.
 * If the filter condition fails for the root node, null is returned.
 *
 * For `GroupNode` instances, the method applies the filter to its children and
 * creates a new `GroupNode` containing filtered children that satisfy the filter condition.
 * For `ShapeNode` instances, a copy is created if the filter condition is met.
 *
 * @param filter A lambda function that takes a `CompositionNode` and returns a `Boolean`.
 *               The function determines if a node should be included in the resulting hierarchy.
 *
 * @return A new filtered `CompositionNode` tree, or null if the root node does not pass the filter.
 */
fun CompositionNode.filter(filter: (CompositionNode) -> Boolean): CompositionNode? {
    val f = filter(this)

    if (!f) {
        return null
    }

    if (this is GroupNode) {
        val copies = mutableListOf<CompositionNode>()
        children.forEach {
            val filtered = it.filter(filter)
            if (filtered != null) {
                when (filtered) {
                    is ShapeNode -> {
                        copies.add(filtered.copy(parent = this))
                    }

                    is GroupNode -> {
                        copies.add(filtered.copy(parent = this))
                    }

                    else -> {

                    }
                }
            }
        }
        return GroupNode(children = copies)
    } else {
        return this
    }
}

fun CompositionNode.map(mapper: (CompositionNode) -> CompositionNode): CompositionNode {
    val r = mapper(this)
    return when (r) {
        is GroupNodeStop -> {
            r.copy().also { copy ->
                copy.children.forEach {
                    it.parent = copy
                }
            }
        }

        is GroupNode -> {
            val copy = r.copy(children = r.children.map { it.map(mapper) }.toMutableList())
            copy.children.forEach {
                it.parent = copy
            }
            copy
        }

        else -> r
    }
}