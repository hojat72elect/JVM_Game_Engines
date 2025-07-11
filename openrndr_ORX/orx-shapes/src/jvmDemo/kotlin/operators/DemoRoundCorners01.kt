package operators

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.shapes.operators.roundCorners
import org.openrndr.extra.shapes.primitives.regularStar

fun main() = application {
    configure {
        width = 720
        height = 720
    }
    program {
        val rp = regularStar(7, 150.0, 300.0, drawer.bounds.center)
        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.fill = null
            drawer.stroke = ColorRGBa.BLACK.opacify(0.2)
            for (i in 1 until 8) {
                val r = rp.roundCorners(i * 15.0).close()
                drawer.contour(r)
            }
        }
    }
}