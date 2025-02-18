/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package egl.templates

import egl.*
import org.lwjgl.generator.*

val KHR_lock_surface3 = "KHRLockSurface3".nativeClassEGL("KHR_lock_surface3", postfix = KHR) {
    IntConstant(
        "READ_SURFACE_BIT_KHR"..0x0001,
        "WRITE_SURFACE_BIT_KHR"..0x0002,
        "LOCK_SURFACE_BIT_KHR"..0x0080,
        "OPTIMAL_FORMAT_BIT_KHR"..0x0100,
        "MATCH_FORMAT_KHR"..0x3043,
        "FORMAT_RGB_565_EXACT_KHR"..0x30C0,
        "FORMAT_RGB_565_KHR"..0x30C1,
        "FORMAT_RGBA_8888_EXACT_KHR"..0x30C2,
        "FORMAT_RGBA_8888_KHR"..0x30C3,
        "MAP_PRESERVE_PIXELS_KHR"..0x30C4,
        "LOCK_USAGE_HINT_KHR"..0x30C5,
        "BITMAP_PITCH_KHR"..0x30C7,
        "BITMAP_ORIGIN_KHR"..0x30C8,
        "BITMAP_PIXEL_RED_OFFSET_KHR"..0x30C9,
        "BITMAP_PIXEL_GREEN_OFFSET_KHR"..0x30CA,
        "BITMAP_PIXEL_BLUE_OFFSET_KHR"..0x30CB,
        "BITMAP_PIXEL_ALPHA_OFFSET_KHR"..0x30CC,
        "BITMAP_PIXEL_LUMINANCE_OFFSET_KHR"..0x30CD,
        "BITMAP_PIXEL_SIZE_KHR"..0x3110,
        "BITMAP_POINTER_KHR"..0x30C6,
        "LOWER_LEFT_KHR"..0x30CE,
        "UPPER_LEFT_KHR"..0x30CF
    )

    EGLBoolean(
        "LockSurfaceKHR",

        EGLDisplay("dpy"),
        EGLSurface("surface"),
        nullable..noneTerminated..EGLint.const.p("attrib_list")
    )

    EGLBoolean(
        "UnlockSurfaceKHR",

        EGLDisplay("dpy"),
        EGLSurface("surface")
    )

    EGLBoolean(
        "QuerySurface64KHR",

        EGLDisplay("dpy"),
        EGLSurface("surface"),
        EGLint("attribute"),
        Check(1)..EGLAttribKHR.p("value")
    )
}