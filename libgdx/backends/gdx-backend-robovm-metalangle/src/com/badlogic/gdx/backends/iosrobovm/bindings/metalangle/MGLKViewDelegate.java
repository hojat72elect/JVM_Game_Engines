package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.objc.annotation.Method;
import org.robovm.rt.bro.annotation.ByVal;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
/*<visibility>*/public/* </visibility> */ interface /* <name> */ MGLKViewDelegate/* </name> */
        /* <implements> */ extends NSObjectProtocol/* </implements> */ {

    /* <ptr> */
    /* </ptr> */
    /* <bind> */
    /* </bind> */
    /* <constants> *//* </constants> */
    /* <properties> */

    /* </properties> */
    /* <methods> */
    @Method(selector = "mglkView:drawInRect:")
    void draw(MGLKView view, @ByVal CGRect rect);
    /* </methods> */
    /* <adapter> */
    /* </adapter> */
}
