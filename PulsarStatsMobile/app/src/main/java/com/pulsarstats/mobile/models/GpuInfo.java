package com.pulsarstats.mobile.models;

public class GpuInfo {
    private double usagePercent;
    private String gpuName;
    private double memoryUsedMB;
    private double memoryTotalMB;
    private double memoryUsagePercent;

    public double getUsagePercent() {
        return usagePercent;
    }

    public void setUsagePercent(double usagePercent) {
        this.usagePercent = usagePercent;
    }

    public String getGpuName() {
        return gpuName;
    }

    public void setGpuName(String gpuName) {
        this.gpuName = gpuName;
    }

    public double getMemoryUsedMB() {
        return memoryUsedMB;
    }

    public void setMemoryUsedMB(double memoryUsedMB) {
        this.memoryUsedMB = memoryUsedMB;
    }

    public double getMemoryTotalMB() {
        return memoryTotalMB;
    }

    public void setMemoryTotalMB(double memoryTotalMB) {
        this.memoryTotalMB = memoryTotalMB;
    }

    public double getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public void setMemoryUsagePercent(double memoryUsagePercent) {
        this.memoryUsagePercent = memoryUsagePercent;
    }
}
