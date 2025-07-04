package com.badlogic.gdx.math

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.sqrt
import com.badlogic.gdx.utils.Array as GdxArray

@RunWith(Parameterized::class)
class BezierTest {
    @Parameterized.Parameter
    lateinit var type: ImportType

    // use constructor or setter
    @Parameterized.Parameter(1)
    @JvmField
    var useSetter = false
    private var bezier: Bezier<Vector2>? = null

    @Before
    fun setup() {
        bezier = null
    }

    private fun create(points: Array<Vector2>) {
        if (useSetter) {
            bezier = Bezier()
            when (type) {
                ImportType.LibGDXArrays -> {
                    bezier!![GdxArray<Vector2>(points), 0] = points.size
                }

                ImportType.JavaArrays -> {
                    bezier!![points, 0] = points.size
                }

                else -> {
                    bezier!!.set(*points)
                }
            }
        } else {
            bezier = when (type) {
                ImportType.LibGDXArrays -> {
                    Bezier(GdxArray(points), 0, points.size)
                }

                ImportType.JavaArrays -> {
                    Bezier(points, 0, points.size)
                }

                else -> {
                    Bezier(*points)
                }
            }
        }
    }

    @Test
    fun testLinear2D() {
        create(arrayOf(Vector2(0f, 0f), Vector2(1f, 1f)))

        val len = bezier!!.approxLength(2)
        val epsilonApproximation = 1e-6f
        Assert.assertEquals(sqrt(2.0).toFloat(), len, epsilonApproximation)

        val d = bezier!!.derivativeAt(Vector2(), 0.5f)
        val epsilon = java.lang.Float.MIN_NORMAL
        Assert.assertEquals(1f, d.x, epsilon)
        Assert.assertEquals(1f, d.y, epsilon)

        val v = bezier!!.valueAt(Vector2(), 0.5f)
        Assert.assertEquals(0.5f, v.x, epsilon)
        Assert.assertEquals(0.5f, v.y, epsilon)

        val t = bezier!!.approximate(Vector2(.5f, .5f))
        Assert.assertEquals(.5f, t, epsilonApproximation)

        Assert.assertEquals(.5f, t, epsilon)
    }

    enum class ImportType {
        LibGDXArrays, JavaArrays, JavaVarArgs
    }

    companion object {
        @Parameterized.Parameters(name = "imported type {0} use setter {1}")
        @JvmStatic
        fun parameters(): Collection<Array<Any>> {
            val parameters: MutableCollection<Array<Any>> = ArrayList()
            for (type in ImportType.entries) {
                parameters.add(arrayOf(type, true))
                parameters.add(arrayOf(type, false))
            }
            return parameters
        }
    }
}
