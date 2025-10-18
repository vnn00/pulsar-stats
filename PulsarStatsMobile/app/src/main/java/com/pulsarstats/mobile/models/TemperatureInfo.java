package com.pulsarstats.mobile.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TemperatureInfo {
    @SerializedName(value = "cpuTemp", alternate = {"CpuTemp"})
    private Double cpuTemp;

    @SerializedName(value = "gpuTemp", alternate = {"GpuTemp"})
    private Double gpuTemp;

    @SerializedName(value = "motherboardTemp", alternate = {"MotherboardTemp"})
    private Double motherboardTemp;

    @SerializedName(value = "diskTemps", alternate = {"DiskTemps"})
    private List<DiskTemperature> diskTemps;

    public Double getCpuTemp() { return cpuTemp; }
    public void setCpuTemp(Double cpuTemp) { this.cpuTemp = cpuTemp; }
    
    public Double getGpuTemp() { return gpuTemp; }
    public void setGpuTemp(Double gpuTemp) { this.gpuTemp = gpuTemp; }
    
    public Double getMotherboardTemp() { return motherboardTemp; }
    public void setMotherboardTemp(Double motherboardTemp) { this.motherboardTemp = motherboardTemp; }
    
    public List<DiskTemperature> getDiskTemps() { return diskTemps; }
    public void setDiskTemps(List<DiskTemperature> diskTemps) { this.diskTemps = diskTemps; }
}
