package com.pulsarstats.mobile.models;

import com.google.gson.annotations.SerializedName;

public class CpuInfo {
    @SerializedName(value = "usagePercent", alternate = {"UsagePercent"})
    private double usagePercent;

    @SerializedName(value = "frequency", alternate = {"Frequency", "FrequencyMHz"})
    private double frequency;

    @SerializedName(value = "coreUsages", alternate = {"CoreUsages"})
    private double[] coreUsages;

    public double getUsagePercent() { return usagePercent; }
    public void setUsagePercent(double usagePercent) { this.usagePercent = usagePercent; }
    
    public double getFrequency() { return frequency; }
    public void setFrequency(double frequency) { this.frequency = frequency; }
    
    public double[] getCoreUsages() { return coreUsages; }
    public void setCoreUsages(double[] coreUsages) { this.coreUsages = coreUsages; }
}
