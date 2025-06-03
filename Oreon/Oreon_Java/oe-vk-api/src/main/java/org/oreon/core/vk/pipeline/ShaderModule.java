package org.oreon.core.vk.pipeline;

import lombok.Getter;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.oreon.core.util.ResourceLoader;
import org.oreon.core.vk.util.VkUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class ShaderModule {

    private final VkDevice device;
    @Getter
    private VkPipelineShaderStageCreateInfo shaderStageInfo;
    @Getter
    private long handle;

    public ShaderModule(VkDevice device, String filePath, int stage) {

        this.device = device;
        createShaderModule(filePath);
        createShaderStage(stage);
    }

    private void createShaderModule(String filePath) {

        ByteBuffer shaderCode = null;
        try {
            shaderCode = ResourceLoader.ioResourceToByteBuffer(filePath, 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int err;
        VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                .pNext(0)
                .pCode(shaderCode)
                .flags(0);
        LongBuffer pShaderModule = memAllocLong(1);
        err = vkCreateShaderModule(device, moduleCreateInfo, null, pShaderModule);
        handle = pShaderModule.get(0);

        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create shader module: " + VkUtil.translateVulkanResult(err));
        }

        memFree(pShaderModule);
        moduleCreateInfo.free();
    }

    private void createShaderStage(int stage) {

        shaderStageInfo = VkPipelineShaderStageCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(stage)
                .module(handle)
                .pName(memUTF8("main"))
                .pSpecializationInfo(null);
    }

    public void destroy() {

        vkDestroyShaderModule(device, handle, null);
        handle = -1;
    }

}
