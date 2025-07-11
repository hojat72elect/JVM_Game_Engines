import org.openrndr.application
import org.openrndr.draw.RenderTarget

/**
 * Demonstration of querying the active render target
 */
fun main() = application {
    configure {
        windowResizable = true
    }

    program {
        extend {
            RenderTarget.active.let {
                println("content scale: ${it.contentScale}")
                println("multisample: ${it.multisample}")
                println("width: ${it.width}, height: ${it.height}")
            }
        }
    }
}