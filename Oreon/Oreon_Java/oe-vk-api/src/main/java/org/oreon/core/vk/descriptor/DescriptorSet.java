package org.oreon.core.vk.descriptor;

import lombok.Getter;
import org.lwjgl.vulkan.*;
import org.oreon.core.vk.util.VkUtil;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public class DescriptorSet {

    private final VkDevice device;
    private final long descriptorPool;
    @Getter
    private long handle;

    public DescriptorSet(VkDevice device, long descriptorPool, LongBuffer layouts) {

        this.device = device;
        this.descriptorPool = descriptorPool;

        VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                .descriptorPool(descriptorPool)
                .pSetLayouts(layouts);

        LongBuffer pDescriptorSet = memAllocLong(1);
        int err = vkAllocateDescriptorSets(device, allocateInfo, pDescriptorSet);

        handle = pDescriptorSet.get(0);

        allocateInfo.free();
        memFree(pDescriptorSet);

        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create Descriptor Set: " + VkUtil.translateVulkanResult(err));
        }
    }

    public void updateDescriptorBuffer(long buffer, long range, long offset,
                                       int binding, int descriptorType) {

        VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc(1)
                .buffer(buffer)
                .offset(offset)
                .range(range);

        VkWriteDescriptorSet.Buffer writeDescriptor = VkWriteDescriptorSet.calloc(1)
                .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                .dstSet(handle)
                .dstBinding(binding)
                .dstArrayElement(0)
                .descriptorType(descriptorType)
                .pBufferInfo(bufferInfo);

        vkUpdateDescriptorSets(device, writeDescriptor, null);
    }

    public void updateDescriptorImageBuffer(long imageView, int imageLayout,
                                            long sampler, int binding, int descriptorType) {

        VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.calloc(1)
                .imageLayout(imageLayout)
                .imageView(imageView)
                .sampler(sampler);

        VkWriteDescriptorSet.Buffer writeDescriptor = VkWriteDescriptorSet.calloc(1)
                .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                .dstSet(handle)
                .dstBinding(binding)
                .dstArrayElement(0)
                .descriptorType(descriptorType)
                .pImageInfo(imageInfo);

        vkUpdateDescriptorSets(device, writeDescriptor, null);
    }

    public void destroy() {

        vkFreeDescriptorSets(device, descriptorPool, handle);

        handle = -1;
    }
}
