package org.oreon.core.vk.context;

import lombok.Getter;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.device.PhysicalDevice;
import org.oreon.core.vk.device.VkDeviceBundle;

import java.util.HashMap;

@Getter
public class DeviceManager {

    private final HashMap<DeviceType, VkDeviceBundle> devices;

    public DeviceManager() {

        devices = new HashMap<DeviceType, VkDeviceBundle>();
    }

    public VkDeviceBundle getDeviceBundle(DeviceType deviceType) {

        return devices.get(deviceType);
    }

    public PhysicalDevice getPhysicalDevice(DeviceType deviceType) {

        return devices.get(deviceType).getPhysicalDevice();
    }

    public LogicalDevice getLogicalDevice(DeviceType deviceType) {

        return devices.get(deviceType).getLogicalDevice();
    }

    public void addDevice(DeviceType deviceType, VkDeviceBundle deviceBundle) {

        devices.put(deviceType, deviceBundle);
    }

    public enum DeviceType {

        MAJOR_GRAPHICS_DEVICE,
        SECONDARY_GRAPHICS_DEVICE,
        COMPUTING_DEVICE,
        SLI_DISCRETE_DEVICE0,
        SLI_DISCRETE_DEVICE1
    }
}
