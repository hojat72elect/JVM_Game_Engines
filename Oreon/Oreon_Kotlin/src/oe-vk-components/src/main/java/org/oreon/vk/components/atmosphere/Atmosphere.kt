package org.oreon.vk.components.atmosphere

import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.VK10
import org.oreon.core.context.BaseContext
import org.oreon.core.model.Vertex.VertexLayout
import org.oreon.core.scenegraph.NodeComponentType
import org.oreon.core.scenegraph.Renderable
import org.oreon.core.util.BufferUtil.createByteBuffer
import org.oreon.core.util.Constants
import org.oreon.core.util.ProceduralTexturing.dome
import org.oreon.core.vk.command.CommandBuffer
import org.oreon.core.vk.context.DeviceManager.DeviceType
import org.oreon.core.vk.context.VkContext.deviceManager
import org.oreon.core.vk.context.VkContext.getCamera
import org.oreon.core.vk.context.VkContext.resources
import org.oreon.core.vk.context.VkResources.VkDescriptorName
import org.oreon.core.vk.descriptor.DescriptorSet
import org.oreon.core.vk.descriptor.DescriptorSetLayout
import org.oreon.core.vk.pipeline.ShaderModule
import org.oreon.core.vk.pipeline.ShaderPipeline
import org.oreon.core.vk.pipeline.VkPipeline
import org.oreon.core.vk.pipeline.VkVertexInput
import org.oreon.core.vk.scenegraph.VkMeshData
import org.oreon.core.vk.scenegraph.VkRenderInfo
import org.oreon.core.vk.util.VkAssimpModelLoader.loadModel
import org.oreon.core.vk.util.VkUtil.createLongArray
import org.oreon.core.vk.util.VkUtil.createLongBuffer
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper.createDeviceLocalBuffer
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer
import org.oreon.core.vk.wrapper.command.SecondaryDrawIndexedCmdBuffer
import org.oreon.core.vk.wrapper.pipeline.GraphicsPipeline
import java.util.*

class Atmosphere : Renderable() {
    private val uniformBuffer: VkUniformBuffer
    override fun update() {
        super.update()
        uniformBuffer.mapMemory(createByteBuffer(worldTransform.worldMatrix))
    }

    override fun shutdown() {
        super.shutdown()
        uniformBuffer.destroy()
    }

    init {
        val device = deviceManager.getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE)
        val memoryProperties = deviceManager.getPhysicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).memoryProperties
        worldTransform.setLocalScaling(Constants.ZFAR * 0.5f, Constants.ZFAR * 0.5f, Constants.ZFAR * 0.5f)
        val mesh = loadModel("models/obj/dome", "dome.obj")[0].mesh
        dome(mesh!!)
        val ubo = MemoryUtil.memAlloc(java.lang.Float.BYTES * 16)
        ubo.put(createByteBuffer(worldTransform.worldMatrix))
        ubo.flip()
        uniformBuffer = VkUniformBuffer(device.handle, memoryProperties, ubo)
        val vertexShader = ShaderModule(device.handle,
                "shaders/atmosphere/atmosphere.vert.spv", VK10.VK_SHADER_STAGE_VERTEX_BIT)
        val graphicsShaderPipeline = ShaderPipeline(device.handle)
        graphicsShaderPipeline.addShaderModule(vertexShader)
        graphicsShaderPipeline.createFragmentShader(if (BaseContext.config.AtmosphericScatteringEnable) "shaders/atmosphere/atmospheric_scattering.frag.spv" else "shaders/atmosphere/atmosphere.frag.spv")
        graphicsShaderPipeline.createShaderPipeline()
        val reflectionShaderPipeline = ShaderPipeline(device.handle)
        reflectionShaderPipeline.addShaderModule(vertexShader)
        reflectionShaderPipeline.createFragmentShader("shaders/atmosphere/atmospheric_scattering_reflection.frag.spv")
        reflectionShaderPipeline.createShaderPipeline()
        val descriptorSets: MutableList<DescriptorSet> = ArrayList()
        val descriptorSetLayouts: MutableList<DescriptorSetLayout> = ArrayList()
        val descriptorSetLayout = DescriptorSetLayout(device.handle, 1)
        descriptorSetLayout.addLayoutBinding(0, VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
                VK10.VK_SHADER_STAGE_VERTEX_BIT)
        descriptorSetLayout.create()
        val descriptorSet = DescriptorSet(device.handle,
                device.getDescriptorPool(Thread.currentThread().id)!!.handle,
                descriptorSetLayout.handlePointer)
        descriptorSet.updateDescriptorBuffer(uniformBuffer.handle,
                ubo.limit().toLong(), 0, 0, VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
        descriptorSets.add(getCamera().descriptorSet)
        descriptorSets.add(descriptorSet)
        descriptorSets.add(resources.descriptors[VkDescriptorName.DIRECTIONAL_LIGHT]!!.descriptorSet)
        descriptorSetLayouts.add(getCamera().descriptorSetLayout)
        descriptorSetLayouts.add(descriptorSetLayout)
        descriptorSetLayouts.add(resources.descriptors[VkDescriptorName.DIRECTIONAL_LIGHT]!!.descriptorSetLayout)
        val vertexInput = VkVertexInput(VertexLayout.POS)
        val vertexBuffer = createByteBuffer(mesh.vertices, VertexLayout.POS)
        val indexBuffer = createByteBuffer(*mesh.indices)
        val pushConstantsRange = java.lang.Float.BYTES * 20 + Integer.BYTES * 3
        val pushConstants = MemoryUtil.memAlloc(pushConstantsRange)
        pushConstants.put(createByteBuffer(getCamera().projectionMatrix))
        pushConstants.putFloat(BaseContext.config.sunRadius)
        pushConstants.putInt(BaseContext.config.frameWidth)
        pushConstants.putInt(BaseContext.config.frameHeight)
        pushConstants.putInt(0)
        pushConstants.putFloat(BaseContext.config.atmosphereBloomFactor)
        pushConstants.putFloat(BaseContext.config.horizonVerticalShift)
        pushConstants.putFloat(BaseContext.config.horizonVerticalShift * 2)
        pushConstants.flip()
        val graphicsPipeline: VkPipeline = GraphicsPipeline(device.handle,
                graphicsShaderPipeline, vertexInput, VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
                createLongBuffer(descriptorSetLayouts),
                BaseContext.config.frameWidth,
                BaseContext.config.frameHeight,
                resources.offScreenFbo!!.renderPass!!.handle,
                resources.offScreenFbo!!.colorAttachmentCount,
                BaseContext.config.multisampling_sampleCount,
                pushConstantsRange, VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
        val vertexBufferObject = createDeviceLocalBuffer(
                device.handle, memoryProperties,
                device.getTransferCommandPool(Thread.currentThread().id)!!.handle,
                device.transferQueue,
                vertexBuffer, VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
        val indexBufferObject = createDeviceLocalBuffer(
                device.handle, memoryProperties,
                device.getTransferCommandPool(Thread.currentThread().id)!!.handle,
                device.transferQueue,
                indexBuffer, VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT)
        val mainCommandBuffer: CommandBuffer = SecondaryDrawIndexedCmdBuffer(
                device.handle,
                device.getGraphicsCommandPool(Thread.currentThread().id)!!.handle,
                graphicsPipeline.handle, graphicsPipeline.layoutHandle,
                resources.offScreenFbo!!.frameBuffer!!.handle,
                resources.offScreenFbo!!.renderPass!!.handle,
                0,
                createLongArray(descriptorSets),
                vertexBufferObject.handle,
                indexBufferObject.handle,
                mesh.indices.size,
                pushConstants, VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
        val meshData: VkMeshData = VkMeshData(vertexBufferObject = vertexBufferObject,
                vertexBuffer = vertexBuffer, indexBufferObject = indexBufferObject, indexBuffer = indexBuffer)
        val mainRenderInfo: VkRenderInfo = VkRenderInfo(commandBuffer = mainCommandBuffer,
                pipeline = graphicsPipeline, descriptorSets = descriptorSets,
                descriptorSetLayouts = descriptorSetLayouts)
        addComponent(NodeComponentType.MESH_DATA, meshData)
        addComponent(NodeComponentType.MAIN_RENDERINFO, mainRenderInfo)
        addComponent(NodeComponentType.WIREFRAME_RENDERINFO, mainRenderInfo)
        if (resources.reflectionFbo != null) {
            val reflectionPipeline: VkPipeline = GraphicsPipeline(device.handle,
                    reflectionShaderPipeline, vertexInput, VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
                    createLongBuffer(descriptorSetLayouts),
                    resources.reflectionFbo!!.width,
                    resources.reflectionFbo!!.height,
                    resources.reflectionFbo!!.renderPass!!.handle,
                    resources.reflectionFbo!!.colorAttachmentCount, 1,
                    pushConstantsRange, VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
            val reflectionCommandBuffer: CommandBuffer = SecondaryDrawIndexedCmdBuffer(
                    device.handle,
                    device.getGraphicsCommandPool(Thread.currentThread().id)!!.handle,
                    reflectionPipeline.handle, reflectionPipeline.layoutHandle,
                    resources.reflectionFbo!!.frameBuffer!!.handle,
                    resources.reflectionFbo!!.renderPass!!.handle,
                    0,
                    createLongArray(descriptorSets),
                    vertexBufferObject.handle,
                    indexBufferObject.handle,
                    mesh.indices.size,
                    pushConstants, VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
            val reflectionRenderInfo: VkRenderInfo = VkRenderInfo(commandBuffer = reflectionCommandBuffer,
                    pipeline = reflectionPipeline)
            addComponent(NodeComponentType.REFLECTION_RENDERINFO, reflectionRenderInfo)
        }
        graphicsShaderPipeline.destroy()
        reflectionShaderPipeline.destroy()
    }
}