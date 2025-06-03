package org.oreon.core.vk.device;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.*;
import org.oreon.core.vk.queue.QueueFamilies;
import org.oreon.core.vk.util.VkUtil;

import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

@Log4j2
@Getter
public class PhysicalDevice {

    private final VkPhysicalDevice handle;
    private final VkPhysicalDeviceProperties properties;
    private final VkPhysicalDeviceFeatures features;
    private final VkPhysicalDeviceMemoryProperties memoryProperties;
    private final QueueFamilies queueFamilies;
    private final SurfaceProperties swapChainCapabilities;
    private final List<String> supportedExtensionNames;

    public PhysicalDevice(VkInstance vkInstance, long surface) {

        IntBuffer pPhysicalDeviceCount = memAllocInt(1);
        int err = vkEnumeratePhysicalDevices(vkInstance, pPhysicalDeviceCount, null);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get number of physical devices: " + VkUtil.translateVulkanResult(err));
        }

        log.info("Available Physical Devices: " + pPhysicalDeviceCount.get(0));

        PointerBuffer pPhysicalDevices = memAllocPointer(pPhysicalDeviceCount.get(0));
        err = vkEnumeratePhysicalDevices(vkInstance, pPhysicalDeviceCount, pPhysicalDevices);
        long physicalDevice = pPhysicalDevices.get(0);

        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get physical devices: " + VkUtil.translateVulkanResult(err));
        }

        memFree(pPhysicalDeviceCount);
        memFree(pPhysicalDevices);

        handle = new VkPhysicalDevice(physicalDevice, vkInstance);
        queueFamilies = new QueueFamilies(handle, surface);
        swapChainCapabilities = new SurfaceProperties(handle, surface);
        supportedExtensionNames = DeviceCapabilities.getPhysicalDeviceExtensionNamesSupport(handle);
        memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
        vkGetPhysicalDeviceMemoryProperties(handle, memoryProperties);

        properties = DeviceCapabilities.checkPhysicalDeviceProperties(handle);
        features = DeviceCapabilities.checkPhysicalDeviceFeatures(handle);

//        log.info(properties.apiVersion());
//        log.info(properties.driverVersion());
//        log.info(properties.vendorID());
//        log.info(properties.deviceID());
//        log.info(properties.deviceType());
//        log.info(properties.deviceNameString());
    }

    public void checkDeviceExtensionsSupport(PointerBuffer ppEnabledExtensionNames) {

        for (int i = 0; i < ppEnabledExtensionNames.limit(); i++) {
            if (!supportedExtensionNames.contains(ppEnabledExtensionNames.getStringUTF8())) {
                throw new AssertionError("Extension " + ppEnabledExtensionNames.getStringUTF8() + " not supported");
            }
        }

        ppEnabledExtensionNames.flip();
    }

    public void checkDeviceFormatAndColorSpaceSupport(int format, int colorSpace) {

        swapChainCapabilities.checkVkSurfaceFormatKHRSupport(format, colorSpace);
    }

    public boolean checkDevicePresentationModeSupport(int presentMode) {

        return swapChainCapabilities.checkPresentationModeSupport(presentMode);
    }

    public int getDeviceMinImageCount4TripleBuffering() {

        return swapChainCapabilities.getMinImageCount4TripleBuffering();
    }

}
