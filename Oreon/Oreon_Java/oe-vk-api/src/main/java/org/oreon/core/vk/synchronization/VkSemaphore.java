package org.oreon.core.vk.synchronization;

import lombok.Getter;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.oreon.core.vk.util.VkUtil;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.*;

public class VkSemaphore {

    @Getter
    private final long handle;
    @Getter
    private final LongBuffer handlePointer;

    private final VkDevice device;

    public VkSemaphore(VkDevice device) {

        this.device = device;

        VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO)
                .pNext(0)
                .flags(0);

        handlePointer = memAllocLong(1);

        int err = vkCreateSemaphore(device, semaphoreCreateInfo, null, handlePointer);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create semaphore: " + VkUtil.translateVulkanResult(err));
        }

        handle = handlePointer.get(0);

        semaphoreCreateInfo.free();
    }

    public void destroy() {

        vkDestroySemaphore(device, handle, null);
    }

}
