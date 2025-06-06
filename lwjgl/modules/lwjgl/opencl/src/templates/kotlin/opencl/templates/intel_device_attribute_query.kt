/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package opencl.templates

import org.lwjgl.generator.*
import opencl.*

val intel_device_attribute_query = "INTELDeviceAttributeQuery".nativeClassCL("intel_device_attribute_query", INTEL) {
    IntConstant(
        "DEVICE_IP_VERSION_INTEL"..0x4250,
        "DEVICE_ID_INTEL"..0x4251,
        "DEVICE_NUM_SLICES_INTEL"..0x4252,
        "DEVICE_NUM_SUB_SLICES_PER_SLICE_INTEL"..0x4253,
        "DEVICE_NUM_EUS_PER_SUB_SLICE_INTEL"..0x4254,
        "DEVICE_NUM_THREADS_PER_EU_INTEL"..0x4255,
        "DEVICE_FEATURE_CAPABILITIES_INTEL"..0x4256
    )

    IntConstant(
        "DEVICE_FEATURE_FLAG_DP4A_INTEL".."1 << 0",
        "DEVICE_FEATURE_FLAG_DPAS_INTEL".."1 << 1"
    )
}