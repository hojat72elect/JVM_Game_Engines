package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.KeyValueStore
import de.fabmax.kool.modules.ui2.Dimension
import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.Grow
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.toBuffer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class DockLayout(val layoutNodes: List<LayoutNode>) {

    fun restoreLayout(target: Dock, itemProvider: (String) -> Dockable? = { null }): Boolean {
        var success = true
        val restoredNodes = mutableMapOf<String, DockNode>()

        layoutNodes.filter { it.nodeType == NodeType.Floating }.forEach {
            it.applyFloating(itemProvider)
        }
        layoutNodes.filter { it.nodeType != NodeType.Floating }.forEach {
            restoredNodes[it.path] = it.toNode(target, itemProvider)
        }

        restoredNodes.keys.forEach { path ->
            val node = restoredNodes[path]!!
            node.parent = path.parentPath()?.let { parentPath ->
                val parent = restoredNodes[parentPath] as? DockNodeInter
                if (parent != null) {
                    parent.childNodes += node
                } else {
                    logE { "Failed to restore dock layout! Parent node not found: $parentPath" }
                    success = false
                }
                parent
            }
        }

        if (success) {
            val root = restoredNodes.values.find { it.parent == null }
            if (root != null) {
                target.root = root
            } else {
                success = false
            }
        }
        return success
    }

    private fun String.parentPath(): String? {
        val parentIdx = lastIndexOf('/')
        return if (parentIdx > 0) {
            substring(0 until parentIdx)
        } else {
            null
        }
    }

    companion object {
        fun saveLayout(dock: Dock, key: String) {
            val json = Json { prettyPrint = true }
            KeyValueStore.store(key, json.encodeToString(serializeLayout(dock)).encodeToByteArray().toBuffer())
        }

        fun loadLayout(key: String, target: Dock, itemProvider: (String) -> Dockable? = { null }): Boolean {
            val data = KeyValueStore.load(key)?.toArray()?.decodeToString()
            if (data != null) {
                try {
                    return Json.decodeFromString<DockLayout>(data).restoreLayout(target, itemProvider)
                } catch (e: Exception) {
                    logE { "Failed to load layout: $e" }
                }
            }
            return false
        }

        fun serializeLayout(dock: Dock): DockLayout {
            val layoutNodes = mutableListOf<LayoutNode>()
            fun traverseNodes(nd: DockNode) {
                val layoutNode = LayoutNode.fromNode(nd)
                layoutNodes += layoutNode
                if (nd is DockNodeInter) {
                    nd.childNodes.forEach { traverseNodes(it) }
                }
            }
            traverseNodes(dock.root)

            dock.dockables.values.filter { !it.isDocked.value }.forEach {
                layoutNodes += LayoutNode.fromFloatingDockable(it)
            }

            return DockLayout(layoutNodes)
        }
    }

    @Serializable
    class LayoutNode(
        val path: String,
        val nodeType: NodeType,
        val width: NodeDim,
        val height: NodeDim,
        val items: List<String>,
        val topItem: String? = null,
        val floatingX: NodeDim? = null,
        val floatingY: NodeDim? = null,
    ) {
        fun toNode(dock: Dock, itemProvider: (String) -> Dockable?): DockNode {
            val w = width.toDimension()
            val h = height.toDimension()
            return when(nodeType) {
                NodeType.Row -> DockNodeRow(dock, null, w, h)
                NodeType.Column -> DockNodeColumn(dock, null, w, h)
                NodeType.Leaf -> {
                    val node = DockNodeLeaf(dock, null, w, h)
                    items.forEach { itemName ->
                        itemProvider(itemName)?.let { node.dock(it) }
                    }
                    topItem?.let { top ->
                        node.dockedItems.find { it.name == top }?.let { node.bringToTop(it) }
                    }
                    node
                }
                NodeType.Floating -> error("Use applyFloating()")
            }
        }

        fun applyFloating(itemProvider: (String) -> Dockable?) {
            val name = items.getOrNull(0) ?: return
            itemProvider(name)?.let { dockable ->
                dockable.floatingX.set(floatingX?.toDimension() as? Dp ?: Dp(100f))
                dockable.floatingY.set(floatingY?.toDimension() as? Dp ?: Dp(100f))
                dockable.floatingWidth.set(width.toDimension())
                dockable.floatingHeight.set(height.toDimension())
            }
        }

        companion object {
            fun fromNode(node: DockNode): LayoutNode {
                val path = node.getPath()
                val width = NodeDim.fromDimension(node.width.value)
                val height = NodeDim.fromDimension(node.height.value)
                val nodeType = when(node) {
                    is DockNodeLeaf -> NodeType.Leaf
                    is DockNodeColumn -> NodeType.Column
                    is DockNodeRow -> NodeType.Row
                }
                val items: List<String>
                val topItem: String?
                if (node is DockNodeLeaf) {
                    items = node.dockedItems.map { it.name }
                    topItem = node.dockItemOnTop?.name
                } else {
                    items = emptyList()
                    topItem = null
                }
                return LayoutNode(path, nodeType, width, height, items, topItem)
            }

            fun fromFloatingDockable(floating: Dockable): LayoutNode {
                val x = NodeDim.fromDimension(floating.floatingX.value)
                val y = NodeDim.fromDimension(floating.floatingY.value)
                val width = NodeDim.fromDimension(floating.floatingWidth.value)
                val height = NodeDim.fromDimension(floating.floatingHeight.value)
                return LayoutNode("", NodeType.Floating, width, height, listOf(floating.name), floatingX = x, floatingY = y)
            }
        }
    }

    @Serializable
    class NodeDim(val value: Float, val isAbsolute: Boolean) {
        fun toDimension(): Dimension {
            return if (isAbsolute) Dp(value) else Grow(value)
        }

        companion object {
            fun fromDimension(dimension: Dimension): NodeDim {
                return when(dimension) {
                    is Grow -> NodeDim(dimension.weight, false)
                    is Dp -> NodeDim(dimension.value, true)
                    else -> NodeDim(1f, false)
                }
            }
        }
    }

    enum class NodeType {
        Leaf,
        Row,
        Column,
        Floating
    }
}
