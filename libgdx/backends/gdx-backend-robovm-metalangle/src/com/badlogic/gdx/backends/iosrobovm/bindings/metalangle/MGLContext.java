package com.badlogic.gdx.backends.iosrobovm.bindings.metalangle;

/*<imports>*/

import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.ObjCRuntime;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.objc.annotation.Property;
import org.robovm.rt.bro.annotation.Pointer;
import org.robovm.rt.bro.ptr.Ptr;
import org.robovm.rt.bro.ptr.VoidPtr;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*/
@NativeClass
/* </annotations> */
/* <visibility> */ public/* </visibility> */ class /* <name> */ MGLContext/* </name> */
        extends /* <extends> */NSObject/* </extends> */
        /* <implements> *//* </implements> */ {

    /* </ptr> */
    /* <bind> */static {
        ObjCRuntime.bind(MGLContext.class);
    }/* </bind> */

    protected MGLContext() {
    }
    /* <constants> *//* </constants> */
    /* <constructors> */

    protected MGLContext(Handle h, long handle) {
        super(h, handle);
    }

    protected MGLContext(SkipInit skipInit) {
        super(skipInit);
    }

    @Method(selector = "initWithAPI:")
    public MGLContext(MGLRenderingAPI api) {
        super((SkipInit) null);
        initObject(initWithAPI(api));
    }

    @Method(selector = "initWithAPI:sharegroup:")
    public MGLContext(MGLRenderingAPI api, MGLSharegroup sharegroup) {
        super((SkipInit) null);
        initObject(initWithAPISharegroup(api, sharegroup));
    }

    @Method(selector = "currentContext")
    public static native MGLContext currentContext();

    @Method(selector = "currentLayer")
    public static native MGLLayer currentLayer();

    @Method(selector = "setCurrentContext:")
    public static native boolean setCurrentContext(MGLContext context);

    @Method(selector = "setCurrentContext:forLayer:")
    public static native boolean setCurrentContextForLayer(MGLContext context, MGLLayer layer);

    /* </constructors> */
    /* <properties> */
    @Property(selector = "API")
    public native MGLRenderingAPI getAPI();

    @Property(selector = "sharegroup")
    public native MGLSharegroup getSharegroup();

    @Property(selector = "eglDisplay")
    public native VoidPtr getEglDisplay();

    /* </properties> */
    /* <members> *//* </members> */
    /* <methods> */
    @Method(selector = "initWithAPI:")
    protected native @Pointer long initWithAPI(MGLRenderingAPI api);

    @Method(selector = "initWithAPI:sharegroup:")
    protected native @Pointer long initWithAPISharegroup(MGLRenderingAPI api, MGLSharegroup sharegroup);

    @Method(selector = "present:")
    public native boolean present(MGLLayer layer);

    /* <ptr> */public static class MGLContextPtr extends Ptr<MGLContext, MGLContextPtr> {
    }
    /* </methods> */
}
