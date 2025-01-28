package app.thelema.android

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.KeyEvent
import android.view.MotionEvent
import app.thelema.app.APP
import app.thelema.app.AbstractApp
import app.thelema.app.AndroidApp
import app.thelema.audio.AL
import app.thelema.concurrency.ATOM
import app.thelema.data.DATA
import app.thelema.ecs.ECS
import app.thelema.fs.FS
import app.thelema.gl.GL
import app.thelema.img.IMG
import app.thelema.input.KB
import app.thelema.input.MOUSE
import app.thelema.input.TOUCH
import app.thelema.json.JSON
import app.thelema.jvm.concurrency.AtomicProviderJvm
import app.thelema.jvm.data.JvmData
import app.thelema.jvm.json.JsonSimpleJson
import app.thelema.jvm.ode.RigidBodyPhysicsWorld
import app.thelema.net.WS
import app.thelema.utils.LOG
import javax.microedition.khronos.opengles.GL10

/** @param surfaceView custom view
 * @author zeganstyl */
class AndroidApp(
    val context: Context,
    val glesVersion: Int = 3,
    surfaceView: GLSurfaceView? = null,
    var initOnGLThread: ((app: AndroidApp) -> Unit)? = null
): AbstractApp() {
    override var clipboardString: String
        get() = (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip?.getItemAt(0)?.text?.toString() ?: ""
        set(value) {
            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("text", value))
        }

    override var cursor: Int
        get() = 0
        set(_) {}

    override val width: Int
        get() = view.holder.surfaceFrame.width()

    override val height: Int
        get() = view.holder.surfaceFrame.height()

    override val platformType: String
        get() = AndroidApp

    override val time: Long
        get() = System.currentTimeMillis()

    val renderer = object : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(p0: GL10, p1: javax.microedition.khronos.egl.EGLConfig) {
            cachedWidth = width
            cachedHeight = height

            GL.initGL()

            performDefaultSetup()

            initOnGLThread?.invoke(this@AndroidApp)
        }

        override fun onDrawFrame(unused: GL10) {
            updateDeltaTime()

            update()

            render()
        }

        override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
        }
    }

    val mouse = AndroidMouse(this)
    val kb = AndroidKB()
    val touch = AndroidTouch(this)

    val view: GLSurfaceView = surfaceView ?: object: GLSurfaceView(context) {
        override fun onTouchEvent(e: MotionEvent): Boolean {
            touch.onTouchEvent(e)
            mouse.onTouchEvent(e)
            return true
        }

        override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
            kb.keyDown(keyCode)
            return super.onKeyDown(keyCode, event)
        }

        override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
            kb.keyUp(keyCode)
            return super.onKeyUp(keyCode, event)
        }
    }

    val fs = AndroidFS(this)

    var maxAdditionalThreads = 1
        set(value) {
            if (field != value) {
                field = value
                threads = Array(value) { Thread() }
                threadStopped = Array(value) { ATOM.bool(true) }
            }
        }
    var threads = Array(maxAdditionalThreads) { Thread() }
    var threadStopped = Array(maxAdditionalThreads) { ATOM.bool(true) }
    val threadBlocks = ArrayList<() -> Unit>()

    init {
        ECS.setupDefaultComponents()

        ATOM = AtomicProviderJvm()
        APP = this
        LOG = AndroidLog()
        FS = fs
        JSON = JsonSimpleJson()
        DATA = JvmData()
        IMG = AndroidImageLoader(this)
        GL = AndroidGL(this)
        MOUSE = mouse
        KB = kb
        AL = AndroidAudio(context)
        WS = KtorWebSocket()
        TOUCH = touch

        view.setEGLContextClientVersion(glesVersion)
        view.setRenderer(renderer)
    }

    override fun setupPhysicsComponents() {
        RigidBodyPhysicsWorld.initOdeComponents()
    }

    override fun thread(block: () -> Unit) {
        var index: Int = -1
        for (i in threads.indices) {
            if (!threads[i].isAlive) {
                index = i
                break
            }
        }
        if (index == -1) {
            threadBlocks.add(block)
        } else {
            val stopped = threadStopped[index]
            stopped.value = false
            threads[index] = Thread {
                block()
                stopped.value = true
            }.apply { start() }
        }
    }

    override fun loadPreferences(name: String): String {
        TODO("Not yet implemented")
    }

    override fun messageBox(title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.create().show()
    }

    override fun savePreferences(name: String, text: String) {
        TODO("Not yet implemented")
    }

    override fun startLoop() {}
}