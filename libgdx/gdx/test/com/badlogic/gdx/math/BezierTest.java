package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BezierTest {

    @Parameter()
    public ImportType type;

    // use constructor or setter
    @Parameter(1)
    public boolean useSetter;
    private Bezier<Vector2> bezier;

    @Parameters(name = "imported type {0} use setter {1}")
    public static Collection<Object[]> parameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        for (ImportType type : ImportType.values()) {
            parameters.add(new Object[]{type, true});
            parameters.add(new Object[]{type, false});
        }
        return parameters;
    }

    @Before
    public void setup() {
        bezier = null;
    }

    protected void create(Vector2[] points) {
        if (useSetter) {
            bezier = new Bezier<>();
            if (type == ImportType.LibGDXArrays) {
                bezier.set(new Array<>(points), 0, points.length);
            } else if (type == ImportType.JavaArrays) {
                bezier.set(points, 0, points.length);
            } else {
                bezier.set(points);
            }
        } else {
            if (type == ImportType.LibGDXArrays) {
                bezier = new Bezier<>(new Array<>(points), 0, points.length);
            } else if (type == ImportType.JavaArrays) {
                bezier = new Bezier<>(points, 0, points.length);
            } else {
                bezier = new Bezier<>(points);
            }

        }
    }

    @Test
    public void testLinear2D() {
        create(new Vector2[]{new Vector2(0, 0), new Vector2(1, 1)});

        float len = bezier.approxLength(2);
        float epsilonApproximation = 1e-6f;
        Assert.assertEquals(Math.sqrt(2), len, epsilonApproximation);

        Vector2 d = bezier.derivativeAt(new Vector2(), 0.5f);
        float epsilon = Float.MIN_NORMAL;
        Assert.assertEquals(1, d.x, epsilon);
        Assert.assertEquals(1, d.y, epsilon);

        Vector2 v = bezier.valueAt(new Vector2(), 0.5f);
        Assert.assertEquals(0.5f, v.x, epsilon);
        Assert.assertEquals(0.5f, v.y, epsilon);

        float t = bezier.approximate(new Vector2(.5f, .5f));
        Assert.assertEquals(.5f, t, epsilonApproximation);

        Assert.assertEquals(.5f, t, epsilon);
    }

    private enum ImportType {
        LibGDXArrays, JavaArrays, JavaVarArgs
    }
}
