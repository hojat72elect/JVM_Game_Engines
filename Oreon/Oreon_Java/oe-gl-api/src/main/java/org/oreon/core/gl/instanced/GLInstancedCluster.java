package org.oreon.core.gl.instanced;

import lombok.Getter;
import lombok.Setter;
import org.oreon.core.gl.memory.GLUniformBuffer;
import org.oreon.core.instanced.InstancedCluster;

@Getter
@Setter
public class GLInstancedCluster extends InstancedCluster {

    private GLUniformBuffer modelMatricesBuffer;
    private GLUniformBuffer worldMatricesBuffer;
}
