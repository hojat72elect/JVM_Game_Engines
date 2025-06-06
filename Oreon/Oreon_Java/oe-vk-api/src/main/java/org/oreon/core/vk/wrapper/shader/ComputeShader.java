package org.oreon.core.vk.wrapper.shader;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.pipeline.ShaderModule;

import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

public class ComputeShader extends ShaderModule {

    public ComputeShader(VkDevice device, String filePath) {
        super(device, filePath, VK_SHADER_STAGE_COMPUTE_BIT);
    }

}
