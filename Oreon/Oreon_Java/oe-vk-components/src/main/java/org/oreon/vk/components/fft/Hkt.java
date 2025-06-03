package org.oreon.vk.components.fft;

import lombok.Getter;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.device.VkDeviceBundle;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.pipeline.ShaderModule;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.synchronization.VkSemaphore;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.descriptor.VkDescriptor;
import org.oreon.core.vk.wrapper.image.Image2DDeviceLocal;

import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.*;

public class Hkt {

    private final VkQueue queue;

    @Getter
    private final VkImageView dxCoefficients_imageView;
    @Getter
    private final VkImageView dyCoefficients_imageView;
    @Getter
    private final VkImageView dzCoefficients_imageView;

    @Getter
    private final VkSemaphore signalSemaphore;
    private final float t_delta;
    private final VkImage image_dxCoefficients;
    private final VkImage image_dyCoefficients;
    private final VkImage image_dzCoefficients;
    private final VkPipeline pipeline;
    private final VkDescriptor descriptor;
    private final VkUniformBuffer buffer;
    private final CommandBuffer commandBuffer;
    private final SubmitInfo submitInfo;
    private float t;
    private long systemTime = System.currentTimeMillis();

    public Hkt(VkDeviceBundle deviceBundle, int N, int L, float t_delta,
               VkImageView tilde_h0k, VkImageView tilde_h0minusk) {

        this.t_delta = t_delta;

        VkDevice device = deviceBundle.getLogicalDevice().getHandle();
        VkPhysicalDeviceMemoryProperties memoryProperties = deviceBundle.getPhysicalDevice().getMemoryProperties();
        DescriptorPool descriptorPool = deviceBundle.getLogicalDevice().getDescriptorPool(Thread.currentThread().getId());
        queue = deviceBundle.getLogicalDevice().getComputeQueue();

        image_dxCoefficients = new Image2DDeviceLocal(device, memoryProperties, N, N,
                VK_FORMAT_R32G32B32A32_SFLOAT,
                VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);

        dxCoefficients_imageView = new VkImageView(device,
                VK_FORMAT_R32G32B32A32_SFLOAT, image_dxCoefficients.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);

        image_dyCoefficients = new Image2DDeviceLocal(device, memoryProperties, N, N,
                VK_FORMAT_R32G32B32A32_SFLOAT,
                VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);

        dyCoefficients_imageView = new VkImageView(device,
                VK_FORMAT_R32G32B32A32_SFLOAT, image_dyCoefficients.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);

        image_dzCoefficients = new Image2DDeviceLocal(device, memoryProperties, N, N,
                VK_FORMAT_R32G32B32A32_SFLOAT,
                VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);

        dzCoefficients_imageView = new VkImageView(device,
                VK_FORMAT_R32G32B32A32_SFLOAT, image_dzCoefficients.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);

        ByteBuffer pushConstants = memAlloc(Integer.BYTES * 2);
        pushConstants.putInt(N);
        pushConstants.putInt(L);
        pushConstants.flip();

        ByteBuffer ubo = memAlloc(Float.BYTES);
        ubo.putFloat(t);
        ubo.flip();

        buffer = new VkUniformBuffer(device, memoryProperties, ubo);

        descriptor = new CoefficientsDescriptor(device, descriptorPool, tilde_h0k, tilde_h0minusk);

        pipeline = new VkPipeline(device);
        pipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, Integer.BYTES * 2);
        pipeline.setLayout(descriptor.getDescriptorSetLayout().getHandlePointer());
        pipeline.createComputePipeline(new ShaderModule(device, "shaders/fft/hkt.comp.spv", VK_SHADER_STAGE_COMPUTE_BIT));

        commandBuffer = new ComputeCmdBuffer(device,
                deviceBundle.getLogicalDevice().getComputeCommandPool(Thread.currentThread().getId()).getHandle(),
                pipeline.getHandle(), pipeline.getLayoutHandle(),
                VkUtil.createLongArray(descriptor.getDescriptorSet()), N / 16, N / 16, 1,
                pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);

        signalSemaphore = new VkSemaphore(device);

        submitInfo = new SubmitInfo();
        submitInfo.setCommandBuffers(commandBuffer.getHandlePointer());
        submitInfo.setSignalSemaphores(signalSemaphore.getHandlePointer());
    }

    public void render() {

        t += (System.currentTimeMillis() - systemTime) * t_delta;
        float[] v = {t};
        buffer.mapMemory(BufferUtil.createByteBuffer(v));
        submitInfo.submit(queue);
        systemTime = System.currentTimeMillis();
    }

    public void destroy() {

        dxCoefficients_imageView.destroy();
        dyCoefficients_imageView.destroy();
        dzCoefficients_imageView.destroy();
        image_dxCoefficients.destroy();
        image_dyCoefficients.destroy();
        image_dzCoefficients.destroy();
        signalSemaphore.destroy();
        pipeline.destroy();
        descriptor.destroy();
        buffer.destroy();
        commandBuffer.destroy();
    }

    private class CoefficientsDescriptor extends VkDescriptor {

        public CoefficientsDescriptor(VkDevice device, DescriptorPool descriptorPool,
                                      VkImageView tilde_h0k, VkImageView tilde_h0minusk) {

            descriptorSetLayout = new DescriptorSetLayout(device, 6);
            descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
                    VK_SHADER_STAGE_COMPUTE_BIT);
            descriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
                    VK_SHADER_STAGE_COMPUTE_BIT);
            descriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
                    VK_SHADER_STAGE_COMPUTE_BIT);
            descriptorSetLayout.addLayoutBinding(3, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
                    VK_SHADER_STAGE_COMPUTE_BIT);
            descriptorSetLayout.addLayoutBinding(4, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
                    VK_SHADER_STAGE_COMPUTE_BIT);
            descriptorSetLayout.addLayoutBinding(5, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
                    VK_SHADER_STAGE_COMPUTE_BIT);
            descriptorSetLayout.create();

            descriptorSet = new DescriptorSet(device, descriptorPool.getHandle(),
                    descriptorSetLayout.getHandlePointer());
            descriptorSet.updateDescriptorImageBuffer(dyCoefficients_imageView.getHandle(),
                    VK_IMAGE_LAYOUT_GENERAL, -1,
                    0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
            descriptorSet.updateDescriptorImageBuffer(dxCoefficients_imageView.getHandle(),
                    VK_IMAGE_LAYOUT_GENERAL, -1,
                    1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
            descriptorSet.updateDescriptorImageBuffer(dzCoefficients_imageView.getHandle(),
                    VK_IMAGE_LAYOUT_GENERAL, -1,
                    2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
            descriptorSet.updateDescriptorImageBuffer(tilde_h0k.getHandle(),
                    VK_IMAGE_LAYOUT_GENERAL, -1,
                    3, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
            descriptorSet.updateDescriptorImageBuffer(tilde_h0minusk.getHandle(),
                    VK_IMAGE_LAYOUT_GENERAL, -1,
                    4, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
            descriptorSet.updateDescriptorBuffer(buffer.getHandle(),
                    Float.BYTES, 0, 5, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
        }
    }

}
