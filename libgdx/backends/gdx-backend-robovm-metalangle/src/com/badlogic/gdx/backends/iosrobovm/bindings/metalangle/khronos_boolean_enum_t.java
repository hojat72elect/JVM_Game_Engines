package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.rt.bro.ValuedEnum;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
public enum /* <name> */khronos_boolean_enum_t/* </name> */ implements ValuedEnum {
    /* <values> */
    FALSE(0L), TRUE(1L), BOOLEAN_ENUM_FORCE_SIZE(2147483647L);
    /* </values> */

    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <methods> *//* </methods> */

    private final long n;

    private /* <name> */ khronos_boolean_enum_t/* </name> */(long n) {
        this.n = n;
    }

    public static /* <name> */khronos_boolean_enum_t/* </name> */ valueOf(long n) {
        for (/* <name> */khronos_boolean_enum_t/* </name> */ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException(
                "No constant with value " + n + " found in " + /* <name> */khronos_boolean_enum_t/* </name> */.class.getName());
    }

    public long value() {
        return n;
    }
}
