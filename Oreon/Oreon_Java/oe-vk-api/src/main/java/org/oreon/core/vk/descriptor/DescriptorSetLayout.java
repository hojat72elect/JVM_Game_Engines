package org.oreon.core.vk.descriptor;

import lombok.Getter;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.util.VkUtil;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.*;

public class DescriptorSetLayout {

    private final VkDescriptorSetLayoutBinding.Buffer layoutBindings;
    private final VkDevice device;
    @Getter
    private long handle;
    @Getter
    private LongBuffer handlePointer;

    public DescriptorSetLayout(VkDevice device, int bindingCount) {

        this.device = device;
        layoutBindings = VkDescriptorSetLayoutBinding.calloc(bindingCount);
    }

    public void create() {

        layoutBindings.flip();

        VkDescriptorSetLayoutCreateInfo layoutInfo = VkDescriptorSetLayoutCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
                .pBindings(layoutBindings)
                .flags(0);

        handlePointer = memAllocLong(1);
        int err = vkCreateDescriptorSetLayout(device, layoutInfo, null, handlePointer);

        handle = handlePointer.get(0);

        layoutBindings.clear();
        layoutBindings.free();
        layoutInfo.free();

        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create DescriptorSetLayout: " + VkUtil.translateVulkanResult(err));
        }
    }

    public void addLayoutBinding(int binding, int type, int stageflags) {

        VkDescriptorSetLayoutBinding layoutBinding = VkDescriptorSetLayoutBinding.calloc()
                .binding(binding)
                .descriptorType(type)
                .descriptorCount(1)
                .stageFlags(stageflags)
                .pImmutableSamplers(null);

        layoutBindings.put(layoutBinding);
    }

    public void destroy() {

        vkDestroyDescriptorSetLayout(device, handle, null);

        handle = -1;
    }
}
