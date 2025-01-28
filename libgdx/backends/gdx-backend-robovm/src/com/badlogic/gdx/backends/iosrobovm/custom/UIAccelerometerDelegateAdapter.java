package com.badlogic.gdx.backends.iosrobovm.custom;

/*<imports>*/

import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.NotImplemented;
/*</imports>*/

/*<javadoc>*/
/*</javadoc>*/
/*<annotations>*//*</annotations>*/
/*<visibility>*/public/* </visibility> */ class /* <name> */ UIAccelerometerDelegateAdapter/* </name> */
        extends /* <extends> */NSObject/* </extends> */
        /* <implements> */ implements UIAccelerometerDelegate/* </implements> */ {

    /* <ptr> */
    /* </ptr> */
    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <constructors> *//* </constructors> */
    /* <properties> */

    /* </properties> */
    /* <members> *//* </members> */
    /* <methods> */

    /**
     * @since Available in iOS 2.0 and later.
     * @deprecated Deprecated in iOS 5.0.
     */
    @Deprecated
    @NotImplemented("accelerometer:didAccelerate:")
    public void didAccelerate(UIAccelerometer accelerometer, UIAcceleration acceleration) {
        throw new UnsupportedOperationException();
    }
    /* </methods> */
}
