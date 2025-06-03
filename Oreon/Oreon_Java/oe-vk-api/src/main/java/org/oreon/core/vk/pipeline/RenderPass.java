package org.oreon.core.vk.pipeline;

import lombok.Getter;
import org.lwjgl.vulkan.*;
import org.oreon.core.vk.util.VkUtil;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public class RenderPass {

    private final VkDevice device;
    private final List<VkAttachmentReference> colorReferences = new ArrayList<>();
    private final List<VkAttachmentDescription> attachmentDescriptions = new ArrayList<>();
    private final List<VkSubpassDependency> subpassDependendies = new ArrayList<>();
    private final List<VkSubpassDescription> subpassDescriptions = new ArrayList<>();
    private VkAttachmentReference depthReference;
    @Getter
    private long handle;
    @Getter
    private int attachmentCount;

    public RenderPass(VkDevice device) {

        this.device = device;
    }

    public void createRenderPass() {

        VkAttachmentDescription.Buffer attachments =
                VkAttachmentDescription.calloc(attachmentDescriptions.size());
        for (VkAttachmentDescription attachment : attachmentDescriptions) {
            attachments.put(attachment);
        }
        attachments.flip();

        VkSubpassDescription.Buffer subpasses =
                VkSubpassDescription.calloc(subpassDescriptions.size());
        for (VkSubpassDescription subpass : subpassDescriptions) {
            subpasses.put(subpass);
        }
        subpasses.flip();

        VkSubpassDependency.Buffer dependencies =
                VkSubpassDependency.calloc(subpassDependendies.size());
        for (VkSubpassDependency dependency : subpassDependendies) {
            dependencies.put(dependency);
        }
        dependencies.flip();

        VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                .pNext(0)
                .pAttachments(attachments)
                .pSubpasses(subpasses)
                .pDependencies(dependencies);

        LongBuffer pRenderPass = memAllocLong(1);
        int err = vkCreateRenderPass(device, renderPassInfo, null, pRenderPass);

        handle = pRenderPass.get(0);

        for (VkAttachmentDescription attachment : attachmentDescriptions) {
            attachment.free();
        }

        for (VkSubpassDescription subpass : subpassDescriptions) {
            subpass.free();
        }

        for (VkSubpassDependency dependency : subpassDependendies) {
            dependency.free();
        }

        attachmentCount = attachments.limit();

        memFree(pRenderPass);
        renderPassInfo.free();
        subpasses.free();
        dependencies.free();
        attachments.free();

        if (depthReference != null) {
            depthReference.free();
        }

        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create render pass: " + VkUtil.translateVulkanResult(err));
        }
    }

    public void addColorAttachment(int location, int layout, int format,
                                   int samples, int initialLayout, int finalLayout) {

        addAttachmentDescription(format, samples, initialLayout, finalLayout);
        addColorAttachmentReference(location, layout);
    }

    public void addDepthAttachment(int location, int layout, int format,
                                   int samples, int initialLayout, int finalLayout) {

        addAttachmentDescription(format, samples, initialLayout, finalLayout);
        addDepthAttachmentReference(location, layout);
    }

    public void addSubpassDependency(int srcSubpass, int dstSubpass,
                                     int srcStageMask, int dstStageMask, int srcAccessMask,
                                     int dstAccessMask, int dependencyFlags) {

        VkSubpassDependency dependencies = VkSubpassDependency.calloc()
                .srcSubpass(srcSubpass)
                .dstSubpass(dstSubpass)
                .srcStageMask(srcStageMask)
                .dstStageMask(dstStageMask)
                .srcAccessMask(srcStageMask)
                .dstAccessMask(dstStageMask)
                .dependencyFlags(dependencyFlags);

        subpassDependendies.add(dependencies);
    }

    private void addColorAttachmentReference(int location, int layout) {

        VkAttachmentReference attachmentReference = VkAttachmentReference.calloc()
                .attachment(location)
                .layout(layout);

        colorReferences.add(attachmentReference);
    }

    private void addDepthAttachmentReference(int location, int layout) {

        depthReference = VkAttachmentReference.calloc()
                .attachment(location)
                .layout(layout);
    }

    private void addAttachmentDescription(int format, int samples,
                                          int initialLayout, int finalLayout) {

        VkAttachmentDescription attachment = VkAttachmentDescription.calloc()
                .format(format)
                .samples(VkUtil.getSampleCountBit(samples))
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .initialLayout(initialLayout)
                .finalLayout(finalLayout);

        attachmentDescriptions.add(attachment);
    }

    public void createSubpass() {

        VkAttachmentReference.Buffer attachmentReferenceBuffer =
                VkAttachmentReference.calloc(colorReferences.size());

        for (VkAttachmentReference reference : colorReferences) {
            attachmentReferenceBuffer.put(reference);
        }

        attachmentReferenceBuffer.flip();

        VkSubpassDescription subpass = VkSubpassDescription.calloc()
                .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                .flags(0)
                .pInputAttachments(null)
                .colorAttachmentCount(attachmentReferenceBuffer.limit())
                .pColorAttachments(attachmentReferenceBuffer)
                .pResolveAttachments(null)
                .pDepthStencilAttachment(depthReference)
                .pPreserveAttachments(null);

        for (VkAttachmentReference reference : colorReferences) {
            reference.free();
        }

        colorReferences.clear();
        subpassDescriptions.add(subpass);
    }

    public void destroy() {

        vkDestroyRenderPass(device, handle, null);
    }

}
