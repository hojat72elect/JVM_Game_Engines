package com.badlogic.gdx.backends.iosrobovm.custom;

/*<imports>*/

import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.objc.annotation.Method;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
/*<visibility>*/public/* </visibility> */ interface /* <name> */ UIAccelerometerDelegate/* </name> */
        /* <implements> */ extends NSObjectProtocol/* </implements> */ {

    /* <ptr> */
    /* </ptr> */
    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <properties> */

    /* </properties> */
    /* <methods> */

    /**
     * @since Available in iOS 2.0 and later.
     * @deprecated Deprecated in iOS 5.0.
     */
    @Deprecated
    @Method(selector = "accelerometer:didAccelerate:")
    void didAccelerate(UIAccelerometer accelerometer, UIAcceleration acceleration);
    /* </methods> */
    /* <adapter> */
    /* </adapter> */
}
