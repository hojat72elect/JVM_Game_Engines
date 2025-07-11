import org.openrndr.application
import org.openrndr.extra.fx.blend.Add
import org.openrndr.extra.fx.blend.ColorBurn
import org.openrndr.extra.fx.blend.ColorDodge
import org.openrndr.extra.fx.blend.Darken
import org.openrndr.extra.fx.blend.DestinationAtop
import org.openrndr.extra.fx.blend.DestinationIn
import org.openrndr.extra.fx.blend.DestinationOut
import org.openrndr.extra.fx.blend.HardLight
import org.openrndr.extra.fx.blend.Lighten
import org.openrndr.extra.fx.blend.Multiply
import org.openrndr.extra.fx.blend.MultiplyContrast
import org.openrndr.extra.fx.blend.Normal
import org.openrndr.extra.fx.blend.Overlay
import org.openrndr.extra.fx.blend.Passthrough
import org.openrndr.extra.fx.blend.Screen
import org.openrndr.extra.fx.blend.SourceAtop
import org.openrndr.extra.fx.blend.SourceIn
import org.openrndr.extra.fx.blend.SourceOut
import org.openrndr.extra.fx.blend.Subtract
import org.openrndr.extra.fx.blend.Xor

fun main() = application {
    program {
        val add = Add()
        val colorBurn = ColorBurn()
        val colorDodge = ColorDodge()
        val darken = Darken()
        val destIn = DestinationIn()
        val destOut = DestinationOut()
        val destAtop = DestinationAtop()
        val hardLight = HardLight()
        val lighten = Lighten()
        val multiply = Multiply()
        val multiplyContrast = MultiplyContrast()
        val normal = Normal()
        val overlay = Overlay()
        val passthrough = Passthrough()
        val screen = Screen()
        val sourceIn = SourceIn()
        val sourceAtop = SourceAtop()
        val sourceOut = SourceOut()
        val subtract = Subtract()
        val xor = Xor()
        application.exit()
    }
}
