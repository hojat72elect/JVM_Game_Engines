package org.oreon.vk.components.atmosphere;

import lombok.Getter;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.light.DirectionalLight;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.context.DeviceManager.DeviceType;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.context.VkResources.VkDescriptorName;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.descriptor.VkDescriptor;

import static org.lwjgl.vulkan.VK10.*;

@Getter
public class VkDirectionalLight extends DirectionalLight {

    private final VkUniformBuffer ubo_light;
    private final DescriptorSet descriptorSet;
    private final DescriptorSetLayout descriptorSetLayout;

    public VkDirectionalLight() {

        super();

        LogicalDevice device = VkContext.getDeviceManager().getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE);
        VkPhysicalDeviceMemoryProperties memoryProperties =
                VkContext.getDeviceManager().getPhysicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).getMemoryProperties();

        ubo_light = new VkUniformBuffer(device.getHandle(), memoryProperties, BufferUtil.createByteBuffer(getFloatBufferLight()));

        descriptorSetLayout = new DescriptorSetLayout(device.getHandle(), 1);
        descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
                VK_SHADER_STAGE_FRAGMENT_BIT | VK_SHADER_STAGE_COMPUTE_BIT);
        descriptorSetLayout.create();

        descriptorSet = new DescriptorSet(device.getHandle(),
                VkContext.getDeviceManager().getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE)
                        .getDescriptorPool(Thread.currentThread().getId()).getHandle(),
                descriptorSetLayout.getHandlePointer());
        descriptorSet.updateDescriptorBuffer(ubo_light.getHandle(), lightBufferSize, 0, 0,
                VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);

        VkContext.getResources().getDescriptors().put(VkDescriptorName.DIRECTIONAL_LIGHT, new VkDescriptor(descriptorSet, descriptorSetLayout));
    }

    public void updateLightUbo() {
        ubo_light.mapMemory(BufferUtil.createByteBuffer(getFloatBufferLight()));
    }

    public void updateMatricesUbo() {

    }
}
