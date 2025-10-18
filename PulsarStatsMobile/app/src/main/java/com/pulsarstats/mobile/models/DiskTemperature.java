package com.pulsarstats.mobile.models;

import com.google.gson.annotations.SerializedName;

public class DiskTemperature {
    @SerializedName(value = "diskName", alternate = {"DiskName"})
    private String diskName;

    @SerializedName(value = "temperature", alternate = {"Temperature"})
    private double temperature;

    public String getDiskName() { return diskName; }
    public void setDiskName(String diskName) { this.diskName = diskName; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
}
