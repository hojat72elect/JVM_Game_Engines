package org.oreon.core.vk.framebuffer;

import lombok.Getter;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.oreon.core.vk.util.VkUtil;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public class VkFrameBuffer {

    @Getter
    private final long handle;

    private final VkDevice device;

    public VkFrameBuffer(VkDevice device, int width, int height, int layers,
                         LongBuffer pAttachments, long renderPass) {

        this.device = device;

        VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                .pAttachments(pAttachments)
                .flags(0)
                .height(height)
                .width(width)
                .layers(layers)
                .pNext(0)
                .renderPass(renderPass);

        LongBuffer pFramebuffer = memAllocLong(1);
        int err = vkCreateFramebuffer(device, framebufferInfo, null, pFramebuffer);

        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create framebuffer: " + VkUtil.translateVulkanResult(err));
        }

        handle = pFramebuffer.get(0);

        framebufferInfo.free();
        memFree(pFramebuffer);
        memFree(pAttachments);
    }

    public void destroy() {

        vkDestroyFramebuffer(device, handle, null);
    }

}
