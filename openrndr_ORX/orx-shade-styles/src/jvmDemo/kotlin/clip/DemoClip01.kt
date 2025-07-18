package clip

import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extra.imageFit.imageFit
import org.openrndr.extra.shadestyles.fills.FillFit
import org.openrndr.extra.shadestyles.fills.clip.clip
import org.openrndr.extra.shapes.primitives.grid
import org.openrndr.math.Vector2
import org.openrndr.math.transforms.transform
import kotlin.math.PI
import kotlin.math.cos

fun main() = application {
    configure {
        width = 720
        height = 720
    }

    program {
        var gf = 0.0
        mouse.buttonDown.listen {
            gf = 0.5 - gf
        }

        val image = loadImage("demo-data/images/image-001.png")
        extend {

            val grid = drawer.bounds.grid(3, 3)
            for ((index, cell) in grid.flatten().withIndex()) {

                drawer.shadeStyle = clip {
                    clipFit = FillFit.CONTAIN
                    feather = gf

                    clipTransform = transform {
                        translate(Vector2(0.5, 0.5))
                        rotate(36.0 * seconds)
                        translate(Vector2(-0.5, -0.5))
                    }

                    star {
                        radius = 0.5
                        center = Vector2(0.5, 0.5)
                        sharpness = cos(2 * PI * index / 9.0 + seconds) * 0.25 + 0.5
                        sides = 24
                    }
                }
                drawer.imageFit(image, cell)
            }
        }
    }
}