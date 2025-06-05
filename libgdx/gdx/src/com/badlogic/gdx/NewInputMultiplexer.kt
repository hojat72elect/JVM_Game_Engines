package com.badlogic.gdx

import com.badlogic.gdx.utils.SnapshotArray

/**
 * An [InputProcessor] that delegates to an ordered list of other InputProcessors. Delegation for an event stops if a
 * processor returns true, which indicates that the event was handled.
 *<br/>
 *
 *
 * whenever you want to read a file format from somewhere, this [InputMultiplexer] will choose a couple of [InputProcessor]s to open that file;
 * and if one of those returns true, it will stop.
 */
class NewInputMultiplexer() : InputProcessor {
    private val processors = SnapshotArray<InputProcessor?>(4)

    constructor(vararg processors: InputProcessor?) : this() {
        this.processors.addAll(*processors)
    }

    fun addProcessor(index: Int, processor: InputProcessor) {
        processors.insert(index, processor)
    }

    fun removeProcessor(index: Int) {
        processors.removeIndex(index)
    }

    fun addProcessor(processor: InputProcessor) {
        processors.add(processor)
    }

    fun removeProcessor(processor: InputProcessor) {
        processors.removeValue(processor, true)
    }

    /**
     * @return the number of processors in this multiplexer
     */
    fun size(): Int = processors.size

    fun clear() {
        processors.clear()
    }

    fun getProcessors(): SnapshotArray<InputProcessor?> = processors

    fun setProcessors(vararg processors: InputProcessor?) {
        this.processors.clear()
        this.processors.addAll(*processors)
    }

    fun setProcessors(processors: com.badlogic.gdx.utils.Array<InputProcessor?>) {
        this.processors.clear()
        this.processors.addAll(processors)
    }

    override fun keyDown(keycode: Int): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).keyDown(keycode)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).keyUp(keycode)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).keyTyped(character)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).touchDown(screenX, screenY, pointer, button)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).touchUp(screenX, screenY, pointer, button)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).touchCancelled(screenX, screenY, pointer, button)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).touchDragged(screenX, screenY, pointer)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).mouseMoved(screenX, screenY)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        val items = processors.begin()
        try {
            var i = 0
            val n = processors.size
            while (i < n) {
                if ((items[i] as InputProcessor).scrolled(amountX, amountY)) return true
                i++
            }
        } finally {
            processors.end()
        }
        return false
    }
}
