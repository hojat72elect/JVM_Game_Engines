import org.openrndr.extra.shapes.rectify.rectified
import org.openrndr.shape.Circle
import org.openrndr.shape.Ellipse
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestRectifiedContour {

    @Test
    fun testEquidistantPointsWithT() {
        val c = Circle(200.0, 200.0, 100.0).contour
        val ep = (c.equidistantPositionsWithT(c.length.toInt()))
        assertEquals(0.0, (ep[0].second))
    }


    @Test
    fun testInverse() {
        val c = Ellipse(40.0, 40.0, 40.0, 80.0).contour.sub(0.0, 0.333)
        val r = c.rectified()
        val rt = r.rectify(0.125)
        val ri = r.inverseRectify(rt)

        assertTrue(abs(ri - 0.125) < 1E-5)
    }
}