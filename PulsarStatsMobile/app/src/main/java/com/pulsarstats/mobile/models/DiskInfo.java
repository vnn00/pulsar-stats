package com.pulsarstats.mobile.models;

import com.google.gson.annotations.SerializedName;

public class DiskInfo {
    @SerializedName(value = "driveName", alternate = {"DriveName"})
    private String driveName;

    @SerializedName(value = "totalGB", alternate = {"TotalGB"})
    private double totalGB;

    @SerializedName(value = "usedGB", alternate = {"UsedGB"})
    private double usedGB;

    @SerializedName(value = "freeGB", alternate = {"FreeGB"})
    private double freeGB;

    @SerializedName(value = "usagePercent", alternate = {"UsagePercent"})
    private double usagePercent;

    public String getDriveName() { return driveName; }
    public void setDriveName(String driveName) { this.driveName = driveName; }
    
    public double getTotalGB() { return totalGB; }
    public void setTotalGB(double totalGB) { this.totalGB = totalGB; }
    
    public double getUsedGB() { return usedGB; }
    public void setUsedGB(double usedGB) { this.usedGB = usedGB; }
    
    public double getFreeGB() { return freeGB; }
    public void setFreeGB(double freeGB) { this.freeGB = freeGB; }
    
    public double getUsagePercent() { return usagePercent; }
    public void setUsagePercent(double usagePercent) { this.usagePercent = usagePercent; }
}
