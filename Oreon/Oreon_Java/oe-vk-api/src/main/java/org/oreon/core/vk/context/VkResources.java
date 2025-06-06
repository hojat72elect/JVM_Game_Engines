package org.oreon.core.vk.context;

import lombok.Getter;
import lombok.Setter;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.wrapper.descriptor.VkDescriptor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class VkResources {

    private VkFrameBufferObject offScreenFbo;
    private VkFrameBufferObject reflectionFbo;
    private VkFrameBufferObject refractionFbo;
    private VkFrameBufferObject transparencyFbo;

    private Map<VkDescriptorName, VkDescriptor> descriptors = new HashMap<VkDescriptorName, VkDescriptor>();

    public enum VkDescriptorName {
        CAMERA,
        DIRECTIONAL_LIGHT
    }
}
