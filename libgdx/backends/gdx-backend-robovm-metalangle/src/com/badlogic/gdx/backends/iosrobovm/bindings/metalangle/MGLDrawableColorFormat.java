package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.rt.bro.ValuedEnum;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
public enum /* <name> */MGLDrawableColorFormat/* </name> */ implements ValuedEnum {
    /* <values> */
    RGBA8888(32L), SRGBA8888(-32L), RGB565(16L);
    /* </values> */

    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <methods> *//* </methods> */

    private final long n;

    private /* <name> */ MGLDrawableColorFormat/* </name> */(long n) {
        this.n = n;
    }

    public static /* <name> */MGLDrawableColorFormat/* </name> */ valueOf(long n) {
        for (/* <name> */MGLDrawableColorFormat/* </name> */ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException(
                "No constant with value " + n + " found in " + /* <name> */MGLDrawableColorFormat/* </name> */.class.getName());
    }

    public long value() {
        return n;
    }
}
