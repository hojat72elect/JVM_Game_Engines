package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.NotImplemented;
import org.robovm.rt.bro.annotation.ByVal;
/*</imports>*/

/*<javadoc>*/
/*</javadoc>*/
/*<annotations>*//*</annotations>*/
/*<visibility>*/public/* </visibility> */ class /* <name> */ MGLKViewDelegateAdapter/* </name> */
        extends /* <extends> */NSObject/* </extends> */
        /* <implements> */ implements MGLKViewDelegate/* </implements> */ {

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
    @NotImplemented("mglkView:drawInRect:")
    public void draw(MGLKView view, @ByVal CGRect rect) {
    }
    /* </methods> */
}
