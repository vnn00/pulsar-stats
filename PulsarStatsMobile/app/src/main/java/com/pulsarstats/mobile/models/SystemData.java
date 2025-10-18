package com.pulsarstats.mobile.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SystemData {
    @SerializedName(value = "cpuInfo", alternate = {"cpu", "Cpu", "CpuInfo"})
    private CpuInfo cpuInfo;

    @SerializedName(value = "memoryInfo", alternate = {"memory", "Memory", "MemoryInfo"})
    private MemoryInfo memoryInfo;

    @SerializedName(value = "diskInfo", alternate = {"disks", "Disks", "DiskInfo"})
    private List<DiskInfo> diskInfo;

    @SerializedName(value = "temperatureInfo", alternate = {"temperature", "Temperature", "TemperatureInfo"})
    private TemperatureInfo temperatureInfo;

    @SerializedName(value = "networkInfo", alternate = {"network", "Network", "NetworkInfo"})
    private NetworkInfo networkInfo;

    @SerializedName(value = "systemInfo", alternate = {"SystemInfo"})
    private SystemInfo systemInfo;

    @SerializedName(value = "gpuInfo", alternate = {"GpuInfo"})
    private GpuInfo gpuInfo;

    @SerializedName(value = "timestamp", alternate = {"Timestamp"})
    private String timestamp;

    public CpuInfo getCpuInfo() { return cpuInfo; }
    public void setCpuInfo(CpuInfo cpuInfo) { this.cpuInfo = cpuInfo; }
    
    public MemoryInfo getMemoryInfo() { return memoryInfo; }
    public void setMemoryInfo(MemoryInfo memoryInfo) { this.memoryInfo = memoryInfo; }
    
    public List<DiskInfo> getDiskInfo() { return diskInfo; }
    public void setDiskInfo(List<DiskInfo> diskInfo) { this.diskInfo = diskInfo; }
    
    public TemperatureInfo getTemperatureInfo() { return temperatureInfo; }
    public void setTemperatureInfo(TemperatureInfo temperatureInfo) { this.temperatureInfo = temperatureInfo; }
    
    public NetworkInfo getNetworkInfo() { return networkInfo; }
    public void setNetworkInfo(NetworkInfo networkInfo) { this.networkInfo = networkInfo; }
    
    public SystemInfo getSystemInfo() { return systemInfo; }
    public void setSystemInfo(SystemInfo systemInfo) { this.systemInfo = systemInfo; }
    
    public GpuInfo getGpuInfo() { return gpuInfo; }
    public void setGpuInfo(GpuInfo gpuInfo) { this.gpuInfo = gpuInfo; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
