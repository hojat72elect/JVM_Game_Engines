package com.littlekt.graphics.shader.builder

/**
 * @author Colton Daily
 * @date 2/6/2025
 */
open class ShaderStruct(val name: String, parameters: Map<String, ShaderStructParameterType>) :
    ShaderSrc() {
    val layout: Map<String, ShaderStructEntry>
    val size: Int
    val alignment: Int

    init {
        var currentOffset = 0
        var maxAlignment = 0

        layout =
            parameters
                .map { (name, type) ->
                    val alignment = type.alignment()
                    val alignedOffset = align(currentOffset, alignment)

                    val entry = ShaderStructEntry(alignedOffset, type.size(), alignment, type)
                    currentOffset = alignedOffset + entry.size
                    maxAlignment = maxOf(maxAlignment, alignment)

                    name to entry
                }
                .toMap()

        alignment = maxAlignment
        size = align(currentOffset, alignment)
    }

    override val src by lazy {
        """
            struct $name {
                ${layout.map { (name, type) ->
                    val prefix =
                        when (type.type) {
                            is ShaderStructParameterType.Location -> {
                                "@location(${type.type.index}) "
                            }

                            is ShaderStructParameterType.BuiltIn -> {
                                "@builtin(${type.type.prefix}) "
                            }

                            else -> {
                                ""
                            }
                        }
                    "$prefix$name: ${type.type.name}"
                }.joinToString(",\n")}
            };
        """
            .trimIndent()
            .format()
    }

    private fun align(offset: Int, alignment: Int): Int {
        return (offset + alignment - 1) / alignment * alignment
    }

    override fun toString(): String {
        return "ShaderStruct(name='$name', layout=$layout, size=$size, alignment=$alignment)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ShaderStruct

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
