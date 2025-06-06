/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package opencl.templates

import org.lwjgl.generator.*
import opencl.*

val intel_required_subgroup_size = "INTELRequiredSubgroupSize".nativeClassCL("intel_required_subgroup_size", INTEL) {
    IntConstant(
        "DEVICE_SUB_GROUP_SIZES_INTEL"..0x4108
    )

    IntConstant(
        "KERNEL_SPILL_MEM_SIZE_INTEL"..0x4109
    )

    IntConstant(
        "KERNEL_COMPILE_SUB_GROUP_SIZE_INTEL"..0x410A
    )
}