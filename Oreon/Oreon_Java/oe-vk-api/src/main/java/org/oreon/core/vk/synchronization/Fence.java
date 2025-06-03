package org.oreon.core.vk.synchronization;

import lombok.Getter;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.oreon.core.vk.util.VkUtil;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.*;

public class Fence {

    @Getter
    private final long handle;
    private final LongBuffer pHandle;

    private final VkDevice device;

    public Fence(VkDevice device) {

        this.device = device;

        VkFenceCreateInfo createInfo = VkFenceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                .pNext(0)
                .flags(VK_FENCE_CREATE_SIGNALED_BIT);

        pHandle = memAllocLong(1);
        VkUtil.vkCheckResult(vkCreateFence(device, createInfo, null, pHandle));

        handle = pHandle.get(0);

        createInfo.free();
    }

    public void reset() {

        VkUtil.vkCheckResult(vkResetFences(device, handle));
    }

    public void waitForFence() {

        VkUtil.vkCheckResult(vkWaitForFences(device, pHandle, true, 1000000000L));
    }

    public void destroy() {

        vkDestroyFence(device, handle, null);
    }
}
