package com.badlogic.gdx.backends.iosrobovm.custom;

/*<imports>*/

import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.ObjCRuntime;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.objc.annotation.Property;
import org.robovm.rt.bro.annotation.Library;
import org.robovm.rt.bro.ptr.Ptr;
/*</imports>*/

/*<javadoc>*/

/**
 * @since Available in iOS 2.0 and later.
 * @deprecated Deprecated in iOS 5.0.
 */
@Deprecated
/* </javadoc> */
/* <annotations> */
@Library("UIKit")
@NativeClass
/* </annotations> */
/* <visibility> */ public/* </visibility> */ class /* <name> */ UIAcceleration/* </name> */
        extends /* <extends> */NSObject/* </extends> */
        /* <implements> *//* </implements> */ {

    /* </ptr> */
    /* <bind> */static {
        ObjCRuntime.bind(UIAcceleration.class);
    }/* </bind> */

    public UIAcceleration() {
    }
    /* <constants> *//* </constants> */
    /* <constructors> */

    protected UIAcceleration(SkipInit skipInit) {
        super(skipInit);
    }

    /* </constructors> */
    /* <properties> */
    @Property(selector = "timestamp")
    public native double getTimestamp();

    @Property(selector = "x")
    public native double getX();

    @Property(selector = "y")
    public native double getY();

    @Property(selector = "z")
    public native double getZ();

    /* <ptr> */public static class UIAccelerationPtr extends Ptr<UIAcceleration, UIAccelerationPtr> {
    }
    /* </properties> */
    /* <members> *//* </members> */
    /* <methods> */

    /* </methods> */
}
