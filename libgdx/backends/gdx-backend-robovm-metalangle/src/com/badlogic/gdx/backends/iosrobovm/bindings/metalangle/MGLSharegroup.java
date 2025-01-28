package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.ObjCRuntime;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.rt.bro.ptr.Ptr;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*/
@NativeClass
/* </annotations> */
/* <visibility> */ public/* </visibility> */ class /* <name> */ MGLSharegroup/* </name> */
        extends /* <extends> */NSObject/* </extends> */
        /* <implements> *//* </implements> */ {

    /* </ptr> */
    /* <bind> */static {
        ObjCRuntime.bind(MGLSharegroup.class);
    }/* </bind> */

    protected MGLSharegroup() {
    }
    /* <constants> *//* </constants> */
    /* <constructors> */

    protected MGLSharegroup(Handle h, long handle) {
        super(h, handle);
    }

    protected MGLSharegroup(SkipInit skipInit) {
        super(skipInit);
    }

    /* <ptr> */public static class MGLSharegroupPtr extends Ptr<MGLSharegroup, MGLSharegroupPtr> {
    }
    /* </constructors> */
    /* <properties> */

    /* </properties> */
    /* <members> *//* </members> */
    /* <methods> */

    /* </methods> */
}
