package org.oreon.core.vk.wrapper.descriptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VkDescriptor {

    protected DescriptorSet descriptorSet;
    protected DescriptorSetLayout descriptorSetLayout;

    public void destroy() {

        descriptorSet.destroy();
        descriptorSetLayout.destroy();
    }
}
