package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.rt.bro.ValuedEnum;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
public enum /* <name> */MGLRenderingAPI/* </name> */ implements ValuedEnum {
    /* <values> */
    OpenGLES1(1L), OpenGLES2(2L), OpenGLES3(3L);
    /* </values> */

    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <methods> *//* </methods> */

    private final long n;

    private /* <name> */ MGLRenderingAPI/* </name> */(long n) {
        this.n = n;
    }

    public static /* <name> */MGLRenderingAPI/* </name> */ valueOf(long n) {
        for (/* <name> */MGLRenderingAPI/* </name> */ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException(
                "No constant with value " + n + " found in " + /* <name> */MGLRenderingAPI/* </name> */.class.getName());
    }

    public long value() {
        return n;
    }
}
