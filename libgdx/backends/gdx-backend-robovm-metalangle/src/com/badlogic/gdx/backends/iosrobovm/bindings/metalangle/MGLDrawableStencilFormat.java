package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.rt.bro.ValuedEnum;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
public enum /* <name> */MGLDrawableStencilFormat/* </name> */ implements ValuedEnum {
    /* <values> */
    None(0L), _8(8L);
    /* </values> */

    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <methods> *//* </methods> */

    private final long n;

    private /* <name> */ MGLDrawableStencilFormat/* </name> */(long n) {
        this.n = n;
    }

    public static /* <name> */MGLDrawableStencilFormat/* </name> */ valueOf(long n) {
        for (/* <name> */MGLDrawableStencilFormat/* </name> */ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException(
                "No constant with value " + n + " found in " + /* <name> */MGLDrawableStencilFormat/* </name> */.class.getName());
    }

    public long value() {
        return n;
    }
}
