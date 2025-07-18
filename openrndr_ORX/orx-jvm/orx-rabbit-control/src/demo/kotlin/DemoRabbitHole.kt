import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.extra.parameters.*
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.Vector4


fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        /**
         * Start RabbitControlServer with a Rabbithole with key 'orxtest'
         * Please visit https://rabbithole.rabbitcontrol.cc for more information.
         *
         * Rabbithole allows you to access your exposed parameter from the internet.
         * To use it with this example just use 'orxtest' as tunnel-name on the main page.
         */
        val rabbit = RabbitControlServer(false, 10000, 8080, "wss://rabbithole.rabbitcontrol.cc/public/rcpserver/connect?key=orxtest")
        val font = loadFont("demo-data/fonts/IBMPlexMono-Regular.ttf", 20.0)
        val settings = object {
            @TextParameter("A string")
            var s: String = "Hello"

            @DoubleParameter("A double", 0.0, 10.0)
            var d: Double = 10.0

            @BooleanParameter("A bool")
            var b: Boolean = true

            @ColorParameter("A fill color")
            var fill = ColorRGBa.PINK

            @ColorParameter("A stroke color")
            var stroke = ColorRGBa.WHITE

            @Vector2Parameter("A vector2")
            var v2 = Vector2(200.0, 200.0)

            @Vector3Parameter("A vector3")
            var v3 = Vector3(200.0, 200.0, 200.0)

            @Vector4Parameter("A vector4")
            var v4 = Vector4(200.0, 200.0, 200.0, 200.0)

            @ActionParameter("Action test")
            fun clicked() {
                println("Clicked from RabbitControl")
            }
        }

        rabbit.add(settings)
        extend(rabbit)
        extend {
            drawer.clear(if (settings.b) ColorRGBa.BLUE else ColorRGBa.BLACK)
            drawer.fontMap = font
            drawer.fill = settings.fill
            drawer.stroke = settings.stroke
            drawer.circle(settings.v2, settings.d)
            drawer.text(settings.s, 10.0, 20.0)
        }
    }
}
