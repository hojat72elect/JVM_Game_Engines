package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.rt.bro.ValuedEnum;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
public enum /* <name> */MGLDrawableDepthFormat/* </name> */ implements ValuedEnum {
    /* <values> */
    None(0L), _16(16L), _24(24L);
    /* </values> */

    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <methods> *//* </methods> */

    private final long n;

    private /* <name> */ MGLDrawableDepthFormat/* </name> */(long n) {
        this.n = n;
    }

    public static /* <name> */MGLDrawableDepthFormat/* </name> */ valueOf(long n) {
        for (/* <name> */MGLDrawableDepthFormat/* </name> */ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException(
                "No constant with value " + n + " found in " + /* <name> */MGLDrawableDepthFormat/* </name> */.class.getName());
    }

    public long value() {
        return n;
    }
}
