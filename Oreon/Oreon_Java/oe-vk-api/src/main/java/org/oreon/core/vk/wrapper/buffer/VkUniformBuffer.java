package org.oreon.core.vk.wrapper.buffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.vk.memory.VkBuffer;

import java.nio.ByteBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class VkUniformBuffer extends VkBuffer {

    public VkUniformBuffer(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties,
                           ByteBuffer data) {

        super(device, data.limit(), VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT);
        allocate(memoryProperties,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
        bindBufferMemory();
        mapMemory(data);
    }

    public void updateData(ByteBuffer data) {

        mapMemory(data);
    }

}
