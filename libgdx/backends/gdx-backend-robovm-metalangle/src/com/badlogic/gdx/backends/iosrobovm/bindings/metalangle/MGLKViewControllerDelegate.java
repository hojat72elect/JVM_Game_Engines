package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.objc.annotation.Method;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
/*<visibility>*/public/* </visibility> */ interface /* <name> */ MGLKViewControllerDelegate/* </name> */
        /* <implements> */ extends NSObjectProtocol/* </implements> */ {

    /* <ptr> */
    /* </ptr> */
    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <properties> */

    /* </properties> */
    /* <methods> */
    @Method(selector = "mglkViewControllerUpdate:")
    void update(MGLKViewController controller);
    /* </methods> */
    /* <adapter> */
    /* </adapter> */
}
