package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.rt.bro.ValuedEnum;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
public enum /* <name> */MGLDrawableMultisample/* </name> */ implements ValuedEnum {
    /* <values> */
    None(0L), _4X(4L);
    /* </values> */

    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <methods> *//* </methods> */

    private final long n;

    private /* <name> */ MGLDrawableMultisample/* </name> */(long n) {
        this.n = n;
    }

    public static /* <name> */MGLDrawableMultisample/* </name> */ valueOf(long n) {
        for (/* <name> */MGLDrawableMultisample/* </name> */ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException(
                "No constant with value " + n + " found in " + /* <name> */MGLDrawableMultisample/* </name> */.class.getName());
    }

    public long value() {
        return n;
    }
}
