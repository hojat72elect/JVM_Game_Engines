package com.badlogic.gdx.backends.iosrobovm.custom;

/*<imports>*/

import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.ObjCRuntime;
import org.robovm.objc.annotation.Method;
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
/* <visibility> */ public/* </visibility> */ class /* <name> */ UIAccelerometer/* </name> */
        extends /* <extends> */NSObject/* </extends> */
        /* <implements> *//* </implements> */ {

    /* </ptr> */
    /* <bind> */static {
        ObjCRuntime.bind(UIAccelerometer.class);
    }/* </bind> */

    public UIAccelerometer() {
    }
    /* <constants> *//* </constants> */
    /* <constructors> */

    protected UIAccelerometer(SkipInit skipInit) {
        super(skipInit);
    }

    /* </properties> */
    /* <members> *//* </members> */
    /* <methods> */
    @Method(selector = "sharedAccelerometer")
    public static native UIAccelerometer getSharedAccelerometer();

    /* </constructors> */
    /* <properties> */
    @Property(selector = "updateInterval")
    public native double getUpdateInterval();

    @Property(selector = "setUpdateInterval:")
    public native void setUpdateInterval(double v);

    @Property(selector = "delegate")
    public native UIAccelerometerDelegate getDelegate();

    @Property(selector = "setDelegate:", strongRef = true)
    public native void setDelegate(UIAccelerometerDelegate v);

    /* <ptr> */public static class UIAccelerometerPtr extends Ptr<UIAccelerometer, UIAccelerometerPtr> {
    }
    /* </methods> */
}
